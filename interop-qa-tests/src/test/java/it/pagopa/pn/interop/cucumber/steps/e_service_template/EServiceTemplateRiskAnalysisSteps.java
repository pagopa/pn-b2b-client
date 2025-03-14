package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

/** Cucumber steps involving risk analyses of E-service templates */
@Data
public class EServiceTemplateRiskAnalysisSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;

    public EServiceTemplateRiskAnalysisSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant,
        EServiceTemplateStepContext templateContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @Given("l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo")
    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully();
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template")
    public void addRiskAnalysisToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate();
    }

    @When("l'utente tenta la creazione di una risk analysis indicando una specifica vuota")
    public void addRiskAnalysisWithEmptySpecToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate(templateContext.getLastTemplateManaged().id(), new EServiceRiskAnalysisSeed());
    }

    @When("l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente")
    public void addRiskAnalysisToNonExistentEServiceTemplate() {
        EServiceRiskAnalysisSeed riskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        testAssistant.addRiskAnalysisToEServiceTemplate(UUID.randomUUID(), riskAnalysisSeed);
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome")
    public void addRiskAnalysisToEServiceTemplateWithSameName() {
        EServiceRiskAnalysisSeed sameNameRiskAnalysisSeed = easyRandom
            .nextObject(EServiceRiskAnalysisSeed.class)
            .name(templateContext.getLastAddedRiskAnalysis().getName());
        testAssistant.addRiskAnalysisToEServiceTemplate(templateContext.getLastTemplateManaged().id(), sameNameRiskAnalysisSeed);
    }

    @Then("l'aggiunta della risk analysis all'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisAddedToEServiceTemplate() {
        testAssistant.checkRiskAnalysisAddedToEServiceTemplate();
    }

    @Given("l'utente effettua la cancellazione della risk analysis dell'e-service template con successo")
    public void deleteRiskAnalysisFromEServiceTemplateSuccessfully() {
        deleteRiskAnalysisFromEServiceTemplate();
        checkRiskAnalysisDeletedFromEServiceTemplate();
    }

    @When("l'utente tenta la cancellazione della risk analysis dell'e-service template")
    public void deleteRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();

        UUID riskAnalysisId = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            eServiceTemplateId).getRiskAnalysis().get(0).getId();
        deleteRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId);
    }

    @When("l'utente tenta la cancellazione della risk analysis indicando un identificativo vuoto")
    public void deleteRiskAnalysisWithEmptyIdFromEServiceTemplate() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo della risk analysis
         * è una BAD_REQUEST annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi generato. */
        deleteRiskAnalysisFromEServiceTemplate(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la cancellazione di una risk analysis inesistente nell'e-service template")
    public void deleteNonExistentRiskAnalysisFromEServiceTemplate() {
        deleteRiskAnalysisFromEServiceTemplate(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
    }

    @Then("la cancellazione della risk analysis dell'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisDeletedFromEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && isEmpty(res.getBody().getRiskAnalysis()),
                "La risk analysis non è stata cancellata correttamente dall'e-service template, oppure l'e-service template risulta nullo."
            );
        } catch (PollingPredicateException e) {
            fail("La risk analysis non è stata cancellata correttamente dall'e-service template");
        }
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template")
    public void editRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();

        List<EServiceRiskAnalysis> riskAnalysis = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            eServiceTemplateId).getRiskAnalysis();
        if(isEmpty(riskAnalysis)) { // TODO aggiungere controlli simili anche nei passi di cancellazione risk analysis
            throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template");
        }

        UUID riskAnalysisId = riskAnalysis.get(templateContext.getLastAddedRiskAnalysisIndex()).getId();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId, editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template indicando una specifica vuota")
    public void editUnspecifiedRiskAnalysisFromEServiceTemplate() {

        // TODO modo inefficiente di reperire la risk analysis inserita: andrebbe memorizzato
        // l'id subito dopo la creazione, e quindi collocato in contesto di classe come per
        // gli altri id
        List<EServiceRiskAnalysis> riskAnalysis = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            templateContext.getLastTemplateManaged().id()).getRiskAnalysis();
        if(isEmpty(riskAnalysis)) { // TODO aggiungere controlli simili anche nei passi di cancellazione risk analysis
            throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template");
        }

        UUID riskAnalysisId = riskAnalysis.get(templateContext.getLastAddedRiskAnalysisIndex()).getId();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = new EServiceRiskAnalysisSeed();
        editRiskAnalysisFromEServiceTemplate(templateContext.getLastTemplateManaged().id(), riskAnalysisId, editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica di una risk analysis inesistente nell'e-service template")
    public void editNonExistentRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, UUID.randomUUID(), editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica di una risk analysis inserendo il nome di un'altra risk analysis")
    public void editRiskAnalysisFromEServiceTemplateWithSameName() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();

        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getRiskAnalysis().size() >= 2,
            "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 risk analysis nell'e-service template"
        );

        @SuppressWarnings("unchecked, DataFlowIssue")
        List<EServiceRiskAnalysis> riskAnalysis = ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()).getBody().getRiskAnalysis();

        UUID riskAnalysisId = riskAnalysis.get(0).getId();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class)
            .name(riskAnalysis.get(1).getName());
        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId, editedRiskAnalysisSeed);
    }


    @Then("la modifica della risk analysis dell'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisEditedFromEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && testAssistant.areConsistent(templateContext.getLastAddedRiskAnalysis(), res.getBody().getRiskAnalysis().get(templateContext.getLastAddedRiskAnalysisIndex())),
                "La risk analysis non è stata modificata correttamente nell'e-service template"
            );
        } catch (PollingPredicateException e) {

            // TODO altrove non si è stati così precisi nei messaggi di errore, adeguare

            List<EServiceRiskAnalysis> riskAnalysis = requireNonNull(
                requireNonNull(
                    ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()),
                    "La response HTTP è nulla, possibile errore silenzioso di comunicazione con interop")
                    .getBody(),
                "Il body della response HTTP è nullo, possibile errore silenzioso di comunicazione con interop o cambiamento dell'API"
            ).getRiskAnalysis();
            if(isEmpty(riskAnalysis)) {
                throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template, possibile uso errato di questo step o precedente inserimento di risk analysis non riuscito");
            }

            fail("La risk analysis non è stata modificata correttamente nell'e-service template: lo stato attuale è %s, quello atteso era %s", riskAnalysis.get(0), templateContext.getLastAddedRiskAnalysis());
        }
    }

    private void deleteRiskAnalysisFromEServiceTemplate(UUID eServiceTemplateId, UUID riskAnalysisId) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteRiskAnalysis(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                riskAnalysisId));
    }

    private void editRiskAnalysisFromEServiceTemplate(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed
    ) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.editRiskAnalysisWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                riskAnalysisId,
                editedRiskAnalysisSeed),

            /* TODO altrove non è stata usata questa variante del metodo che permette di conservare il codice di risposta originale,
             * modificare anche gli altri scenari così che si possa effettuare un check preciso dello status restituito
             */
            ResponseEntity::getStatusCode);
    }
}
