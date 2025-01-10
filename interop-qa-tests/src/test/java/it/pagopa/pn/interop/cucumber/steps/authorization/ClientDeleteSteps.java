package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientDeleteSteps {
    private final IAuthorizationClient authorizationClientCreate;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;

    public ClientDeleteSteps(IAuthorizationClient authorizationClientCreate,
                             SharedStepsContext sharedStepsContext) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClientCreate.deleteClient(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient()));
    }
}
