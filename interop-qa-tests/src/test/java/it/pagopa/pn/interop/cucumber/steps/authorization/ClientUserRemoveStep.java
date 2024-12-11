package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientUserRemoveStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;

    public ClientUserRemoveStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede la rimozione di quel membro dal client")
    public void removeUserFromClient() {
        httpCallExecutor.performCall(() -> authorizationClient.removeUserFromClient("",
                clientCommonSteps.getClients().get(0), clientCommonSteps.getUsers().get(0)));
    }

}
