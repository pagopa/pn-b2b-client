package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.service.IAuthorizationClient;
import org.springframework.http.HttpStatus;

public class ClientDeleteSteps {
    private final IAuthorizationClient authorizationClientCreate;
    private final ClientCommonSteps clientCommonSteps;

    public ClientDeleteSteps(IAuthorizationClient authorizationClientCreate, ClientCommonSteps clientCommonSteps) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.clientCommonSteps = clientCommonSteps;
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        clientCommonSteps.performCall(() -> authorizationClientCreate.deleteClient("", clientCommonSteps.getClients().get(0)));
    }
}
