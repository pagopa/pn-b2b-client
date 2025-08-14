package it.pagopa.pn.interop.cucumber.steps.m2m.eservice;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.eservice.service.IM2MEserviceClient;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceDelegationPatchRequest;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceDescriptionPatchRequest;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceInterfaceUploadRequest;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServiceNamePatchRequest;
import it.pagopa.interop.eservice.service.IM2MEserviceClient.EServicePatchRequest;
import it.pagopa.interop.eservice.service.mapper.EserviceDescriptorDomainMapper;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EServiceTechnology;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.EServiceDelegationPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.EServiceDescriptionPatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.EServiceNamePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.eservice.assistant.EServicePatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

public class EserviceSteps extends AbstractCommonSteps<EService, UUID> {
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpExecutor;
    private final PollingService pollingService;
    private final IM2MEserviceClient client;
    private final BlobFileCreator blobFileCreator;

    private final EServicePatchOperationsAssistant eServicePatchAssistant;
    private final EServiceDelegationPatchOperationsAssistant eServiceDelegationPatchAssistant;
    private final EServiceNamePatchOperationsAssistant eServiceNamePatchAssistant;
    private final EServiceDescriptionPatchOperationsAssistant eServiceDescriptionPatchAssistant;

    public EserviceSteps(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator,
        BlobFileCreator blobFileCreator,
        EServicePatchOperationsAssistant eServicePatchAssistant,
        EServiceDelegationPatchOperationsAssistant eServiceDelegationPatchAssistant,
        EServiceNamePatchOperationsAssistant eServiceNamePatchAssistant,
        EServiceDescriptionPatchOperationsAssistant eServiceDescriptionPatchAssistant
    ) {
        super("eService", clientTokenConfigurator.getM2meServiceClient(), sharedStepsContext);
        this.sharedStepsContext = sharedStepsContext;
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.client  = clientTokenConfigurator.getM2meServiceClient();
        this.blobFileCreator = blobFileCreator;
        client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.eServicePatchAssistant = eServicePatchAssistant;
        this.eServiceDelegationPatchAssistant = eServiceDelegationPatchAssistant;
        this.eServiceNamePatchAssistant = eServiceNamePatchAssistant;
        this.eServiceDescriptionPatchAssistant = eServiceDescriptionPatchAssistant;
    }

    @Given("l'utente effettua la cancellazione dell'e-service con successo")
    public void successfullyDeleteEService() {
        deleteEService();
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        pollingService.makePolling(
            () -> httpExecutor.performCall(() -> this.client.get(eserviceId)),
            status -> status.equals(NOT_FOUND),
            "Non è stato possibile eliminare l'e-service. Consultare i log per maggiori dettagli.");
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

    @When("l'utente tenta di effettuare la riattivazione dell'e-service")
    public void unsuspendEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        httpExecutor.performCall(() -> client.unsuspendEService(eserviceId, descriptorId));
    }

    @When("l'utente tenta di effettuare la riattivazione di un e-service inesistente")
    public void unsuspendNonExistentEService() {
        UUID eserviceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        httpExecutor.performCall(() -> client.unsuspendEService(eserviceId, descriptorId));
    }

    @Then("l'e-service è stato riattivato con successo")
    public void successfullyUnsuspendedEService() {
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        pollingService.makePolling(() -> httpExecutor.performCall(
            () ->client.getDescriptor(eserviceId, descriptorId)),
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
        httpExecutor.performCall(() -> client.deleteInterface(eServiceId, descriptorId));
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
            () -> httpExecutor.performCall(() -> client.downloadEServiceDescriptorInterface(eServiceId, descriptorId)),
            interfaceUploaded,
            "L'interfaccia dell'e-service non sottostà alle condizioni attese. Visionare logs per maggiori dettagli");
    }

    @When("l'utente tenta di effettuare il caricamento di un'interfaccia di un e-service inesistente")
    public void uploadNonExistentEServiceInterface(){
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        String interfaceName = buildInterfaceName(eServiceId, descriptorId);
        uploadInterface(interfaceName, eServiceId, descriptorId);
    }

    @When("l'utente tenta di effettuare la cancellazione di un'interfaccia di un e-service inesistente")
    public void deleteNonExistentEServiceInterface(){
        UUID eServiceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        deleteInterface(eServiceId, descriptorId);
    }

    private void uploadInterface(String interfaceName, UUID eServiceId, UUID descriptorId) {
        String fileName = String.format("interface.%s", "yaml");
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
        eServicePatchAssistant.patchResource(EServicePatchRequest.builder()
            .technology(EServiceTechnology.SOAP)
            .isSignalHubEnabled(false)
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceSubset() {
        eServicePatchAssistant.patchResource(EServicePatchRequest.builder()
            .technology(EServiceTechnology.REST)
            .build());
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

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service")
    public void patchEServiceDelegation() {
        eServiceDelegationPatchAssistant.patchResource(EServiceDelegationPatchRequest.builder()
            .isConsumerDelegable(true)
            .isClientAccessDelegable(true)
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega di un e-service inesistente")
    public void patchNonExistentEServiceDelegation() {
        eServiceDelegationPatchAssistant.patchNonExistentResource();
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceDelegationSubset() {
        eServiceDelegationPatchAssistant.patchResource(EServiceDelegationPatchRequest.builder()
            .isConsumerDelegable(false)
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale della delega dell'e-service senza apportare cambiamenti")
    public void patchEServiceWithSameDelegation() {
        eServiceDelegationPatchAssistant.patchResourceWithSameInfo();
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service")
    public void patchEServiceName() {
        eServiceNamePatchAssistant.patchResource(EServiceNamePatchRequest.builder()
            .name("patched name - " + UUID.randomUUID())
            .prettyName("patched pretty name - " + UUID.randomUUID())
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale del nome dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceNameSubset() {
        eServiceNamePatchAssistant.patchResource(EServiceNamePatchRequest.builder()
            .prettyName("patched pretty name")
            .build());
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
        eServiceDescriptionPatchAssistant.patchResource(EServiceDescriptionPatchRequest.builder()
            .description("patched description - " + UUID.randomUUID())
            .summary("patched summary - " + UUID.randomUUID())
            .build());
    }

    @When("l'utente tenta di effettuare la modifica parziale della descrizione dell'e-service specificando un sottoinsieme di informazioni")
    public void patchEServiceDescriptionSubset() {
        eServiceDescriptionPatchAssistant.patchResource(EServiceDescriptionPatchRequest.builder()
            .summary("patched summary")
            .build());
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
