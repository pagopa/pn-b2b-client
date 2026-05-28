package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.catalog.utils.CatalogResolver;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class DescriptorArchivingSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final CatalogResolver catalogResolver;

    public DescriptorArchivingSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.catalogResolver = new CatalogResolver(sharedStepsContext);
    }

    @When("l'utente archivia la vecchia versione con id {string} dell'e-service con id {string}")
    public void archiveOldDescriptor(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);

        scheduleArchiveDescriptor(resolvedEServiceId, resolvedDescriptorId);
    }

    @Given("l'utente ha già messo in archiviazione la vecchia versione con id {string} dell'e-service con id {string}")
    public void oldDescriptorAlreadyInArchiving(String descriptorId, String eServiceId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID resolvedDescriptorId = catalogResolver.resolveOldDescriptorId(descriptorId);
        UUID resolvedEServiceId = catalogResolver.resolveEServiceId(eServiceId);
        ProducerEServiceDescriptor oldDescriptor = clientTokenConfigurator.getEServiceClient()
                .getEServiceDescriptor(resolvedEServiceId, resolvedDescriptorId);
        EServiceDescriptorState expectedState = expectedArchivingState(oldDescriptor.getState());

        scheduleArchiveDescriptor(resolvedEServiceId, resolvedDescriptorId);
        if (httpCallExecutor.getResponseStatus() == null || !httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            throw new IllegalStateException("L'avvio dell'archiviazione della vecchia versione dell'e-service non ha avuto successo");
        }

        pollDescriptorState(resolvedEServiceId, resolvedDescriptorId, expectedState);
    }

    private void scheduleArchiveDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveDescriptor(eServiceId, descriptorId),
                ResponseEntity::getStatusCode
        );
    }

    @Then("la vecchia versione dell'e-service è in stato {string}")
    public void oldEServiceVersionIsInState(String descriptorState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();
        EServiceDescriptorState expectedState = EServiceDescriptorState.fromValue(descriptorState);

        pollDescriptorState(eServiceId, oldDescriptorId, expectedState);
    }

    private void pollDescriptorState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState expectedState) {
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, descriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "La vecchia versione dell'e-service non risulta in stato " + expectedState
        );
    }

    private EServiceDescriptorState expectedArchivingState(EServiceDescriptorState descriptorState) {
        return switch (descriptorState) {
            case DEPRECATED -> EServiceDescriptorState.ARCHIVING;
            case SUSPENDED -> EServiceDescriptorState.ARCHIVING_SUSPENDED;
            default -> throw new IllegalStateException(
                    "La vecchia versione dell'e-service non può essere portata in archiviazione dallo stato " + descriptorState
            );
        };
    }
}
