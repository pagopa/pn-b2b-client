package it.pagopa.pn.interop.cucumber.steps.delegate;

import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class DelegationApproveStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IEServiceClient eServiceClient;
    private final IdentityService identityService;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DelegationApproveStep(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("{string} approva la pubblicazione dell'e-service")
    public void userApproveEService(String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, null));
        approveEService();
    }

    @When("l'utente approva la pubblicazione dell'e-service")
    public void userApproveEService() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        approveEService();
    }

    private void approveEService() {
        httpCallExecutor.performCall(() -> eServiceClient.approveDelegatedEServiceDescriptor(
            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
            sharedStepsContext.getEServicesCommonContext().getDescriptorId()
        ));
    }
}
