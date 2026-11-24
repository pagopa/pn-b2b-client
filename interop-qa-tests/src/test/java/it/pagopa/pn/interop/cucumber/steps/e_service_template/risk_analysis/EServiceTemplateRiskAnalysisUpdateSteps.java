package it.pagopa.pn.interop.cucumber.steps.e_service_template.risk_analysis;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.fail;

/** Cucumber steps involving risk analyses of E-service templates */
@Data
public class EServiceTemplateRiskAnalysisUpdateSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EasyRandom easyRandom;

    public EServiceTemplateRiskAnalysisUpdateSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateTestAssistant testAssistant
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.testAssistant = testAssistant;
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template")
    public void editRiskAnalysisFromEServiceTemplate() {
        editRiskAnalysisBySupplier(() -> testAssistant.getEServiceRiskAnalysisSeed(false));
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template indicandone una coerente con il tenant kind {string}")
    public void editRiskAnalysisFromEServiceTemplate(String tenantKind) {
        IdentityService identityService = sharedStepsContext.getIdentityService();
        String tenant = identityService.getTenantTypesByKind(tenantKind).stream()
                .findFirst().orElseThrow(() -> new IllegalStateException("Nessun tenant type trovato per il tenant kind " + tenantKind));
        editRiskAnalysisBySupplier(() -> testAssistant.getEServiceRiskAnalysisSeedWithType(tenant, true));
    }

    private void editRiskAnalysisBySupplier(Supplier<EServiceTemplateRiskAnalysisSeed> raSupplier) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID riskAnalysisId = sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysisId();

        EServiceTemplateRiskAnalysisSeed editedRiskAnalysisSeed = raSupplier.get();

        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId, editedRiskAnalysisSeed);
        sharedStepsContext.getEServiceTemplateStepContext().setLastAddedRiskAnalysis(editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template indicando una specifica vuota")
    public void editUnspecifiedRiskAnalysisFromEServiceTemplate() {

        // TODO modo inefficiente di reperire la risk analysis inserita: andrebbe memorizzato
        //  l'id subito dopo la creazione, e quindi collocato in contesto di classe come per
        //  gli altri id
        List<EServiceTemplateRiskAnalysis> riskAnalysis = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId()).getRiskAnalysis();
        if(isEmpty(riskAnalysis)) {
            throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template");
        }

        UUID riskAnalysisId = riskAnalysis.get(sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysisIndex()).getId();
        EServiceTemplateRiskAnalysisSeed editedRiskAnalysisSeed = new EServiceTemplateRiskAnalysisSeed();
        editRiskAnalysisFromEServiceTemplate(sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(), riskAnalysisId, editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica di una risk analysis inesistente nell'e-service template")
    public void editNonExistentRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();

        String tenantType = sharedStepsContext.getTenantType();
        String kind = sharedStepsContext.getIdentityService().getKind(tenantType);
        EServiceTemplateRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceTemplateRiskAnalysisSeed.class)
            .tenantKind(TenantKind.fromValue(kind));

        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, UUID.randomUUID(), editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica di una risk analysis inserendo il nome di un'altra risk analysis")
    public void editRiskAnalysisFromEServiceTemplateWithSameName() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();

        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                    eServiceTemplateId),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getRiskAnalysis().size() >= 2,
            "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 risk analysis nell'e-service template"
        );

        @SuppressWarnings("unchecked, DataFlowIssue")
        List<EServiceTemplateRiskAnalysis> riskAnalysis = ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()).getBody().getRiskAnalysis();

        UUID riskAnalysisId = riskAnalysis.get(0).getId();

        String tenantType = sharedStepsContext.getTenantType();
        String kind = sharedStepsContext.getIdentityService().getKind(tenantType);
        EServiceTemplateRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceTemplateRiskAnalysisSeed.class)
            .name(riskAnalysis.get(1).getName())
            .tenantKind(TenantKind.fromValue(kind));

        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId, editedRiskAnalysisSeed);
    }

    @Then("la modifica della risk analysis dell'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisEditedFromEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && testAssistant.areConsistent(sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysis(), res.getBody().getRiskAnalysis().get(sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysisIndex())),
                "La risk analysis non è stata modificata correttamente nell'e-service template"
            );
        } catch (PollingPredicateException e) {
            List<EServiceTemplateRiskAnalysis> riskAnalysis = requireNonNull(
                requireNonNull(
                    ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()),
                    "La response HTTP è nulla, possibile errore silenzioso di comunicazione con interop")
                    .getBody(),
                "Il body della response HTTP è nullo, possibile errore silenzioso di comunicazione con interop o cambiamento dell'API"
            ).getRiskAnalysis();
            if(isEmpty(riskAnalysis)) {
                throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template, possibile uso errato di questo step o precedente inserimento di risk analysis non riuscito");
            }

            fail("La risk analysis non è stata modificata correttamente nell'e-service template: lo stato attuale è %s, quello atteso era %s", riskAnalysis.get(0), sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysis());
        }
    }

    private void editRiskAnalysisFromEServiceTemplate(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceTemplateRiskAnalysisSeed editedRiskAnalysisSeed
    ) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.editRiskAnalysisWithHttpInfo(
                eServiceTemplateId,
                riskAnalysisId,
                editedRiskAnalysisSeed),

            /* TODO altrove non è stata usata questa variante del metodo che permette di conservare il codice di risposta originale,
             * modificare anche gli altri scenari così che si possa effettuare un check preciso dello status restituito
             */
            ResponseEntity::getStatusCode);
    }
}
