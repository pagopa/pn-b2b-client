package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.*;
import it.pagopa.interop.eservice.service.IM2MEserviceDescriptorClient;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.GracePeriodDays;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.*;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.helpers.EServiceSeedFactory;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper.DocumentMapper;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.jeasy.random.randomizers.text.StringRandomizer;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.utility.StepParser.nullableBoolean;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class EserviceSteps extends AbstractCommonSteps<EService, UUID> {
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;
    private final PollingService pollingService;
    private final IM2MEserviceClient client;
    private final IM2MEserviceDescriptorClient descriptorClient;
    private final BlobFileCreator blobFileCreator;
    private final DelayService delayService;
    private final CatalogResolver catalogResolver;

    private final EServicePatchOperationsAssistant eServicePatchAssistant;
    private final EServiceDelegationPatchOperationsAssistant eServiceDelegationPatchAssistant;
    private final EServiceNamePatchOperationsAssistant eServiceNamePatchAssistant;
    private final EServiceDescriptionPatchOperationsAssistant eServiceDescriptionPatchAssistant;

    private final EServiceSeedFactory eServiceSeedFactory;
    private final DocumentMapper documentMapper;

    public EserviceSteps(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator,
            BlobFileCreator blobFileCreator,
            EServicePatchOperationsAssistant eServicePatchAssistant,
            EServiceDelegationPatchOperationsAssistant eServiceDelegationPatchAssistant,
            EServiceNamePatchOperationsAssistant eServiceNamePatchAssistant,
            EServiceDescriptionPatchOperationsAssistant eServiceDescriptionPatchAssistant,
            EServiceSeedFactory eServiceSeedFactory,
            DocumentMapper documentMapper,
            DelayService delayService
    ) {
        super("eService", clientTokenConfigurator.getM2meServiceClient(), sharedStepsContext);
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.client = clientTokenConfigurator.getM2meServiceClient();
        this.descriptorClient = clientTokenConfigurator.getM2mEServiceDescriptorClient();
        this.blobFileCreator = blobFileCreator;
        client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.eServicePatchAssistant = eServicePatchAssistant;
        this.eServiceDelegationPatchAssistant = eServiceDelegationPatchAssistant;
        this.eServiceNamePatchAssistant = eServiceNamePatchAssistant;
        this.eServiceDescriptionPatchAssistant = eServiceDescriptionPatchAssistant;
        this.eServiceSeedFactory = eServiceSeedFactory;
        this.documentMapper = documentMapper;
        this.delayService = delayService;
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @Given("l'utente tenta la creazione dell'e-service con la configurazione predefinita")
    public void createEService() {
        EServiceSeed seed = this.eServiceSeedFactory.defaultEServiceSeed();

        EServiceCreateRequest request = EServiceCreateRequest.fromSeed(seed);

        httpExecutor.performCall(() -> this.client.createEService(request));

        if (httpExecutor.getResponseStatus() == HttpStatus.CREATED || httpExecutor.getResponseStatus() == HttpStatus.OK) {
            sharedStepsContext.getEServicesCommonContext().setEserviceId(
                    ((EService) httpExecutor.getResponse()).getId()
            );
        }
    }

    @Given("l'utente tenta la creazione dell'e-service con la seguente configurazione:")
    public void createEService(DataTable dataTable) {

        EServiceSeed seed = this.eServiceSeedFactory.defaultEServiceSeed();

        Map<String, String> data = dataTable.asMap(String.class, String.class);

        if (data.containsKey("description-length")) {
            int descriptionLength = Integer.parseInt(data.get("description-length"));
            seed.description((new StringRandomizer(descriptionLength, descriptionLength, System.currentTimeMillis())).getRandomValue());
        }

        EServiceCreateRequest request = EServiceCreateRequest.fromSeed(seed);

        httpExecutor.performCall(() -> this.client.createEService(request));

        if (httpExecutor.getResponseStatus() == HttpStatus.CREATED || httpExecutor.getResponseStatus() == HttpStatus.OK) {
            sharedStepsContext.getEServicesCommonContext().setEserviceId(
                    ((EService) httpExecutor.getResponse()).getId()
            );
        }
    }

    @Given("l'utente effettua la cancellazione dell'e-service con successo")
    public void successfullyDeleteEService() {
        deleteEService();
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        pollingService.makePolling(
                () -> httpExecutor.performCallSavingBodyResponse(() -> client.getWithHttpInfo(eserviceId)),
                Objects::isNull,
                "Non è stato possibile eliminare l'e-service. Consultare i log per maggiori dettagli.");

        Assertions.assertThat(this.httpExecutor.getResponseStatus()).isEqualTo(NOT_FOUND);
    }

    @When("l'utente tenta di effettuare la cancellazione di un e-service inesistente")
    public void deleteNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        httpExecutor.performCall(() -> this.client.delete(eserviceId));
    }

    @When("l'utente tenta di effettuare la cancellazione dell'e-service")
    public void deleteEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        httpExecutor.performCall(() -> this.client.delete(eserviceId));
    }

    @When("viene avviata l'archiviazione dell'e-service {string} indicando la motivazione {string} e un preavviso di {gracePeriodDays} giorni")
    public void scheduleEServiceArchiving(String eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason, gracePeriodDays);
    }

    @When("viene avviata l'archiviazione dell'e-service {string} indicando una motivazione di {int} caratteri e un preavviso di {gracePeriodDays} giorni")
    public void scheduleEServiceArchivingWithReasonLength(String eServiceId, int archivingReasonLength, GracePeriodDays gracePeriodDays) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);

        scheduleArchiveEService(resolvedEServiceId, archivingReason, gracePeriodDays);
    }

    private void scheduleArchiveEService(UUID eServiceId, String archivingReason, GracePeriodDays gracePeriodDays) {
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        sharedStepsContext.getEServicesCommonContext()
                .setDescriptorArchivingGracePeriodDays(gracePeriodDays);

        EServiceArchivingRequest request = EServiceArchivingRequest.builder()
                .archivingReason(archivingReason)
                .gracePeriodDays(gracePeriodDays.getValue())
                .build();

        httpExecutor.performCall(() -> client.scheduleArchiveEService(eServiceId, request));
    }

    @When("viene annullato il processo di archiviazione dell'e-service con id {string}")
    public void cancelEServiceArchiving(String eServiceId) {
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        httpExecutor.performCall(() -> client.cancelScheduleArchiveEService(resolvedEServiceId));
    }

    @When("l'utente tenta di sospende quel descrittore")
    public void suspendDescriptor() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        httpExecutor.performCall(() -> descriptorClient.suspendDescriptor(eserviceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione dell'e-service")
    public void unsuspendEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        httpExecutor.performCall(() -> descriptorClient.unsuspendEService(eserviceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione di un e-service inesistente")
    public void unsuspendNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        httpExecutor.performCall(() -> descriptorClient.unsuspendEService(eserviceId, descriptorId));
    }

    @Then("l'e-service è stato riattivato con successo")
    public void successfullyUnsuspendedEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        pollingService.makePolling(() -> httpExecutor.performCall(
                        () -> descriptorClient.getDescriptor(eserviceId, descriptorId)),
                status -> status.is2xxSuccessful() && ((it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptor) httpExecutor.getResponse()).getState().equals(EServiceDescriptorState.PUBLISHED),
                "Il servizio non è stato riattivato come previsto.");
    }

    @Given("l'utente effettua il caricamento dell'interfaccia dell'e-service con successo")
    public void successfullyUploadInterface() {
        uploadInterface();
        interfaceExistsCheck();
    }

    @Given("l'utente effettua la cancellazione dell'interfaccia dell'e-service con successo")
    public void successfullyDeletedInterface() {
        deleteInterface();
        interfaceNotExistsCheck();
    }

    @When("l'utente tenta di effettuare il caricamento dell'interfaccia dell'e-service")
    public void uploadInterface() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        String interfaceName = buildInterfaceName(eServiceId, descriptorId);
        uploadInterface(interfaceName, eServiceId, descriptorId);
    }

    private static String buildInterfaceName(UUID eServiceId, UUID descriptorId) {
        return "e-service-%s-descriptor-%s-interface-%d".formatted(eServiceId,
                descriptorId,
                RandomUtils.secure().randomInt(0, 99));
    }

    @When("l'utente tenta di effettuare la cancellazione dell'interfaccia dell'e-service")
    public void deleteInterface() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        deleteInterface(eServiceId, descriptorId);
    }

    private void deleteInterface(UUID eServiceId, UUID descriptorId) {
        httpExecutor.performCall(() -> descriptorClient.deleteInterface(eServiceId, descriptorId));
    }

    @When("l'utente tenta di recuperare i metadati dei documenti associati all'e-service")
    public void getDocumentsMetadata() {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        getDocuments(eServiceId, descriptorId);
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di un e-service inesistenti")
    public void getNonExistentEServiceDocumentsMetadata() {
        UUID randomUUID = UUID.randomUUID();
        getDocuments(randomUUID, sharedStepsContext.getEServicesCommonContext().getDescriptorId());
    }

    @When("l'utente tenta di recuperare i metadati dei documenti di un descriptor inesistenti")
    public void getNonExistentDescriptorDocumentsMetadata() {
        UUID randomUUID = UUID.randomUUID();
        getDocuments(sharedStepsContext.getEServicesCommonContext().getEserviceId(), randomUUID);
    }

    private void getDocuments(UUID eServiceId, UUID descriptorId) {
        delayService.delay();
        httpExecutor.performCall(() -> descriptorClient.getDocuments(eServiceId, descriptorId));
    }

    @Then("i metadati dei documenti ottenuti sono coerenti con quelli caricati")
    public void checkDocumentsMetadata() {
        List<Document> actualDocuments = ((Documents) httpExecutor.getResponse()).getResults();
        List<DocumentMetadata> actualDocumentsMetadata = documentMapper.map(actualDocuments);
        List<DocumentMetadata> expectedDocumentsMetadata = sharedStepsContext.getEServicesCommonContext()
                .getDocumentsMetadata();

        assertSoftly(softly -> softly.assertThat(actualDocumentsMetadata)
                .as("Verifica che i metadati dei documenti caricati siano coerenti")
                .usingFieldByFieldElementComparator()
                .usingComparatorForElementFieldsWithType(
                        (timestamp1, timestamp2) -> {
                            Duration actualAndExpectedDifference = Duration.between(timestamp1, timestamp2).abs();
                            Duration acceptedDelay = Duration.ofSeconds(10);

                            // Se i timestamp di creazione sono divisi da un delay ragionevole, allora
                            // si considerano "uguali", per la riuscita del test
                            return actualAndExpectedDifference.compareTo(acceptedDelay) < 0 ? 0 : 1;
                        },
                        OffsetDateTime.class)
                .containsExactlyInAnyOrderElementsOf(expectedDocumentsMetadata));
    }

    @Then("è presente un'interfaccia per l'e-service")
    public void interfaceExistsCheck() {
        Predicate<HttpStatus> interfaceUploaded = HttpStatus::is2xxSuccessful;
        checkEServiceInterface(interfaceUploaded);
    }

    @Then("non è presente alcuna interfaccia per l'e-service")
    public void interfaceNotExistsCheck() {
        Predicate<HttpStatus> interfaceUploaded = HttpStatus::isError;
        checkEServiceInterface(interfaceUploaded);
    }

    private void checkEServiceInterface(Predicate<HttpStatus> interfaceUploaded) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        pollingService.makePolling(
                () -> httpExecutor.performCall(() -> descriptorClient.downloadEServiceDescriptorInterface(eServiceId, descriptorId)),
                interfaceUploaded,
                "L'interfaccia dell'e-service non sottostà alle condizioni attese. Visionare logs per maggiori dettagli");
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente")
    public void uploadNonExistentEServiceInterface() {
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        String interfaceName = buildInterfaceName(eServiceId, descriptorId);
        uploadInterface(interfaceName, eServiceId, descriptorId);
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di tipo YAML {string}")
    public void uploadInterfaceWithNoVersion(String versionState) {
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        String interfaceName = buildInterfaceName(eServiceId, descriptorId);

        String filename = switch (versionState) {
            case "senza versione" -> "missing-version-interface.yaml";
            case "con versione obsoleta" -> "invalid-version-interface.yaml";
            default -> throw new IllegalStateException("Unexpected value: " + versionState);
        };

        uploadInterface(interfaceName, eServiceId, descriptorId, filename);
    }

    @When("l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente")
    public void deleteNonExistentEServiceInterface() {
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        deleteInterface(eServiceId, descriptorId);
    }

    private void uploadInterface(String interfaceName, UUID eServiceId, UUID descriptorId) {
        uploadInterface(interfaceName, eServiceId, descriptorId, String.format("interface.%s", "yaml"));
    }

    private void uploadInterface(String interfaceName, UUID eServiceId, UUID descriptorId, String fileName) {
        delayService.delay();
        String filePath = String.format("src/main/resources/%s", fileName);
        Resource resource = blobFileCreator.createBlobFile(filePath, fileName);
        sharedStepsContext.getEServicesCommonContext().setInterfaceName(interfaceName);
        EServiceInterfaceUploadRequest request = new EServiceInterfaceUploadRequest()
                .prettyName(interfaceName)
                .resource(resource)
                .eServiceId(eServiceId)
                .descriptorId(descriptorId);
        httpExecutor.performCall(() -> client.uploadInterface(request));
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service")
    public void patchEService() {
        EServicePatchRequest request = this.eServicePatchAssistant.buildDefaultPatchRequest();
        eServicePatchAssistant.patchResource(request);
    }

    @When("{string} con ruolo {m2mRole} tenta di effettuare la modifica parziale dell'e-service")
    public void patchEService(String tenant, M2MRole m2mRole) {
        EServicePatchRequest request = this.eServicePatchAssistant.buildDefaultPatchRequest();
        eServicePatchAssistant.patchResource(request, tenant, m2mRole);
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service con token non valido")
    public void patchEServiceWithNotValidToken() {
        eServicePatchAssistant.patchResourceWithInvalidToken(
                this.eServicePatchAssistant.buildDefaultPatchRequest());
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceSubset() {
        String id = RandomStringUtils.insecure().nextAlphanumeric(5);
        EServicePatchRequest request = EServicePatchRequest.builder()
                .name("some patched name - " + id)
                .description("some patched description - " + id)
                .technology(EServiceTechnology.REST)
                .build();
        eServicePatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service in stato DRAFT specificando una descrizione di lunghezza pari a {int} caratteri")
    public void patchEServiceWithDescriptionLength(int length) {
        String description = (new StringRandomizer(length, length, System.currentTimeMillis())).getRandomValue();
        EServicePatchRequest request = EServicePatchRequest.builder()
                .description(description)
                .build();
        eServicePatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di recuperare l'e-service creato")
    public void getEService() {
        UUID eService = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        this.client.get(eService);
    }

    @When("l'utente tenta di effettuare la modifica parziale di un e-service inesistente")
    public void patchNonExistentEService() {
        eServicePatchAssistant.patchNonExistentResource();
    }

    @Then("l'e-service è stato parzialmente modificato correttamente")
    public void verificaPatchedEService() {
        eServicePatchAssistant.checkPatchedResource();
    }

    @Then("l'e-service non ha subito modifiche")
    public void verificaUnpatchedEService() {
        eServicePatchAssistant.checkUnpatchedResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service impostando la delega amministrativa a {string} e quella tecnica a {string}")
    public void patchEServiceDelegation(String isConsumerDelegable, String isClientAccessDelegable) {
        //aggiunto per supportare i casi in cui l'e-service è stato generato da e-service template
        if (sharedStepsContext.getEServicesCommonContext().getEserviceId() == null) {
            UUID templateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
            if (templateInstanceId != null) {
                sharedStepsContext.getEServicesCommonContext().setEserviceId(templateInstanceId);
            }
        }

        EServiceDelegationPatchRequest request = EServiceDelegationPatchRequest.builder()
                .isConsumerDelegable(nullableBoolean(isConsumerDelegable))
                .isClientAccessDelegable(nullableBoolean(isClientAccessDelegable))
                .build();
        eServiceDelegationPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega di un e-service inesistente")
    public void patchNonExistentEServiceDelegation() {
        eServiceDelegationPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale non specificando l'id dell'e-service")
    public void patchNonSpecifiedEServiceDelegation() {
        eServiceDelegationPatchAssistant.patchNonSpecifiedResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service con token non valido")
    public void patchEServiceDelegationWithNotValidToken() {
        //aggiunto per supportare i casi in cui l'e-service è stato generato da e-service template
        if (sharedStepsContext.getEServicesCommonContext().getEserviceId() == null) {
            UUID templateInstanceId = sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();
            if (templateInstanceId != null) {
                sharedStepsContext.getEServicesCommonContext().setEserviceId(templateInstanceId);
            }
        }

        EServiceDelegationPatchRequest request = EServiceDelegationPatchRequest.builder()
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false)
                .build();
        eServiceDelegationPatchAssistant.patchResourceWithInvalidToken(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameDelegation() {
        eServiceDelegationPatchAssistant.patchResourceWithSameInfo();
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service")
    public void patchEServiceName() {
        EServiceNamePatchRequest request = EServiceNamePatchRequest.builder()
                .name("patched name - " + RandomStringUtils.insecure().nextAlphanumeric(5))
                .build();
        eServiceNamePatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service con un token non valido")
    public void patchEServiceNameWithNotValidToken() {
        EServiceNamePatchRequest request = EServiceNamePatchRequest.builder()
                .name("patched name - " + RandomStringUtils.insecure().nextAlphanumeric(5))
                .build();
        eServiceNamePatchAssistant.patchResourceWithInvalidToken(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceNameSubset() {
        eServiceNamePatchAssistant.patchResource(EServiceNamePatchRequest.builder().build());
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome di un e-service inesistente")
    public void patchNonExistentEServiceName() {
        eServiceNamePatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameName() {
        eServiceNamePatchAssistant.patchResourceWithSameInfo();
    }

    @When("l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service")
    public void patchEServiceDescription() {
        EServiceDescriptionPatchRequest request = EServiceDescriptionPatchRequest.builder()
                .description("patched description - " + UUID.randomUUID())
                .build();
        eServiceDescriptionPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica della descrizione dell'e-service specificando una descrizione di lunghezza pari a {int} caratteri")
    public void patchEServiceDescription(int length) {
        String description = (new StringRandomizer(length, length, System.currentTimeMillis())).getRandomValue();
        EServiceDescriptionPatchRequest request = EServiceDescriptionPatchRequest.builder()
                .description(description)
                .build();
        eServiceDescriptionPatchAssistant.patchResource(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service con token non valido")
    public void patchEServiceDescriptionWithNotValidToken() {
        EServiceDescriptionPatchRequest request = EServiceDescriptionPatchRequest.builder()
                .description("patched description - " + UUID.randomUUID())
                .build();
        eServiceDescriptionPatchAssistant.patchResourceWithInvalidToken(request);
    }

    @When("l'utente tenta di effettuare la modifica parziale della descrizione di un e-service inesistente")
    public void patchNonExistentEServiceDescription() {
        eServiceDescriptionPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameDescription() {
        eServiceDescriptionPatchAssistant.patchResourceWithSameInfo();
    }

    @Then("l'e-service restituito è coerente con le modifiche effettuate")
    public void verificaRisultatoPatch() {
        eServicePatchAssistant.checkPatchOperationResult();
    }

    @Override
    public void bindActual(SharedStepsContext context, List<EService> actualEntities) {
        var eserviceContext = context.getEServicesCommonContext();
        eserviceContext.setRetrievedEservicesIds(actualEntities.stream().map(this::mapTo).collect(Collectors.toList()));
    }

    @Override
    public List<EService> bindExpected(SharedStepsContext context) {
        return context.getEServicesCommonContext().getPublishedEservicesIds().stream().map(this::mapTo).collect(Collectors.toList());
    }

    @Override
    protected boolean isEqual(EService a, EService b) {
        return a.getId().equals(b.getId());
    }

    private EServiceDescriptor mapTo(EService eService) {
        return EserviceDescriptorDomainMapper.mapTo(eService);
    }

    private EService mapTo(EServiceDescriptor descriptor) {
        return EserviceDescriptorDomainMapper.mapTo(descriptor);
    }
}
