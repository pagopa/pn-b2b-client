package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientDeleteSteps {
    private final IAuthorizationClient authorizationClientCreate;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;

    public ClientDeleteSteps(IAuthorizationClient authorizationClientCreate, ClientCommonSteps clientCommonSteps, HttpCallExecutor httpCallExecutor) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        httpCallExecutor.performCall(() -> authorizationClientCreate.deleteClient("", clientCommonSteps.getClients().get(0)));
    }
}
