package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind.DOCUMENT;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.anyNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganization;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactOrganizations;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstance;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateInstances;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.InstanceEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplate;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceTemplates;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateInstanceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateTestAssistant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import org.assertj.core.api.Condition;
import org.jeasy.random.EasyRandom;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateSteps {
    /* TODO 13/03/2025 almeno alcuni di questi attributi resteranno inutilizzati dopo lo smistamento
     *  degli step in classi dedicate, rimuoverli */
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceClient eServiceClient;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EServiceTemplateTestAssistant testAssistant;
    private final EServiceTemplateStepContext templateContext;

    // TODO alcune di queste variabili andranno incapsulate in un bean di tipo Context
    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;
    private EServiceTemplateNameUpdateSeed lastTemplateNameUpdateSeed;
    private EServiceTemplateDescriptionUpdateSeed lastTemplateIntendedTargetUpdateSeed;
    private EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed;
    private EServiceTemplateVersionQuotasUpdateSeed lastTemplateVersionQuotasUpdateSeed;
    private DescriptorAttributesSeed lastAttributesUpdateSeed;
    private InstanceEServiceSeed lastEServiceCreatedFromTemplate;
    private UUID lastEServiceIdCreatedFromTemplate;
    private CompactDescriptor lastEServiceDescriptorCreatedFromTemplate;
    private UUID lastEServiceIdUpdatedFromTemplate;
    private UUID lastEServiceDescriptorIdUpdatedFromTemplate;
    private UpdateEServiceTemplateInstanceSeed lastUpdateEServiceTemplateInstanceSeed;
    private UpdateEServiceDescriptorTemplateInstanceSeed lastUpdateEServiceDescriptorTemplateInstanceSeed;
    
    private final EasyRandom easyRandom;

    public EServiceTemplateSteps(ClientTokenConfigurator clientTokenConfigurator,
                                DataPreparationService dataPreparationService,
                                SharedStepsContext sharedStepsContext,
                                DescriptorAttributesMapper descriptorAttributesMapper,
                                EServiceTemplateTestAssistant testAssistant,
                                EServiceTemplateStepContext templateContext
        ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.testAssistant = testAssistant;
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
    }

    @ParameterType("DOCUMENT|INTERFACE")
    public EServiceTemplateDocumentKind eServiceTemplateDocumentKind(String kind) {
        return switch (kind) {
            case "DOCUMENT"     -> DOCUMENT;
            case "INTERFACE"    -> EServiceTemplateDocumentKind.INTERFACE;
            default             -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                        EServiceTemplateDocumentKind.class.getSimpleName(),
                                        kind));
        };
    }

    @ParameterType("DRAFT|PUBLISHED|DEPRECATED|SUSPENDED|ARCHIVED|WAITING_FOR_APPROVAL")
    public EServiceDescriptorState eServiceDescriptorState(String state) {
        return switch (state) {
            case "DRAFT"                -> EServiceDescriptorState.DRAFT;
            case "PUBLISHED"            -> EServiceDescriptorState.PUBLISHED;
            case "DEPRECATED"           -> EServiceDescriptorState.DEPRECATED;
            case "SUSPENDED"            -> EServiceDescriptorState.SUSPENDED;
            case "ARCHIVED"             -> EServiceDescriptorState.ARCHIVED;
            case "WAITING_FOR_APPROVAL" -> EServiceDescriptorState.WAITING_FOR_APPROVAL;
            default                     -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                                EServiceDescriptorState.class.getSimpleName(),
                                                state));
        };
    }

    private DescriptorAttributesSeed nextDescriptorAttributesSeed() {
        return descriptorAttributesMapper.mapSeedsToSeeds(testAssistant.nextAttributesSeed());
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template")
    public void addRiskAnalysisToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate();
    }

    @When("l'utente tenta la creazione di una risk analysis indicando una specifica vuota")
    public void addRiskAnalysisWithEmptySpecToEServiceTemplate() {
        testAssistant.addRiskAnalysisToEServiceTemplate(templateContext.getLastTemplateManaged().id(), new EServiceRiskAnalysisSeed());
    }

    @Then("l'aggiunta della risk analysis all'e-service è stata effettuata correttamente")
    public void checkRiskAnalysisAddedToEServiceTemplate() {
        testAssistant.checkRiskAnalysisAddedToEServiceTemplate();
    }

    @When("l'utente tenta l'aggiunta di una risk analysis a un e-service template inesistente")
    public void addRiskAnalysisToNonExistentEServiceTemplate() {
        EServiceRiskAnalysisSeed riskAnalysisSeed = easyRandom.nextObject(EServiceRiskAnalysisSeed.class);
        testAssistant.addRiskAnalysisToEServiceTemplate(UUID.randomUUID(), riskAnalysisSeed);
    }

    @Given("l'utente effettua l'aggiunta di una risk analysis all'e-service template con successo")
    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        testAssistant.addRiskAnalysisToEServiceTemplateSuccessfully();
    }

    @When("l'utente tenta l'aggiunta di una risk analysis all'e-service template specificando lo stesso nome")
    public void addRiskAnalysisToEServiceTemplateWithSameName() {
        EServiceRiskAnalysisSeed sameNameRiskAnalysisSeed = easyRandom
            .nextObject(EServiceRiskAnalysisSeed.class)
            .name(templateContext.getLastAddedRiskAnalysis().getName());
        testAssistant.addRiskAnalysisToEServiceTemplate(templateContext.getLastTemplateManaged().id(), sameNameRiskAnalysisSeed);
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

    @When("l'utente tenta la cancellazione di una risk analysis inesistente nell'e-service template")
    public void deleteNonExistentRiskAnalysisFromEServiceTemplate() {
        deleteRiskAnalysisFromEServiceTemplate(templateContext.getLastTemplateManaged().id(), UUID.randomUUID());
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

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template")
    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(kind);
    }

    // WIP
    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template specificando un contenuto vuoto")
    public void addUnspecifiedDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        ByteArrayResource emptyByteArray = new ByteArrayResource(new byte[]{});
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, emptyByteArray);
    }

    @Then("l'aggiunta del documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template è stata effettuata correttamente")
    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.checkDocumentAddedToEServiceTemplateVersion(kind);
    }

    @Given("l'utente effettua l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template con successo")
    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersionSuccessfully(kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template specificando lo stesso nome")
    public void addDocumentToEServiceTemplateVersionWithSameName(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        testAssistant.addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, templateContext.getLastAddedDocument().prettyName());
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a un e-service template inesistente")
    public void addDocumentToNonExistentEServiceTemplate(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a una versione inesistente dell'e-service template")
    public void addDocumentToNonExistentEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        testAssistant.addDocumentToEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), kind);
    }

    private void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, Resource resource) {
        testAssistant.addDocumentToEserviceTemplateVersion(
            eServiceTemplateId,
            eServiceTemplateVersionId,
            kind,
            testAssistant.buildPrettyName(kind),
            getUserToken(),
            resource);
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template")
    public void getDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, null);
    }

    @When("l'utente tenta il reperimento di un documento da un e-service template inesistente")
    public void getDocumentFromNonExistentEServiceTemplate() {
        getDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template")
    public void getNonExistentDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, UUID.randomUUID());
    }

    private void getDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la modifica del documento dell'e-service template")
    public void editDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        lastDocumentUpdateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class);
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id(), lastDocumentUpdateSeed);
    }

    @When("l'utente tenta la modifica del documento dell'e-service template indicando una specifica vuota")
    public void editDocumentWithEmptySpecFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, templateContext.getLastAddedDocument().id(), new UpdateEServiceTemplateVersionDocumentSeed());
    }

    @Then("la modifica del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentEditedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();

        try {
            pollingService.makePolling(
                    // 05/03/2025 Viene chiamata sola questa API perché l'unica che contiene info utili per la verifica della modifica effettuata
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        Optional<EServiceDoc> foundDoc = res.getBody().getDocs().stream()
                            .filter(d -> d.getId().equals(documentId)).findFirst();
                        return foundDoc.isPresent() && this.areConsistent(lastDocumentUpdateSeed, foundDoc.get());
                    }
                    return false;
                },
                "Lo stato del documento restituito dalla API GET dei documenti non corrisponde a quello atteso"
            );
        } catch (PollingPredicateException e) {
            fail("Il documento non è stato modificato correttamente dalla versione dell'e-service template: " + e.getMessage());
        }
    }

    @When("l'utente tenta la modifica di un documento da un e-service template inesistente")
    public void editDocumentFromNonExistentEServiceTemplate() {
        editDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica del documento da una versione inesistente dell'e-service template")
    public void editDocumentFromNonExistentEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), templateContext.getLastAddedDocument().id(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inesistente nell'e-service template")
    public void editNonExistentDocumentFromEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inserendo il nome di un altro documento")
    public void editDocumentFromEServiceTemplateVersionWithSameName() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                ResponseEntity::getStatusCode),
            res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getDocs().size() >= 2,
            "Condizioni di polling non rispettate. NOTA: questo step prevede l'esistenza di almeno 2 documenti nell'e-service template"
        );

        @SuppressWarnings("unchecked, DataFlowIssue")
        List<EServiceDoc> docs = ((ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse()).getBody().getDocs();

        UUID documentId = docs.get(0).getId();
        UpdateEServiceTemplateVersionDocumentSeed updateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class)
            .prettyName(docs.get(1).getPrettyName());
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId, updateSeed);
    }

    private void editDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId, UUID documentId, UpdateEServiceTemplateVersionDocumentSeed seed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId,
                seed),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la cancellazione del documento dell'e-service template")
    public void deleteDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();
        deleteDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @When("l'utente tenta la cancellazione del documento dell'e-service template indicando un identificato vuoto")
    public void deleteUnspecifiedDocumentFromEServiceTemplateVersion() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        deleteDocumentFromEServiceTemplateVersion(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            null);
    }

    @Then("la cancellazione del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentDeletedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        UUID documentId = templateContext.getLastAddedDocument().id();

        try {
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return res.getBody().getDocs().stream().noneMatch(d -> d.getId().equals(documentId));
                    }
                    return false;
                },
                "Il documento risulta ancora presente nell'e-service template"
            );
        } catch (PollingPredicateException e) {
            fail("Il documento non è stato cancellato correttamente dalla versione dell'e-service template: " + e.getMessage());
        }
    }

    @Given("l'utente effettua la cancellazione del documento dall'e-service template con successo")
    public void deleteDocumentFromEServiceTemplateVersionSuccessfully() {
        deleteDocumentFromEServiceTemplateVersion();
        checkDocumentDeletedFromEServiceTemplateVersion();
    }

    @When("l'utente tenta la cancellazione di un documento inesistente nell'e-service template")
    public void deleteNonExistentDocumentFromEServiceTemplateVersion() {
        deleteDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione del documento da una versione inesistente nell'e-service template")
    public void deleteDocumentFromNonExistentEServiceTemplateVersion() {
        deleteDocumentFromEServiceTemplateVersion(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), templateContext.getLastAddedDocument().id());
    }

    @When("l'utente tenta la cancellazione di un documento da un e-service template inesistente")
    public void deleteDocumentFromNonExistentEServiceTemplate() {
        deleteDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    private void deleteDocumentFromEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, UUID documentId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                documentId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la modifica del nome dell'e-service template")
    public void editEServiceTemplateName() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateNameUpdateSeed = easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class);
        editEServiceTemplateName(eServiceTemplateId, lastTemplateNameUpdateSeed);
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando lo stesso nome")
    public void editEServiceTemplateNameWithSameName() {
        editEServiceTemplateNameWith(templateContext.getLastTemplateManaged().name());
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateNameWithEmptyName() {
        editEServiceTemplateNameWith("");
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando NULL")
    public void editEServiceTemplateNameWithNullName() {
        editEServiceTemplateNameWith(null);
    }

    @When("l'utente tenta la modifica del nome di un e-service template inesistente")
    public void editNonExistentEServiceTemplateName() {
        editEServiceTemplateName(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class));
    }

    private void editEServiceTemplateNameWith(String name) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateNameUpdateSeed = easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class)
            .name(name);
        editEServiceTemplateName(eServiceTemplateId, lastTemplateNameUpdateSeed);
    }

    private void editEServiceTemplateName(UUID eServiceTemplateId,
        EServiceTemplateNameUpdateSeed lastTemplateNameUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateNameWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateNameUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica del nome dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateNameEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return this.areConsistent(res.getBody(), lastTemplateNameUpdateSeed);
                    }
                    return false;
                },
                "Il nome dell'e-service template non è stato modificato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Il nome dell'e-service template non è stato modificato correttamente");
        }
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template")
    public void editEServiceTemplateIntendedTarget() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateIntendedTargetUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateIntendedTarget(eServiceTemplateId, lastTemplateIntendedTargetUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateIntendedTargetWithSameIntendedTarget() {
        editEServiceTemplateIntendedTargetWith(templateContext.getLastTemplateManaged().intendedTarget());
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateIntendedTargetWith() {
        editEServiceTemplateIntendedTargetWith("");
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando NULL")
    public void editEServiceTemplateIntendedTargetWithNullIntendedTarget() {
        editEServiceTemplateIntendedTargetWith(null);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo di un e-service template inesistente")
    public void editNonExistentEServiceTemplateIntendedTarget() {
        editEServiceTemplateIntendedTarget(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class));
    }

    private void editEServiceTemplateIntendedTargetWith(String description) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateIntendedTargetUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class)
            .description(description);
        editEServiceTemplateIntendedTarget(eServiceTemplateId, lastTemplateIntendedTargetUpdateSeed);
    }

    private void editEServiceTemplateIntendedTarget(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed lastTemplateIntendedTargetUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceIntendedTargetWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateIntendedTargetUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica della descrizione dello scopo dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateIntendedTargetEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateDetails template = res.getBody();
                        return template.getIntendedTarget().equals(
                            lastTemplateIntendedTargetUpdateSeed.getDescription());
                    }
                    return false;
                },
                "La descrizione dello scopo dell'e-service template non è stata modificata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La descrizione dello scopo dell'e-service template non è stata modificata correttamente");
        }
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template")
    public void editEServiceTemplateDescription() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateDescriptionWithSameDescription() {
        editEServiceTemplateDescriptionWith(templateContext.getLastTemplateManaged().eServiceDescription());
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateDescriptionWith() {
        editEServiceTemplateDescriptionWith("");
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando NULL")
    public void editEServiceTemplateDescriptionWithNullDescription() {
        editEServiceTemplateDescriptionWith(null);
    }

    @When("l'utente tenta la modifica della descrizione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateDescription() {
        editEServiceTemplateDescription(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class));
    }

    private void editEServiceTemplateDescriptionWith(String description) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class)
            .description(description);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    private void editEServiceTemplateDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateDescriptionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateDescriptionUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica della descrizione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateDescriptionEdited() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateDetails template = res.getBody();
                        return template.getDescription().equals(
                            lastTemplateDescriptionUpdateSeed.getDescription());
                    }
                    return false;
                },
                "La descrizione dell'e-service template non è stata modificata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La descrizione dell'e-service template non è stata modificata correttamente");
        }
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template")
    public void editEServiceTemplateVersionQuotas() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        lastTemplateVersionQuotasUpdateSeed = easyRandom.nextObject(
            EServiceTemplateVersionQuotasUpdateSeed.class);
        lastTemplateVersionQuotasUpdateSeed.setVoucherLifespan(abs(lastTemplateVersionQuotasUpdateSeed.getVoucherLifespan()));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsTotal(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer() + 1));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsPerConsumer(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer()));

        editEServiceTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, lastTemplateVersionQuotasUpdateSeed);
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateVersionQuotasWithEmptySpec() {
        editEServiceTemplateVersionQuotas(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            new EServiceTemplateVersionQuotasUpdateSeed());
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template specificando un \"dailyCallsTotal\" inferiore a \"dailyCallsPerConsumer\"")
    public void editEServiceTemplateVersionQuotasWithDailyCallsTotalLessThanDailyCallsPerConsumer() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        lastTemplateVersionQuotasUpdateSeed = easyRandom.nextObject(
            EServiceTemplateVersionQuotasUpdateSeed.class);
        lastTemplateVersionQuotasUpdateSeed.setVoucherLifespan(abs(lastTemplateVersionQuotasUpdateSeed.getVoucherLifespan()));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsTotal(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer() - 1));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsPerConsumer(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer()));

        editEServiceTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, lastTemplateVersionQuotasUpdateSeed);
    }

    @When("l'utente tenta la modifica delle quote della versione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateVersionQuotas() {
        editEServiceTemplateVersionQuotas(UUID.randomUUID(), UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateVersionQuotasUpdateSeed.class));
    }

    @When("l'utente tenta la modifica delle quote di una versione inesistente dell'e-service template")
    public void editEServiceTemplateNonExistentVersionQuotas() {
        editEServiceTemplateVersionQuotas(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateVersionQuotasUpdateSeed.class));
    }

    private void editEServiceTemplateVersionQuotas(UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateVersionQuotasUpdateSeed lastTemplateVersionQuotasUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionQuotasWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastTemplateVersionQuotasUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica delle quote della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionQuotasEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall( // TODO usare la versione invece del template
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id(),
                        templateContext.getLastTemplateManaged().lastVersionId()),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceTemplateVersionDetails version = res.getBody();
                        return this.areConsistent(version, lastTemplateVersionQuotasUpdateSeed);
                    }
                    return false;
                },
                "Le quote dell'e-service template non sono state modificate correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Le quote dell'e-service template non sono state modificate correttamente");
        }
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template")
    public void editEServiceTemplateVersionAttributes() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        //lastTemplateVersionUpdateSeed contiene, tra le altre, cose, gli attributi aggiunti l'ultima volta
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(this.templateContext.getLastTemplateVersionUpdateSeed().getAttributes());

        Boolean explicitAttributeVerification = lastAttributesUpdateSeed.getCertified().get(0)
            .get(0).getExplicitAttributeVerification();
        lastAttributesUpdateSeed.getCertified().get(0).get(0).setExplicitAttributeVerification(!explicitAttributeVerification); // modifico 1 campo di 1 attributo
        editEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, lastAttributesUpdateSeed);
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateVersionAttributesWithEmptySpec() {
        editEServiceTemplateVersionAttributes(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId(),
            new DescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template aggiungendone di nuovi")
    public void editEServiceTemplateVersionAttributesAddingNew() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.mapSeedsToSeeds(this.templateContext.getLastTemplateVersionUpdateSeed().getAttributes());
        DescriptorAttributeSeed newAttribute = easyRandom.nextObject(DescriptorAttributeSeed.class);
        lastAttributesUpdateSeed.getCertified().add(List.of(newAttribute));
        editEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, lastAttributesUpdateSeed);
    }

    @When("l'utente tenta la modifica degli attributi della versione di un e-service template inesistente")
    public void editNonExistentEServiceTemplateVersionAttributes() {
        editEServiceTemplateVersionAttributes(UUID.randomUUID(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @When("l'utente tenta la modifica degli attributi di una versione inesistente dell'e-service template")
    public void editEServiceTemplateNonExistentVersionAttributes() {
        editEServiceTemplateVersionAttributes(templateContext.getLastTemplateManaged().id(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @Then("la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionAttributesEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id(),
                        templateContext.getLastTemplateManaged().lastVersionId()),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        DescriptorAttributes retrievedAttributes = res.getBody().getAttributes();
                        DescriptorAttributesSeed retrievedAttributesSeed = this.descriptorAttributesMapper.map(retrievedAttributes);
                        return retrievedAttributesSeed.equals(lastAttributesUpdateSeed);
                    }
                    return false;
                },
                "Gli attributi della versione dell'e-service template non sono stati modificati correttamente"
            );
        } catch (PollingPredicateException e) {
            // TODO occorrerebbero più dettagli, sul modello di quelli dati solitamente in automatico da AssertJ
            fail("Gli attributi della versione dell'e-service template non sono stati modificati correttamente");
        }
    }

    private void editEServiceTemplateVersionAttributes(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, DescriptorAttributesSeed lastAttributesUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateVersionAttributesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                lastAttributesUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la visualizzazione del catalogo degli e-service template")
    public void getEServiceTemplatesCatalog() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplatesCatalog(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("il catalogo degli e-service template contiene esattamente {int} elementi tutti in stato {eServiceTemplateVersionState}")
    public void checkEServiceTemplatesCatalogContainsElementsInState(int expectedCount, EServiceTemplateVersionState expectedState) {
        /* TODO la precondizione di questo metodo sarebbe che lo status code sia positivo, che il body non sia null e che il catalogo non sia vuoto.
         * Migliorare questo e altri step così che venga sempre fatto un check preventivo, eventualmente aiutandosi
         * con un framework con le Precondition come Google Guava. Spunti: https://www.sw-engineering-candies.com/blog-1/comparison-of-ways-to-check-preconditions-in-java
         */
        CatalogEServiceTemplates catalog = ((ResponseEntity<CatalogEServiceTemplates>) httpCallExecutor.getResponse()).getBody();
        List<CatalogEServiceTemplate> templatesInCatalog = catalog.getResults();

        Condition<CatalogEServiceTemplate> published = new Condition<>(
            template -> template.getPublishedVersion().getState() == expectedState,
            "of state %s", expectedState);
        assertThat(templatesInCatalog)
            .hasSize(expectedCount)
            .are(published);
    }

    @Then("il catalogo degli e-service template è vuoto")
    public void checkEServiceTemplatesCatalogIsEmpty() {
        CatalogEServiceTemplates catalog = ((ResponseEntity<CatalogEServiceTemplates>) httpCallExecutor.getResponse()).getBody();
        List<CatalogEServiceTemplate> templatesInCatalog = catalog.getResults();
        assertThat(templatesInCatalog).isEmpty();
    }

    @Then("i dettagli dell'e-service template contengono esattamente {int} versioni")
    public void checkEServiceTemplateDetailsContainVersions(int expectedVersionCount) {
        EServiceTemplateDetails template = ((ResponseEntity<EServiceTemplateDetails>) httpCallExecutor.getResponse()).getBody();
        assertThat(template.getVersions()).hasSize(expectedVersionCount);
    }

    @When("l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template")
    public void getEServiceTemplateVersionDetails() {
        getEServiceTemplateVersionDetails(templateContext.getLastTemplateManaged().id(), templateContext.getLastTemplateManaged().lastVersionId());
    }

    @When("l'utente tenta la visualizzazione dei dettagli della versione dell'e-service template indicando un identificativo vuoto")
    public void getUnspecifiedEServiceTemplateVersionDetails() {
        /* DEV. NOTE 11/03/2025: il passaggio di NULL come identificativo è una BAD_REQUEST
         * annunciata, in quanto è il comportamento di default del client OpenApi
         * generato. Ciò implica che la chiamata non raggiungerà mai il server. Non è stato
         * trovato un modo per passare stringa vuota senza bypassare il client OpenApi. */
        getEServiceTemplateVersionDetails(templateContext.getLastTemplateManaged().id(), null);
    }

    @When("l'utente tenta la visualizzazione dei dettagli di una versione di un e-service template inesistente")
    public void getNonExistentEServiceTemplateVersionDetails() {
        getEServiceTemplateVersionDetails(UUID.randomUUID(), UUID.randomUUID());
    }

    private void getEServiceTemplateVersionDetails(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la visualizzazione dell'elenco producers degli e-service templates")
    public void getEServiceTemplatesProducers() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getCreatorEServiceTemplates(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("l'elenco producers degli e-service templates contiene esattamente {int} elementi")
    public void checkEServiceTemplatesProducersCount(int expectedCount) {
        List<ProducerEServiceTemplate> producers = ((ResponseEntity<ProducerEServiceTemplates>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(producers).hasSize(expectedCount);
    }

    @When("l'utente tenta la visualizzazione dell'elenco dei creatori di e-service templates attivi")
    public void getActiveEServiceTemplatesCreators() {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.getEServiceTemplateCreators(sharedStepsContext.getXCorrelationId()),
            ResponseEntity::getStatusCode);
    }

    @Then("l'unico ente presente nell'elenco dei creatori di e-service templates attivi è {string}")
    public void checkActiveEServiceTemplatesCreators(String tenant) {
        List<CompactOrganization> creators = ((ResponseEntity<CompactOrganizations>) httpCallExecutor.getResponse()).getBody().getResults();
        assertThat(creators)
            .hasSize(1)
            .first()
            .extracting(CompactOrganization::getName)
            .isEqualTo(tenant);
    }

    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpec() {
        createEServiceFromTemplate(templateContext.getLastTemplateManaged().id(), null);
    }


    @When("l'utente tenta la creazione di un nuovo e-service a partire dal template indicando tutte le specifiche")
    public void createEServiceFromTemplateFullSpec() {
        InstanceEServiceSeed seed = easyRandom.nextObject(InstanceEServiceSeed.class);
        createEServiceFromTemplate(templateContext.getLastTemplateManaged().id(), seed);
    }

    @When("l'utente tenta la creazione di un nuovo e-service indicando un template inesistente")
    public void createEServiceFromNonExistentTemplate() {
        createEServiceFromTemplate(UUID.randomUUID(), null);
    }

    private void createEServiceFromTemplate(UUID id, InstanceEServiceSeed seed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.createEServiceInstanceFromTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                id,
                seed),
            ResponseEntity::getStatusCode);

        this.lastEServiceIdCreatedFromTemplate = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody().getId();
        this.lastEServiceCreatedFromTemplate = seed;
    }

    @Given("l'utente effettua la creazione di un nuovo e-service a partire dal template con successo indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpecSuccessfully() {
        createEServiceFromTemplateMinimalSpec();
        checkEServiceCreated(EServiceDescriptorState.DRAFT);
    }

    // TODO il passo precedente è un sottoinsieme di questo, accorpare per ridurre ambiguità
    // TODO aggiungere una virgola: "[...] a partire dal template con successo, indicando [...]"
    @Given("l'utente effettua la creazione di un nuovo e-service in stato {eServiceDescriptorState} a partire dal template con successo indicando solo le specifiche strettamente necessarie")
    public void createEServiceFromTemplateMinimalSpecSuccessfully(EServiceDescriptorState expectedState) {
        createEServiceFromTemplateMinimalSpec();
        checkEServiceCreated(EServiceDescriptorState.DRAFT);

        if(anyNull(lastEServiceIdCreatedFromTemplate, lastEServiceDescriptorCreatedFromTemplate)) {
            throw new IllegalStateException(("Una o più precondizioni necessarie al mutamento di "
                + "stato dell'e-service non sono rispettate: eServiceId = %s, eServiceDescriptor = %s")
                .formatted(lastEServiceIdCreatedFromTemplate, lastEServiceDescriptorCreatedFromTemplate));
        }
        this.dataPreparationService.bringDescriptorToGivenState(
            lastEServiceIdCreatedFromTemplate,
            lastEServiceDescriptorCreatedFromTemplate.getId(),
            expectedState,
            false);
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze dell'e-service template")
    public void getEServiceTemplateInstances() {
        getEserviceTemplateInstances(templateContext.getLastTemplateManaged().id());
    }

    @When("l'utente tenta la visualizzazione dell'elenco di tutte le istanze di un e-service template inesistente")
    public void getNotExistentEServiceTemplateInstances() {
        getEserviceTemplateInstances(UUID.randomUUID());
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template")
    public void editEServiceTemplateInstanceDescriptor() {
        UUID eServiceTemplateInstanceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceTemplateInstanceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();

        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceTemplateInstanceId, eServiceTemplateInstanceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica di un descriptor inesistente dell'istanza dell'e-service template")
    public void editNonExistentEServiceTemplateInstanceDescriptor() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceDescriptorId = UUID.randomUUID();
        lastUpdateEServiceDescriptorTemplateInstanceSeed = easyRandom.nextObject(UpdateEServiceDescriptorTemplateInstanceSeed.class);
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, lastUpdateEServiceDescriptorTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica del descriptor dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceTemplateInstanceDescriptorWithEmptySpec() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();
        UpdateEServiceDescriptorTemplateInstanceSeed emptySeed = new UpdateEServiceDescriptorTemplateInstanceSeed();
        editEServiceTemplateInstanceDescriptor(eServiceId, eServiceDescriptorId, emptySeed);
    }

    private void editEServiceTemplateInstanceDescriptor(
        UUID eServiceId,
        UUID eServiceDescriptorId,
        UpdateEServiceDescriptorTemplateInstanceSeed seed
    ) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
          httpCallExecutor.performCall(
                () -> eServiceClient.updateDraftDescriptorTemplateInstanceWithHttpInfo(
                 sharedStepsContext.getXCorrelationId(),
                 eServiceId,
                 eServiceDescriptorId,
                 seed),
                ResponseEntity::getStatusCode);
    }

    @Given("l'utente effettua l'aggiunta di una versione in stato {eServiceDescriptorState} all'e-service con successo")
    public void createEServiceVersionDraftSuccessfully(EServiceDescriptorState descriptorState) {
        UUID newDescriptor = this.dataPreparationService.createNextDraftDescriptor(
            lastEServiceIdCreatedFromTemplate);
        this.dataPreparationService.bringDescriptorToGivenState(
            lastEServiceIdCreatedFromTemplate,
            newDescriptor,
            descriptorState,
            false
        );
    }

    @Then("il descriptor dell'istanza dell'e-service template è stato modificato correttamente")
    public void checkEServiceTemplateInstanceDescriptorEdited() {
        UUID eServiceTemplateInstanceId = lastEServiceIdCreatedFromTemplate;
        UUID eServiceTemplateInstanceDescriptorId = lastEServiceDescriptorCreatedFromTemplate.getId();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateInstanceId,
                        eServiceTemplateInstanceDescriptorId),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return this.areConsistent(res.getBody(), lastUpdateEServiceDescriptorTemplateInstanceSeed);
                    }
                    return false;
                },
                "Il descriptor dell'istanza dell'e-service template non è stato modificato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("Il descriptor dell'istanza dell'e-service template non è stato modificato correttamente");
        }
    }

    private boolean areConsistent(ProducerEServiceDescriptor descriptor, UpdateEServiceDescriptorTemplateInstanceSeed seed) {
        return seed.getAudience().equals(descriptor.getAudience()) &&
            seed.getAgreementApprovalPolicy().equals(descriptor.getAgreementApprovalPolicy()) &&
            seed.getDailyCallsPerConsumer().equals(descriptor.getDailyCallsPerConsumer()) &&
            seed.getDailyCallsTotal().equals(descriptor.getDailyCallsTotal());
    }

    private void getEserviceTemplateInstances(UUID templateId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateId
            ),
            ResponseEntity::getStatusCode);
    }

    @Then("sono state visualizzate {int} istanza in stato DRAFT, {int} in stato PUBLISHED e {int} in stato SUSPENDED")
    public void checkEServiceTemplateInstancesCount(int draftCount, int publishedCount, int suspendedCount) {
        List<EServiceTemplateInstance> response = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody().getResults();
        assertSoftly(softly -> {
            softly.assertThat(response)
                .areExactly(
                    draftCount,
                    instanceInState(EServiceDescriptorState.DRAFT));
            softly.assertThat(response)
                .areExactly(
                    publishedCount,
                    instanceInState(EServiceDescriptorState.PUBLISHED));
            softly.assertThat(response)
                .areExactly(
                    suspendedCount,
                    instanceInState(EServiceDescriptorState.SUSPENDED));
        });
    }

    private Condition<EServiceTemplateInstance> instanceInState(EServiceDescriptorState state) {
        return new Condition<>(
            instance -> instance.getActiveDescriptor().getState().equals(state),
            "instances in state %s", state);
    }

    @Then("il nuovo e-service è stato creato correttamente in stato {eServiceDescriptorState}")
    public void checkEServiceCreated(EServiceDescriptorState expectedState) {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id()
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && !res.getBody().getResults().isEmpty(),
                "Il nuovo e-service non è stato creato correttamente"
            );

            EServiceTemplateVersionDetails eServiceSourceTemplate = this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateContext.getLastTemplateManaged().id(),
                templateContext.getLastTemplateManaged().lastVersionId()).getBody();
            Optional<EServiceTemplateInstance> eServiceCreatedFromTemplate = ((ResponseEntity<EServiceTemplateInstances>) httpCallExecutor.getResponse()).getBody()
                .getResults()
                .stream()
                .filter(instance -> instance.getId().equals(lastEServiceIdCreatedFromTemplate))
                .findAny();

            assertSoftly(softly -> {
                softly.assertThat(eServiceCreatedFromTemplate)
                    .as("Check esistenza istanza del template")
                    .withFailMessage("Fra le istanze del template non è presente quella appena creata. E' possibile sia avvenuto un errore a monte in fase di creazione dell'istanza, oppure a valle in fase di reperimento delle stesse.")
                    .isPresent();

                if(eServiceCreatedFromTemplate.get().getDescriptors().size() != 1) {
                    throw new IllegalStateException("L'e-service appena creato ha più di un descriptor: ciò rende incerto quale descriptor considerare per le successive operazioni di test");
                }
                this.lastEServiceDescriptorCreatedFromTemplate = eServiceCreatedFromTemplate.get().getDescriptors().get(0);

                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .as("Check stato dell'istanza creata")
                    .extracting(EServiceTemplateInstance::getActiveDescriptor)
                    .extracting(CompactDescriptor::getState)
                    .isEqualTo(expectedState);

                String instanceDefaultName = eServiceSourceTemplate.getEserviceTemplate().getName();
                softly.assertThat(eServiceCreatedFromTemplate)
                    .get()
                    .as("Check correttezza del nome dell'istanza creata")
                    .isEqualTo(isNull(lastEServiceCreatedFromTemplate) || isNull(lastEServiceCreatedFromTemplate.getInstanceLabel())
                        ? instanceDefaultName
                        : "%s %s".formatted(instanceDefaultName, lastEServiceCreatedFromTemplate.getInstanceLabel()));

                /* TODO 10/03/2025: in checkEServiceCreatedFromLatestTemplateVersion (parte del test
                *   dell'API di upgrade del servizio) è stata usata l'API
                *   getProducerEServiceDescriptor; verificare se possa essere sufficiente per essere usata
                *   anche qui, e in tal caso usarla al posto di getEServiceTemplateInstances */

                /* TODO 10/03/2025 sebbene i controlli soprastanti bastino a implementare lo
                    scenario indicato in SRS, sarebbe il caso di verificare che il risultato sia
                    coerente con tutti gli altri parametri del template, nonché con i parametri
                    inseriti nella creazione dell'e-service a partire dal template.
                    Un modo elastico per implementarli potrebbe essere mappare con Mapstruct
                    EServiceTemplateVersionDetails in EServiceTemplateInstance e procedere con
                    un isEqualTo(...), e quindi fare lo stesso mappando EServiceTemplateInstance
                    in InstanceEServiceSeed.
                 */
            });
        } catch (PollingPredicateException e) {
            fail("Il nuovo e-service non è stato creato correttamente");
        }
    }

    @When("l'utente tenta l'aggiornamento dell'istanza dell'e-service template all'ultima versione")
    public void updateEServiceInstanceToLatestVersion() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        updateEServiceInstance(eServiceId);
    }

    @When("l'utente tenta l'aggiornamento di un'istanza inesistente dell'e-service template")
    public void updateNonExistentEServiceInstance() {
        updateEServiceInstance(UUID.randomUUID());
    }

    @When("l'utente tenta l'aggiornamento di un'istanza dell'e-service template specificando un identificativo vuoto")
    public void updateEmptyEServiceInstance() {
        updateEServiceInstance(null);
    }

    private void updateEServiceInstance(UUID uuid) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.upgradeEServiceInstanceWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                uuid),
            ResponseEntity::getStatusCode);

        ResponseEntity<CreatedEServiceDescriptor> response = (ResponseEntity<CreatedEServiceDescriptor>) httpCallExecutor.getResponse();
        this.lastEServiceIdUpdatedFromTemplate = response.getBody().getId();
        this.lastEServiceDescriptorIdUpdatedFromTemplate = response.getBody().getId();
    }

    @Then("il nuovo e-service riferito all'ultima versione dell'e-service template è stato creato correttamente")
    public void checkEServiceCreatedFromLatestTemplateVersion() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getProducerEServiceDescriptorWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        lastEServiceIdUpdatedFromTemplate,
                        lastEServiceDescriptorIdUpdatedFromTemplate
                    ),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()),
                "Il nuovo e-service non è stato aggiornato correttamente"
            );

            @SuppressWarnings("all")
            ProducerEServiceDescriptor eServiceUpdatedDescriptor = ((ResponseEntity<ProducerEServiceDescriptor>) httpCallExecutor.getResponse()).getBody();

            assertSoftly(softly -> {
                softly.assertThat(lastEServiceDescriptorIdUpdatedFromTemplate)
                    .as("Check presenza descriptor associato all'istanza aggiornata")
                    .isEqualTo(eServiceUpdatedDescriptor.getId()); // NPE impossibile, in quanto da condizione di polling il body non può essere null
                softly.assertThat(eServiceUpdatedDescriptor)
                    .as("Check corretto stato dell'istanza aggiornata")
                    .extracting(ProducerEServiceDescriptor::getState)
                    .isEqualTo(EServiceDescriptorState.DRAFT);
            });
        } catch (PollingPredicateException e) {
            fail("Il nuovo e-service non è stato aggiornato correttamente");
        }
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template")
    public void editEServiceInstanceFields() {
        UUID eServiceId = lastEServiceIdCreatedFromTemplate;
        lastUpdateEServiceTemplateInstanceSeed = easyRandom.nextObject(UpdateEServiceTemplateInstanceSeed.class);
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica dei campi di un'istanza inesistente dell'e-service template")
    public void editNotExistentEServiceInstanceFields() {
        UUID eServiceId = UUID.randomUUID();
        lastUpdateEServiceTemplateInstanceSeed = easyRandom.nextObject(
            UpdateEServiceTemplateInstanceSeed.class);
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @When("l'utente tenta la modifica dei campi dell'istanza dell'e-service template indicando una specifica vuota")
    public void editEServiceInstanceUnspecifiedFields() {
        UUID eServiceId = UUID.randomUUID();
        lastUpdateEServiceTemplateInstanceSeed = new UpdateEServiceTemplateInstanceSeed();
        editEServiceInstanceFields(eServiceId, lastUpdateEServiceTemplateInstanceSeed);
    }

    @Then("i campi dell'istanza dell'e-service template sono stati modificati correttamente")
    public void checkEServiceInstanceFieldsEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceClient.getEServiceTemplateInstancesWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        templateContext.getLastTemplateManaged().id()),
                    ResponseEntity::getStatusCode),
                res ->
                        res.getStatusCode().is2xxSuccessful() &&
                        nonNull(res.getBody()) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getId().equals(lastEServiceIdCreatedFromTemplate)) &&
                        res.getBody().getResults().stream().anyMatch(instance -> instance.getInstanceLabel().equals(lastUpdateEServiceTemplateInstanceSeed.getInstanceLabel())),
                "L'istanza non è presente nell'elenco delle istanze dell'e-service template oppure non è stata modificata correttamente. Visionare i log delle call HTTP per maggiori dettagli."
            );
            /* TODO 12/03/2025 andrebbe effettuato un secondo polling per verificare la coerenza
            *   con i restanti campi di lastUpdateEServiceTemplateInstanceSeed. Rimandato causa
            *   incertezza sulla API da utilizzare. */
        } catch (PollingPredicateException e) {
            /* TODO questo tipo di gestione potrebbe essere di fatto inutile, lasciare che l'eccezione si
             *  propaghi potrebbe portare sostanzialmente allo stesso risultato. Indagare. */
            fail(e.getMessage());
        }
    }

    private void editEServiceInstanceFields(UUID eServiceId, UpdateEServiceTemplateInstanceSeed seed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceClient.updateEServiceTemplateInstanceByIdWithHttpInfo(
                eServiceId,
                seed),
            ResponseEntity::getStatusCode);
    }


    /* TODO un'alternativa all'uso di metodi come "areConsistent" - che confrontano i campi uno a uno - potrebbe essere
     * l'uso di una libreria di mapping, da usare per mappare un oggetto nell'altro tipo, e quindi procedere con
     * un normale equals(...).
     */

    private boolean areConsistent(EServiceTemplateVersionDetails version, EServiceTemplateVersionQuotasUpdateSeed lastUpdate) {
        return version.getDailyCallsPerConsumer().equals(lastUpdate.getDailyCallsPerConsumer()) &&
            version.getDailyCallsTotal().equals(lastUpdate.getDailyCallsTotal()) &&
            version.getVoucherLifespan().equals(lastUpdate.getVoucherLifespan());
    }

    private boolean areConsistent(EServiceTemplateDetails template, EServiceTemplateNameUpdateSeed lastUpdate) {
        return template.getName().equals(lastUpdate.getName());
    }

    private boolean areConsistent(UpdateEServiceTemplateVersionDocumentSeed updateSeed, EServiceDoc doc) {
        return updateSeed.getPrettyName().equals(doc.getPrettyName());
    }

    private String getUserToken() {
        return requireNonNull(
            sharedStepsContext.getUserToken(),
            "Il token dell'utente non è stato precedentemente impostato");
    }
}
