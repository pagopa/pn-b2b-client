package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServicesCommonContext;

import java.util.UUID;

public class EServiceCloneSteps {
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final EServicesCommonContext eServicesCommonContext;

    public EServiceCloneSteps(DataPreparationService dataPreparationService,
                                       ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.dataPreparationService = dataPreparationService;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServicesCommonContext = sharedStepsContext.getEServicesCommonContext();
    }

    @Given("{string} ha già creato una versione in {string} per quell'e-service")
    public void tenantHasAlreadyCreatedVersionWithState(String tenantType, String descriptorState) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        UUID descriptorId = dataPreparationService.createNextDraftDescriptor(eServicesCommonContext.getEserviceId());
        eServicesCommonContext.setDescriptorId(descriptorId);

        dataPreparationService.bringDescriptorToGivenState(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId(),
                EServiceDescriptorState.fromValue(descriptorState), false);
    }

    @When("l'utente clona quell'e-service")
    public void cloneEservice() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().cloneEServiceByDescriptor(eServicesCommonContext.getEserviceId(), eServicesCommonContext.getDescriptorId())
        );
    }
}
