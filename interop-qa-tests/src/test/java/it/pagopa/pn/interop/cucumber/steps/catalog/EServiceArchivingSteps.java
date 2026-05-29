package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceArchivingReasonSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EServiceArchivingSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;

    public EServiceArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione {string}")
    public void scheduleEServiceArchiving(String eServiceId, String archivingReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason);
    }

    @When("l'utente avvia il processo di archiviazione dell'e-service con id {string} e specificando la motivazione composta da {int} caratteri")
    public void scheduleEServiceArchivingWithReasonLength(String eServiceId, int archivingReasonLength) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String archivingReason = RandomStringUtils.insecure().nextAlphanumeric(archivingReasonLength);

        scheduleArchiveEService(resolvedEServiceId, archivingReason);
    }

    @When("l'utente annulla il processo di archiviazione dell'e-service con id {string}")
    public void cancelEServiceArchiving(String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        cancelArchiveEService(resolvedEServiceId);
    }

    @Given("l'utente ha già avviato il processo di archiviazione dell'e-service con id {string} e specificando la motivazione {string}")
    public void eServiceAlreadyInArchiving(String eServiceId, String archivingReason) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        String resolvedArchivingReason = catalogResolver.resolveArchivingReason(archivingReason);
        Map<UUID, EServiceDescriptorState> expectedStates = getExpectedArchivingStates(resolvedEServiceId);

        scheduleArchiveEService(resolvedEServiceId, resolvedArchivingReason);
        if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("L'avvio dell'archiviazione dell'e-service non ha avuto successo");
        }

        expectedStates.forEach((descriptorId, expectedState) ->
                pollDescriptorState(resolvedEServiceId, descriptorId, expectedState)
        );
    }

    private void scheduleArchiveEService(UUID eServiceId, String archivingReason) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveEService(
                                eServiceId,
                                new EServiceArchivingReasonSeed().archivingReason(archivingReason)
                        ),
                ResponseEntity::getStatusCode
        );
    }

    private Map<UUID, EServiceDescriptorState> getExpectedArchivingStates(UUID eServiceId) {
        UUID descriptorId = sharedStepsContext.getEServicesCommonContext().getDescriptorId();
        ProducerEServiceDescriptor producerEServiceDescriptor = clientTokenConfigurator.getEServiceClient()
                .getEServiceDescriptor(eServiceId, descriptorId);
        List<CompactDescriptor> descriptors = producerEServiceDescriptor.getEservice().getDescriptors();

        Map<UUID, EServiceDescriptorState> expectedStates = new LinkedHashMap<>();
        descriptors.forEach(descriptor ->
                expectedStates.put(descriptor.getId(), expectedArchivingState(descriptor.getState()))
        );
        expectedStates.putIfAbsent(
                producerEServiceDescriptor.getId(),
                expectedArchivingState(producerEServiceDescriptor.getState())
        );
        return expectedStates;
    }

    private void pollDescriptorState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "Il descriptor " + descriptorId + " dell'e-service non risulta in stato " + expectedState
        );
    }

    private void cancelArchiveEService(UUID eServiceId) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .cancelEServiceArchiving(eServiceId),
                ResponseEntity::getStatusCode
        );
    }

    private EServiceDescriptorState expectedArchivingState(EServiceDescriptorState descriptorState) {
        return switch (descriptorState) {
            case PUBLISHED, DEPRECATED, ARCHIVING -> EServiceDescriptorState.ARCHIVING;
            case SUSPENDED -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            case ARCHIVED -> EServiceDescriptorState.ARCHIVED;
            default -> throw new IllegalStateException(
                    "Il descriptor dell'e-service non può essere portato in archiviazione dallo stato " + descriptorState
            );
        };
    }
}
