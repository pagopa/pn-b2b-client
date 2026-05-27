package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
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

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient()
                        .scheduleArchiveDescriptor(resolvedEServiceId, resolvedDescriptorId),
                ResponseEntity::getStatusCode
        );
    }

    @Then("la vecchia versione dell'e-service è in stato {string}")
    public void oldEServiceVersionIsInState(String descriptorState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID oldDescriptorId = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId();
        EServiceDescriptorState expectedState = EServiceDescriptorState.fromValue(descriptorState);

        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getEServiceClient().getEServiceDescriptor(eServiceId, oldDescriptorId),
                descriptor -> descriptor != null && expectedState.equals(descriptor.getState()),
                "La vecchia versione dell'e-service non risulta in stato " + expectedState
        );
    }
}
