package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateSteps {


    /** Stores data on an e-service template useful for testing */
    record EServiceTemplateInfo(String name, UUID id, UUID lastVersionId){}

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    private EServiceTemplateInfo lastTemplateManaged;
    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;
    private EServiceRiskAnalysisSeed lastAddedRiskAnalysis;
    private int lastAddedRiskAnalysisIndex = -1; // -1 means no risk analysis has been added yet

    // TODO farne un bean centralizzato riutilizzabile ovunque
    private static EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
        .seed(123L)
        .objectPoolSize(20)
        .randomizationDepth(5)
        .charset(StandardCharsets.UTF_8)
        .stringLengthRange(5, 30)
        .collectionSizeRange(1, 10)
        .scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(true)
        .ignoreRandomizationErrors(false)
        .randomize(EServiceTemplateSteps::isAnswersFieldInRiskAnalysisFormSeed, EServiceTemplateSteps::randomAnswers);
    private static EasyRandom easyRandom = new EasyRandom(easyRandomParameters);

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    private static boolean isAnswersFieldInRiskAnalysisFormSeed(Field field) {
        return field.getName().equals("answers") && field.getDeclaringClass().equals(
            RiskAnalysisFormSeed.class);
    }

    private static Map<String, List<String>> randomAnswers() {
        int mapCapacity = 10;
        EasyRandom easyRandom = new EasyRandom();
        Map<String, List<String>> map = new HashMap<>(mapCapacity);
        for (int i = 0; i < mapCapacity; i++) {
            map.put(
                easyRandom.nextObject(String.class),
                easyRandom.objects(String.class, 5).toList());
        }

        return map;
    }

    @ParameterType("erogazione|ricezione")
    public EServiceMode eServiceMode(String mode) {
        return switch (mode) {
            case "erogazione"   -> EServiceMode.DELIVER;
            case "ricezione"    -> EServiceMode.RECEIVE;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                        EServiceMode.class.getSimpleName(),
                                        mode));
        };
    }

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED")
    public EServiceTemplateVersionState eServiceTemplateVersionState(String state) {
        return switch (state) {
            case "DRAFT"        -> EServiceTemplateVersionState.DRAFT;
            case "PUBLISHED"    -> EServiceTemplateVersionState.PUBLISHED;
            case "DEPRECATED"   -> EServiceTemplateVersionState.DEPRECATED;
            case "SUSPENDED"    -> EServiceTemplateVersionState.SUSPENDED;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                        EServiceTemplateVersionState.class.getSimpleName(),
                                        state));
        };
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode);
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState) {
        createEServiceTemplate(eServiceMode);
        switch (desiredState) {
            case DRAFT -> { /* no-op: un template appena creato è automaticamente in questo stato */ }
            case PUBLISHED -> publishEServiceTemplate();
            case SUSPENDED -> suspendEServiceTemplate();
            default -> throw new IllegalArgumentException("Stato non supportato: " + desiredState);
        }
    }

    @When("l'utente tenta delle modifiche all'e-service template")
    public void updateEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(lastTemplateManaged.name() + " - modificato")
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE)
            .isSignalHubEnabled(false);
        updateEServiceTemplate(eServiceTemplateId, lastTemplateUpdateSeed);
    }

    @Then("le modifiche al template sono state applicate correttamente")
    public void checkEServiceTemplateUpdate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        try {
            pollingService.makePolling(
                    () -> httpCallExecutor.performCall( // TODO è stata introdotta la API specifica per i template, refattorizzare usando quella (non solo qui) per i check che riguardano solo i template
                        () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                            sharedStepsContext.getXCorrelationId(),
                            eServiceTemplateId,
                            eServiceTemplateVersionId),
                        ResponseEntity::getStatusCode),
                    res -> nonNull(res.getBody()) && this.areConsistent(lastTemplateUpdateSeed, res.getBody().getEserviceTemplate()),
                    "L'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche all'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                lastTemplateUpdateSeed, httpCallExecutor.getResponse());
        }
    }

    @When("l'utente tenta di modificare l'e-service template specificando lo stesso nome")
    public void updateEServiceTemplateWithSameName() {
        UpdateEServiceTemplateSeed sameNameUpdateSeed = new UpdateEServiceTemplateSeed()
            .name(lastTemplateManaged.name())
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        UUID eServiceTemplateId = lastTemplateManaged.id();
        updateEServiceTemplate(eServiceTemplateId, sameNameUpdateSeed);
    }

    @When("l'utente tenta delle modifiche a un e-service template inesistente")
    public void updateNonExistentEServiceTemplate() {
        UUID eServiceTemplateId = UUID.randomUUID();
        UpdateEServiceTemplateSeed updateSeed = new UpdateEServiceTemplateSeed()
            .name("Nuovo nome")
            .audienceDescription("Nuova audience description")
            .eserviceDescription("Nuova descrizione del servizio")
            .technology(EServiceTechnology.SOAP)
            .mode(EServiceMode.RECEIVE);
        updateEServiceTemplate(eServiceTemplateId, updateSeed);
    }

    private void updateEServiceTemplate(UUID eServiceTemplateId, UpdateEServiceTemplateSeed sameNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplate(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                sameNameUpdateSeed));
    }

    private boolean areConsistent(UpdateEServiceTemplateSeed lastUpdate, EServiceTemplateDetails retrievedTemplate) {
        return lastUpdate.getName().equals(retrievedTemplate.getName()) &&
            lastUpdate.getAudienceDescription().equals(retrievedTemplate.getAudienceDescription()) &&
            lastUpdate.getEserviceDescription().equals(retrievedTemplate.getEserviceDescription()) &&
            lastUpdate.getTechnology().equals(retrievedTemplate.getTechnology()) &&
            lastUpdate.getMode().equals(retrievedTemplate.getMode());
    }

    @When("l'utente tenta delle modifiche alla versione dell'e-service template")
    public void updateEServiceTemplateVersion() {
        lastTemplateVersionUpdateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            //.attributes() <-- TODO costoso da implementare, rimandato
            .dailyCallsPerConsumer(100)
            .dailyCallsTotal(1000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(
            this.lastTemplateManaged.id(),
            this.lastTemplateManaged.lastVersionId(),
            lastTemplateVersionUpdateSeed);
    }

    @Then("le modifiche alla versione sono state applicate correttamente")
    public void checkEServiceTemplateVersionUpdate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res -> nonNull(res.getBody()) && this.areConsistent(lastTemplateVersionUpdateSeed, res.getBody()),
                "La versione dell'e-service template non corrisponde alle modifiche apportate"
            );
        } catch (PollingPredicateException e) {
            fail("Le modifiche alla versione dell'e-service template non sono state "
                    + "applicate correttamente: le modifiche apportate '%s' non sono compatibili con il risultato ricevuto '%s'",
                lastTemplateUpdateSeed, httpCallExecutor.getResponse());
        }
    }

    @When("l'utente tenta delle modifiche alla versione di un e-service template inesistente")
    public void updateNonExistentEServiceTemplateVersion() {
        UpdateEServiceTemplateVersionSeed updateSeed = new UpdateEServiceTemplateVersionSeed()
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
            //.attributes() <-- TODO costoso da implementare, rimandato
            .dailyCallsPerConsumer(500)
            .dailyCallsTotal(5000)
            .voucherLifespan(586400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), updateSeed);
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template")
    public void addRiskAnalysisToEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastAddedRiskAnalysis = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        lastAddedRiskAnalysisIndex++;
        addRiskAnalysisToEServiceTemplate(eServiceTemplateId, lastAddedRiskAnalysis);
    }

    private void addRiskAnalysisToEServiceTemplate(UUID eServiceTemplateId, EServiceRiskAnalysisSeed riskAnalysisSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addRiskAnalysis(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                riskAnalysisSeed));
    }

    @Then("l'aggiunta della risk analysis all'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisAddedToEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res ->
                    nonNull(res.getBody()) &&
                    this.areConsistent(lastAddedRiskAnalysis, res.getBody().getEserviceTemplate().getRiskAnalysis().get(lastAddedRiskAnalysisIndex)),
                "La risk analysis non è stata aggiunta correttamente all'e-service template"
            );
        } catch (PollingPredicateException e) {
            fail("La risk analysis non è stata aggiunta correttamente all'e-service template");
        }
    }

    @When("l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente")
    public void addRiskAnalysisToNonExistentEServiceTemplate() {
        EServiceRiskAnalysisSeed riskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        addRiskAnalysisToEServiceTemplate(UUID.randomUUID(), riskAnalysisSeed);
    }

    @Given("l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo")
    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        addRiskAnalysisToEServiceTemplate();
        checkRiskAnalysisAddedToEServiceTemplate();
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome")
    public void addRiskAnalysisToEServiceTemplateWithSameName() {
        EServiceRiskAnalysisSeed sameNameRiskAnalysisSeed = easyRandom
            .nextObject(EServiceRiskAnalysisSeed.class)
            .name(lastAddedRiskAnalysis.getName());
        addRiskAnalysisToEServiceTemplate(lastTemplateManaged.id(), sameNameRiskAnalysisSeed);
    }

    @When("l'utente tenta la cancellazione della risk analysis dell'e-service template")
    public void deleteRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();

        UUID riskAnalysisId = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            eServiceTemplateId).getRiskAnalysis().get(0).getId();
        deleteRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId);
    }

    @Then("la cancellazione della risk analysis dell'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisDeletedFromEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
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

    @When("l'utente tenta la cancellazione di una risk analysis inesistente nell'e-service template")
    public void deleteNonExistentRiskAnalysisFromEServiceTemplate() {
        deleteRiskAnalysisFromEServiceTemplate(lastTemplateManaged.id(), UUID.randomUUID());
    }

    @Given("l'utente effettua la cancellazione della risk analysis dell'e-service template con successo")
    public void deleteRiskAnalysisFromEServiceTemplateSuccessfully() {
        deleteRiskAnalysisFromEServiceTemplate();
        checkRiskAnalysisDeletedFromEServiceTemplate();
    }

    private void deleteRiskAnalysisFromEServiceTemplate(UUID eServiceTemplateId, UUID riskAnalysisId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteRiskAnalysis(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                riskAnalysisId));
    }

    private void updateEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UpdateEServiceTemplateVersionSeed sameNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersion(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                sameNameUpdateSeed));
    }

    @When("l'utente tenta la modifica della risk analysis dell'e-service template")
    public void editRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();

        List<EServiceRiskAnalysis> riskAnalysis = eServiceTemplateClient.getEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            eServiceTemplateId).getRiskAnalysis();
        if(isEmpty(riskAnalysis)) { // TODO aggiungere controlli simili anche nei passi di cancellazione risk analysis
            throw new IllegalStateException("Nessuna risk analysis presente nell'e-service template");
        }

        UUID riskAnalysisId = riskAnalysis.get(lastAddedRiskAnalysisIndex).getId();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, riskAnalysisId, editedRiskAnalysisSeed);
    }

    @Then("la modifica della risk analysis dell'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisEditedFromEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && this.areConsistent(lastAddedRiskAnalysis, res.getBody().getRiskAnalysis().get(lastAddedRiskAnalysisIndex)),
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

            fail("La risk analysis non è stata modificata correttamente nell'e-service template: lo stato attuale è %s, quello atteso era %s", riskAnalysis.get(0), lastAddedRiskAnalysis);
        }
    }

    @When("l'utente tenta la modifica di una risk analysis inesistente nell'e-service template")
    public void editNonExistentRiskAnalysisFromEServiceTemplate() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        editRiskAnalysisFromEServiceTemplate(eServiceTemplateId, UUID.randomUUID(), editedRiskAnalysisSeed);
    }

    @When("l'utente tenta la modifica di una risk analysis inserendo il nome di un'altra risk analysis")
    public void editRiskAnalysisFromEServiceTemplateWithSameName() {
        UUID eServiceTemplateId = lastTemplateManaged.id();

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

    private void editRiskAnalysisFromEServiceTemplate(
        UUID eServiceTemplateId,
        UUID riskAnalysisId,
        EServiceRiskAnalysisSeed editedRiskAnalysisSeed
    ) {
        String userToken = getUserToken();
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


    /* TODO un'alternativa all'uso di metodi come "areConsistent" - che confrontano i campi uno a uno - potrebbe essere
     * l'uso di una libreria di mapping, da usare per mappare un oggetto nell'altro tipo, e quindi procedere con
     * un normale equals(...).
     */

    // TODO diverse NPE possibili, agire di conseguenza
    private boolean areConsistent(UpdateEServiceTemplateVersionSeed lastUpdate, EServiceTemplateVersionDetails retrievedTemplate) {
        return //lastUpdate.getAttributes().equals(retrievedTemplate.getAttributes()) &&  <- TODO costoso da implementare, rimandato
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getAgreementApprovalPolicy().equals(retrievedTemplate.getAgreementApprovalPolicy()) &&
            lastUpdate.getVoucherLifespan().equals(retrievedTemplate.getVoucherLifespan()) &&
            lastUpdate.getDailyCallsTotal().equals(retrievedTemplate.getDailyCallsTotal()) &&
            lastUpdate.getDailyCallsPerConsumer().equals(retrievedTemplate.getDailyCallsPerConsumer());
    }

    private boolean areConsistent(EServiceRiskAnalysisSeed lastRiskAnalysis, EServiceRiskAnalysis retrievedAnalysis) {
        return lastRiskAnalysis.getName().equals(retrievedAnalysis.getName()) &&
            lastRiskAnalysis.getRiskAnalysisForm().equals(retrievedAnalysis.getRiskAnalysisForm());


        /* TODO retrievedAnalysis ha il campo "createdAt" che però è di tipo stringa: stando
         * a https://stackoverflow.com/questions/49379006/what-is-the-correct-way-to-declare-a-date-in-an-openapi-swagger-file#:~:text=In%20OpenAPI%2C%20the%20date-time%20format%20is%20used%20to,a%20breakdown%3A%20Regex%20for%20this%3A%20%5Ed%7B4%7D-d%7B2%7D-d%7B2%7DTd%7B2%7D%3Ad%7B2%7D%3Ad%7B2%7DZ%24%20CODE%20%22fmt%22
         * dovrebbe trattarsi dello standard ISO 8601; arricchire il test così da verificare anche questo dato
         */
    }

    /** Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        String templateName = String.format("eservice-template-%d-%d", sharedStepsContext.getTestSeed(), randomInt);
        VersionSeedForEServiceTemplateCreation version = new VersionSeedForEServiceTemplateCreation()
            .voucherLifespan(86400);
        return new EServiceTemplateSeed()
            .audienceDescription("Audience description per il template " + templateName)
            .name(templateName)
            .eserviceDescription("Descrizione del servizio associato al template " + templateName)
            .mode(eServiceMode)
            .version(version)
            .technology(EServiceTechnology.REST);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        CreatedEServiceTemplateVersion creationResponse = this.dataPreparationService.createEServiceTemplate(
            templateSeed);
        this.lastTemplateManaged = new EServiceTemplateInfo(
            templateSeed.getName(),
            creationResponse.getId(),
            creationResponse.getVersionId());
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} usando lo stesso nome")
    public void createEServiceTemplateWithSameName(EServiceMode eServiceMode) {
        String lastTemplateNameUsed = this.lastTemplateManaged.name();
        EServiceTemplateSeed sameNameTemplateSeed = this.getEServiceTemplateSeed(eServiceMode)
            .name(lastTemplateNameUsed);
        createEServiceTemplate(sameNameTemplateSeed);
    }

    @Then("l'e-service template è in stato di {eServiceTemplateVersionState}")
    public void checkEServiceTemplateState(EServiceTemplateVersionState expectedState) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        /* Attende qualora eventuali chiamate precedenti (creazione, pubblicazione, sospensine...)
         * non abbiano ancora completato il proprio corso */
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersion(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the e-service template"
        );

        EServiceTemplateVersionDetails retrievedTemplateVersion = (EServiceTemplateVersionDetails) this.httpCallExecutor.getResponse();
        EServiceTemplateVersionState actualState = retrievedTemplateVersion.getState();

        assertThat(actualState)
            .as("Lo stato dell'e-service template deve corrispondere a quanto atteso dal test")
            .isEqualTo(expectedState);
    }

    @When("l'utente effettua la pubblicazione dell'e-service template")
    public void publishEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.publishEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }

    @When("l'utente effettua la sospensione dell'e-service template")
    public void suspendEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.suspendEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }


    @When("l'utente effettua la riattivazione dell'e-service template")
    public void activateEServiceTemplate() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.activateEServiceTemplate(
            lastTemplateManaged.id(),
            lastTemplateManaged.lastVersionId());
    }

    private String getUserToken() {
        return requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
    }
}
