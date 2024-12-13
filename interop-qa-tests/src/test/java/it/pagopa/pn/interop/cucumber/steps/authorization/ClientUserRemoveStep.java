package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientUserRemoveStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientUserRemoveStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            HttpCallExecutor httpCallExecutor,
            CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede la rimozione di quel membro dal client")
    public void removeUserFromClient() {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeUserFromClient("",
                clientCommonSteps.getClients().get(0), clientCommonSteps.getUsers().get(0)));
    }

}
