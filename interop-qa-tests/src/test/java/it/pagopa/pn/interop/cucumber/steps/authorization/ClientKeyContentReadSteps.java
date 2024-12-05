package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.service.IAuthorizationClient;

public class ClientKeyContentReadSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;

    public ClientKeyContentReadSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
    }

    @When("l'utente richiede la lettura del contenuto della chiave pubblica")
    public void readPublicKey() {
        clientCommonSteps.performCall(() -> authorizationClient.getEncodedClientKeyById("",
                clientCommonSteps.getClients().get(0),
                clientCommonSteps.getClientPublicKey()));
    }

}
