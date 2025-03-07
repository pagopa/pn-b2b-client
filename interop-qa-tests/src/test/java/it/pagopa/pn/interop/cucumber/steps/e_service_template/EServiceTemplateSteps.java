package it.pagopa.pn.interop.cucumber.steps.e_service_template;

import static it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind.DOCUMENT;
import static java.lang.Math.abs;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.IterableUtils.isEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.google.common.io.Files;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceTemplateVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDescriptionUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateNameUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionQuotasUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionDocumentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VersionSeedForEServiceTemplateCreation;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class EServiceTemplateSteps {
    /** Stores data on an e-service template useful for testing */
    public record EServiceTemplateInfo(String name, String audienceDescription, String eServiceDescription, UUID id, UUID lastVersionId){}

    @Mapper(componentModel = "spring")
    public interface EServiceTemplateInfoMapper {
        /* TODO 07/03/2025 overhead, se questo mapper continua a servire solo a questo bisognerebbe
         * semplicemente mutare EServiceTemplateInfo in un pojo e ricorrere ai metodi set per modificarlo   */
        @Mapping(source = "newVersionId", target = "lastVersionId")
        EServiceTemplateInfo withVersionId(EServiceTemplateInfo templateInfo, UUID newVersionId);
    }

    /** Stores data on an e-service template document useful for testing */
    record EServiceTemplateDocumentInfo(UUID id, String prettyName, String body){}

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final DataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final EServiceTemplateInfoMapper templateInfoMapper;

    // TODO alcune di queste variabili andranno incapsulate in un bean di tipo Context
    private EServiceTemplateInfo lastTemplateManaged;
    private UpdateEServiceTemplateSeed lastTemplateUpdateSeed;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;
    private EServiceRiskAnalysisSeed lastAddedRiskAnalysis;
    private int lastAddedRiskAnalysisIndex = -1; // -1 means no risk analysis has been added yet
    private EServiceTemplateDocumentInfo lastAddedDocument;
    private UpdateEServiceTemplateVersionDocumentSeed lastDocumentUpdateSeed;
    private UUID lastDeletedVersion;
    private EServiceTemplateNameUpdateSeed lastTemplateNameUpdateSeed;
    private EServiceTemplateDescriptionUpdateSeed lastTemplateAudienceDescriptionUpdateSeed;
    private EServiceTemplateDescriptionUpdateSeed lastTemplateDescriptionUpdateSeed;
    private EServiceTemplateVersionQuotasUpdateSeed lastTemplateVersionQuotasUpdateSeed;
    private DescriptorAttributesSeed lastAttributesUpdateSeed;

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
                                SharedStepsContext sharedStepsContext,
                                DescriptorAttributesMapper descriptorAttributesMapper,
                                EServiceTemplateInfoMapper templateInfoMapper) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.templateInfoMapper = templateInfoMapper;
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

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode}")
    public void createEServiceTemplate(EServiceMode eServiceMode) {
        EServiceTemplateSeed templateSeed = getEServiceTemplateSeed(eServiceMode);
        createEServiceTemplate(templateSeed);
    }

    @When("l'utente effettua la creazione di un e-service template in modalità {eServiceMode} in stato di {eServiceTemplateVersionState}")
    public void createEServiceTemplate(EServiceMode eServiceMode, EServiceTemplateVersionState desiredState) {
        createEServiceTemplate(eServiceMode);
        Runnable publisher = () -> {
            if (eServiceMode == EServiceMode.RECEIVE) {
                this.addRiskAnalysisToEServiceTemplateSuccessfully(); // perché ogni template in RECEIVE deve avere una risk analysis
            }

            this.addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind.INTERFACE); // perché ogni template deve avere almeno un'interfaccia
            publishEServiceTemplate();
        };
        switch (desiredState) {
            case DRAFT -> { /* no-op: un template appena creato è automaticamente in questo stato */ }
            case PUBLISHED -> publisher.run();
            case SUSPENDED -> {
                publisher.run();    // perché prima di essere sospeso deve essere pubblicato
                suspendEServiceTemplate();
            }
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
            .attributes(this.nextAttributesSeed())
            .dailyCallsPerConsumer(100)
            .dailyCallsTotal(1000)
            .voucherLifespan(86400)
            .description("Nuova descrizione della versione");
        updateEServiceTemplateVersion(
            this.lastTemplateManaged.id(),
            this.lastTemplateManaged.lastVersionId(),
            lastTemplateVersionUpdateSeed);
    }

    @Given("l'utente effettua delle modifiche alla versione dell'e-service template con successo")
    public void updateEServiceTemplateVersionSuccessfully() {
        updateEServiceTemplateVersion();
        checkEServiceTemplateVersionUpdate();
    }

    private DescriptorAttributesSeed nextDescriptorAttributesSeed() {
        return descriptorAttributesMapper.map(nextAttributesSeed());
    }

    private EServiceTemplateAttributesSeed nextAttributesSeed() {
        return new EServiceTemplateAttributesSeed()
            .addCertifiedItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList())
            .addDeclaredItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList())
            .addVerifiedItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList());
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

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template")
    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind);
    }

    @Then("l'aggiunta del documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template è stata effettuata correttamente")
    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        try {
            // controlla la coerenza con quanto contenuto nel template
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceDoc doc = switch (kind) {
                            case DOCUMENT -> res.getBody().getDocs().stream().filter(d -> d.getId().equals(lastAddedDocument.id())).findFirst().orElse(null);
                            case INTERFACE -> res.getBody().getInterface();
                            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                EServiceTemplateDocumentKind.class.getSimpleName(),
                                kind));
                        };
                        return doc.getPrettyName().equals(lastAddedDocument.prettyName());
                    }
                    return false;

                },
                "Lo stato del documento restituito dalla API GET degli e-service templates non corrisponde a quello atteso"
            );

            // controlla la coerenza del documento stesso
            pollingService.makePolling(
                () -> eServiceTemplateClient.getDocumentWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId,
                    lastAddedDocument.id()),
                res -> {
                    try {
                        return res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && Files.readLines(res.getBody(), StandardCharsets.UTF_8).get(0).equals(lastAddedDocument.body());
                    } catch (IOException e) {
                        throw new RuntimeException("Errore nella lettura del body binario della risposta HTTP: %s".formatted(res), e);
                    }
                },
                "Lo stato del documento restituito dalla API GET dei documenti non corrisponde a quello atteso"
            );
        } catch (PollingPredicateException e) {
            // TODO altrove non si è stati così precisi nei messaggi di errore, adeguare
            fail("Il documento non è stato aggiunto correttamente alla versione dell'e-service template: " + e.getMessage());
        }
    }

    @Given("l'utente effettua l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template con successo")
    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        addDocumentToEServiceTemplateVersion(kind);
        checkDocumentAddedToEServiceTemplateVersion(kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} alla versione dell'e-service template specificando lo stesso nome")
    public void addDocumentToEServiceTemplateVersionWithSameName(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, lastAddedDocument.prettyName());
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a un e-service template inesistente")
    public void addDocumentToNonExistentEServiceTemplate(EServiceTemplateDocumentKind kind) {
        addDocumentToEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), kind);
    }

    @When("l'utente tenta l'aggiunta di un documento di tipo {eServiceTemplateDocumentKind} a una versione inesistente dell'e-service template")
    public void addDocumentToNonExistentEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        addDocumentToEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID(), kind);
    }

    private void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind) {
        String prettyName = "e-service-template-%s-%s".formatted(kind.toString(),
            nextTestResourceNameSuffix());
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName);
    }

    private void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, String prettyName) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        String docBody = "Hello, I'm a document of type %s".formatted(kind);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                kind,
                prettyName,
                new ByteArrayResource(docBody.getBytes(StandardCharsets.UTF_8))),

            /* TODO altrove non è stata usata questa variante del metodo che permette di conservare il codice di risposta originale,
             * modificare anche gli altri scenari così che si possa effettuare un check preciso dello status restituito
             */
            ResponseEntity::getStatusCode);

        ResponseEntity<CreatedResource> response = (ResponseEntity<CreatedResource>) httpCallExecutor.getResponse();
        this.lastAddedDocument = response.getStatusCode().is2xxSuccessful()
            ? new EServiceTemplateDocumentInfo(response.getBody().getId(), prettyName, docBody)
            : null;
    }

    @When("l'utente tenta il reperimento del documento dalla versione dell'e-service template")
    public void getDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        getDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, lastAddedDocument.id());
    }

    @When("l'utente tenta il reperimento di un documento da un e-service template inesistente")
    public void getDocumentFromNonExistentEServiceTemplate() {
        getDocumentFromEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID(), lastAddedDocument.id());
    }

    @When("l'utente tenta il reperimento di un documento inesistente dalla versione dell'e-service template")
    public void getNonExistentDocumentFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        lastDocumentUpdateSeed = easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class);
        editDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, lastAddedDocument.id(), lastDocumentUpdateSeed);
    }

    @Then("la modifica del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentEditedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        UUID documentId = lastAddedDocument.id();

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
        editDocumentFromEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID(), lastAddedDocument.id(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inesistente nell'e-service template")
    public void editNonExistentDocumentFromEServiceTemplateVersion() {
        editDocumentFromEServiceTemplateVersion(lastTemplateManaged.id(), lastTemplateManaged.lastVersionId(), UUID.randomUUID(), easyRandom.nextObject(UpdateEServiceTemplateVersionDocumentSeed.class));
    }

    @When("l'utente tenta la modifica di un documento inserendo il nome di un altro documento")
    public void editDocumentFromEServiceTemplateVersionWithSameName() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

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
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        UUID documentId = lastAddedDocument.id();
        deleteDocumentFromEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, documentId);
    }

    @Then("la cancellazione del documento dell'e-service template è stata effettuata correttamente")
    public void checkDocumentDeletedFromEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        UUID documentId = lastAddedDocument.id();

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
        deleteDocumentFromEServiceTemplateVersion(lastTemplateManaged.id(), lastTemplateManaged.lastVersionId(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione del documento da una versione inesistente nell'e-service template")
    public void deleteDocumentFromNonExistentEServiceTemplateVersion() {
        deleteDocumentFromEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID(), lastAddedDocument.id());
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

    @When("l'utente tenta la pubblicazione della versione dell'e-service template")
    public void publishEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        publishEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @Then("la pubblicazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionPublished() {
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
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.PUBLISHED,
                "La versione dell'e-service template non è stata pubblicata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata pubblicata correttamente");
        }
    }

    @When("l'utente tenta la pubblicazione di una versione di un e-service template inesistente")
    public void publishNonExistentEServiceTemplate() {
        publishEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la pubblicazione di una versione inesistente di un e-service template")
    public void publishNonExistentEServiceTemplateVersion() {
        publishEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID());
    }

    private void publishEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.publishEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId));
    }

    @Given("l'utente effettua la creazione di una ulteriore versione nell'e-service template con successo")
    public void createAnotherEServiceTemplateVersionSuccessfully() {
        createAnotherEServiceTemplateVersion();
        checkEServiceTemplateVersionCreated();
    }

    @When("l'utente tenta la creazione di una ulteriore versione in un e-service template inesistente")
    public void createAnotherEServiceTemplateVersionInNonExistentEServiceTemplate() {
        createAnotherEServiceTemplateVersion(UUID.randomUUID());
    }

    @When("l'utente tenta la creazione di una ulteriore versione nell'e-service template")
    public void createAnotherEServiceTemplateVersion() {
        createAnotherEServiceTemplateVersion(lastTemplateManaged.id());
    }

    private void createAnotherEServiceTemplateVersion(UUID eServiceTemplateId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.createEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId),
            ResponseEntity::getStatusCode);

        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            UUID idOfNewVersion = ((ResponseEntity<CreatedResource>) httpCallExecutor.getResponse()).getBody()
            .getId();
            lastTemplateManaged = this.templateInfoMapper.withVersionId(lastTemplateManaged, idOfNewVersion);
        }
    }

    @Then("la creazione di una ulteriore versione nell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionCreated() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getVersions().size() == 2,
                "La versione dell'e-service template non è stata creata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata creata correttamente");
        }
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template")
    public void deleteEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    // TODO questa classe è piena di pattern ricorrenti, questo step ne è un'esempio. Andrebbero astratti e portati in classi di utility esterne.
    @Then("la cancellazione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateDeleted() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().equals(HttpStatus.NOT_FOUND),
                "L'e-service template non è stato cancellato correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("L'e-service template non è stato cancellato correttamente");
        }
    }

    @Then("la cancellazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionDeleted() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId),
                    ResponseEntity::getStatusCode),
                res -> res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && res.getBody().getVersions().size() == 1,
                "La versione dell'e-service template non è stata cancellata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata cancellata correttamente: il numero di versioni presenti è diverso da 1");
        }
    }

    @When("l'utente tenta la cancellazione di una versione di un e-service template inesistente")
    public void deleteNonExistentEServiceTemplate() {
        deleteEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la cancellazione di una versione inesistente dell'e-service template")
    public void deleteNonExistentEServiceTemplateVersion() {
        deleteEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID());
    }

    @Given("l'utente effettua la cancellazione della versione dell'e-service template con successo")
    public void deleteEServiceTemplateVersionSuccessfully() {
        deleteEServiceTemplateVersion();
        checkEServiceTemplateVersionDeleted();
        this.lastDeletedVersion = lastTemplateManaged.lastVersionId();
    }

    @When("l'utente tenta la cancellazione della versione dell'e-service template già cancellata")
    public void deleteAlreadyDeletedEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = this.lastDeletedVersion;
        deleteEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    private void deleteEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.deleteEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    @Given("l'utente effettua la sospensione della versione dell'e-service template con successo")
    public void suspendEServiceTemplateVersionSuccessfully() {
        suspendEServiceTemplateVersion();
        checkEServiceTemplateVersionSuspended();
    }

    @When("l'utente tenta la sospensione della versione dell'e-service template")
    public void suspendEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        suspendEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la sospensione della versione di un e-service template inesistente")
    public void suspendNonExistentEServiceTemplate() {
        suspendEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la sospensione di una versione inesistente nell'e-service template")
    public void suspendNonExistentEServiceTemplateVersion() {
        suspendEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID());
    }

    @Then("la sospensione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionSuspended() {
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
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.SUSPENDED,
                "La versione dell'e-service template non è stata sospesa correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata sospesa correttamente");
        }
    }

    private void suspendEServiceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.suspendEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la riattivazione della versione dell'e-service template")
    public void reactivateEServiceTemplateVersion() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        reactivateEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId);
    }

    @When("l'utente tenta la riattivazione di una versione di un e-service template inesistente")
    public void reactivateNonExistentEServiceTemplate() {
        reactivateEServiceTemplateVersion(UUID.randomUUID(), UUID.randomUUID());
    }

    @When("l'utente tenta la riattivazione di una versione inesistente nell'e-service template")
    public void reactivateNonExistentEServiceTemplateVersion() {
        reactivateEServiceTemplateVersion(lastTemplateManaged.id(), UUID.randomUUID());
    }

    // TODO gli step della classe andrebbero ordinati per Given -> When -> Then, rinominando gli And in modo da rendere chiaro il contesto

    @Then("la riattivazione della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionReactivated() {
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
                res -> nonNull(res.getBody()) && res.getBody().getState() == EServiceTemplateVersionState.PUBLISHED,
                "La versione dell'e-service template non è stata riattivata correttamente"
            );
        } catch (PollingPredicateException e) {
            fail("La versione dell'e-service template non è stata riattivata correttamente");
        }
    }

    private void reactivateEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.activateEServiceTemplateWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId),
            ResponseEntity::getStatusCode);
    }

    @When("l'utente tenta la modifica del nome dell'e-service template")
    public void editEServiceTemplateName() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateNameUpdateSeed = easyRandom.nextObject(EServiceTemplateNameUpdateSeed.class);
        editEServiceTemplateName(eServiceTemplateId, lastTemplateNameUpdateSeed);
    }

    @When("l'utente tenta la modifica del nome dell'e-service template specificando lo stesso nome")
    public void editEServiceTemplateNameWithSameName() {
        editEServiceTemplateNameWith(lastTemplateManaged.name());
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
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
    public void editEServiceTemplateAudienceDescription() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateAudienceDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateAudienceDescription(eServiceTemplateId, lastTemplateAudienceDescriptionUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateAudienceDescriptionWithSameAudienceDescription() {
        editEServiceTemplateAudienceDescriptionWith(lastTemplateManaged.audienceDescription());
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando la stringa vuota")
    public void editEServiceTemplateAudienceDescriptionWith() {
        editEServiceTemplateAudienceDescriptionWith("");
    }

    @When("l'utente tenta la modifica della descrizione dello scopo dell'e-service template specificando NULL")
    public void editEServiceTemplateAudienceDescriptionWithNullAudienceDescription() {
        editEServiceTemplateAudienceDescriptionWith(null);
    }

    @When("l'utente tenta la modifica della descrizione dello scopo di un e-service template inesistente")
    public void editNonExistentEServiceTemplateAudienceDescription() {
        editEServiceTemplateAudienceDescription(UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class));
    }

    private void editEServiceTemplateAudienceDescriptionWith(String description) {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateAudienceDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class)
            .description(description);
        editEServiceTemplateAudienceDescription(eServiceTemplateId, lastTemplateAudienceDescriptionUpdateSeed);
    }

    private void editEServiceTemplateAudienceDescription(UUID eServiceTemplateId,
        EServiceTemplateDescriptionUpdateSeed lastTemplateAudienceDescriptionUpdateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.updateEServiceTemplateAudienceDescriptionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                lastTemplateAudienceDescriptionUpdateSeed),
            ResponseEntity::getStatusCode);
    }

    @Then("la modifica della descrizione dello scopo dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateAudienceDescriptionEdited() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
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
                        return template.getAudienceDescription().equals(
                            lastTemplateAudienceDescriptionUpdateSeed.getDescription());
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
        lastTemplateDescriptionUpdateSeed = easyRandom.nextObject(EServiceTemplateDescriptionUpdateSeed.class);
        editEServiceTemplateDescription(eServiceTemplateId, lastTemplateDescriptionUpdateSeed);
    }

    @When("l'utente tenta la modifica della descrizione dell'e-service template specificando la stessa descrizione")
    public void editEServiceTemplateDescriptionWithSameDescription() {
        editEServiceTemplateDescriptionWith(lastTemplateManaged.eServiceDescription());
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
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
                        return template.getEserviceDescription().equals(
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        lastTemplateVersionQuotasUpdateSeed = easyRandom.nextObject(
            EServiceTemplateVersionQuotasUpdateSeed.class);
        lastTemplateVersionQuotasUpdateSeed.setVoucherLifespan(abs(lastTemplateVersionQuotasUpdateSeed.getVoucherLifespan()));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsTotal(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer() + 1));
        lastTemplateVersionQuotasUpdateSeed.setDailyCallsPerConsumer(abs(lastTemplateVersionQuotasUpdateSeed.getDailyCallsPerConsumer()));

        editEServiceTemplateVersionQuotas(eServiceTemplateId, eServiceTemplateVersionId, lastTemplateVersionQuotasUpdateSeed);
    }

    @When("l'utente tenta la modifica delle quote della versione dell'e-service template specificando un \"dailyCallsTotal\" inferiore a \"dailyCallsPerConsumer\"")
    public void editEServiceTemplateVersionQuotasWithDailyCallsTotalLessThanDailyCallsPerConsumer() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

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
        editEServiceTemplateVersionQuotas(lastTemplateManaged.id(), UUID.randomUUID(), easyRandom.nextObject(EServiceTemplateVersionQuotasUpdateSeed.class));
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
                        lastTemplateManaged.id(),
                        lastTemplateManaged.lastVersionId()),
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
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();

        //lastTemplateVersionUpdateSeed contiene, tra le altre, cose, gli attributi aggiunti l'ultima volta
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.map(lastTemplateVersionUpdateSeed.getAttributes());

        Boolean explicitAttributeVerification = lastAttributesUpdateSeed.getCertified().get(0)
            .get(0).getExplicitAttributeVerification();
        lastAttributesUpdateSeed.getCertified().get(0).get(0).setExplicitAttributeVerification(!explicitAttributeVerification); // modifico 1 campo di 1 attributo
        editEServiceTemplateVersionAttributes(eServiceTemplateId, eServiceTemplateVersionId, lastAttributesUpdateSeed);
    }

    @When("l'utente tenta la modifica degli attributi della versione dell'e-service template aggiungendone di nuovi")
    public void editEServiceTemplateVersionAttributesAddingNew() {
        UUID eServiceTemplateId = lastTemplateManaged.id();
        UUID eServiceTemplateVersionId = lastTemplateManaged.lastVersionId();
        lastAttributesUpdateSeed = this.descriptorAttributesMapper.map(lastTemplateVersionUpdateSeed.getAttributes());
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
        editEServiceTemplateVersionAttributes(lastTemplateManaged.id(), UUID.randomUUID(), nextDescriptorAttributesSeed());
    }

    @Then("la modifica degli attributi della versione dell'e-service template è stata effettuata correttamente")
    public void checkEServiceTemplateVersionAttributesEdited() {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        lastTemplateManaged.id(),
                        lastTemplateManaged.lastVersionId()),
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

    private boolean areConsistent(UpdateEServiceTemplateVersionDocumentSeed updateSeed, EServiceDoc doc) {
        return updateSeed.getPrettyName().equals(doc.getPrettyName());
    }

    /** Return a new {@link EServiceTemplateSeed} with only the mandatory fields set
     * @param eServiceMode the risk analysis mode of the e-service
     * @return a new {@link EServiceTemplateSeed} instance
     */
    private EServiceTemplateSeed getEServiceTemplateSeed(EServiceMode eServiceMode) {
        String templateName = String.format("eservice-template-%s", nextTestResourceNameSuffix());
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

    private String nextTestResourceNameSuffix() {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        return String.format("%d-%d", sharedStepsContext.getTestSeed(), randomInt);
    }

    private void createEServiceTemplate(EServiceTemplateSeed templateSeed) {
        String userToken = getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);

        CreatedEServiceTemplateVersion creationResponse = this.dataPreparationService.createEServiceTemplate(
            templateSeed);
        this.lastTemplateManaged = new EServiceTemplateInfo(
            templateSeed.getName(),
            templateSeed.getAudienceDescription(),
            templateSeed.getEserviceDescription(),
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
