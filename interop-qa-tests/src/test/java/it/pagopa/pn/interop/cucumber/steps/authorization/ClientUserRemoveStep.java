package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientUserRemoveStep {

    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientUserRemoveStep(IAuthorizationClient authorizationClient,
            SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @When("l'utente richiede la rimozione di quel membro dal client")
    public void removeUserFromClient() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeUserFromClient(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getClientCommonContext().getFirstClient(), sharedStepsContext.getClientCommonContext().getFirstUser()));
    }

}
