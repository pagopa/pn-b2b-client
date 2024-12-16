package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientDeleteSteps {
    private final IAuthorizationClient authorizationClientCreate;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientDeleteSteps(IAuthorizationClient authorizationClientCreate,
                             SharedStepsContext sharedStepsContext,
                             HttpCallExecutor httpCallExecutor,
                             CommonUtils commonUtils) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClientCreate.deleteClient("", sharedStepsContext.getClientCommonContext().getFirstClient()));
    }
}
