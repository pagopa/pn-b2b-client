package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientReadStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;

    public ClientReadStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede una operazione di lettura di quel client")
    public void getClient() {
        httpCallExecutor.performCall(() -> authorizationClient.getClient("", clientCommonSteps.getClients().get(0)));
    }

}
