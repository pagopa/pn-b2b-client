package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientKeyContentReadSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyContentReadSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps, HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede la lettura del contenuto della chiave pubblica")
    public void readPublicKey() {
        httpCallExecutor.performCall(() -> authorizationClient.getEncodedClientKeyById("",
                clientCommonSteps.getClients().get(0),
                clientCommonSteps.getClientPublicKey()));
    }

}
