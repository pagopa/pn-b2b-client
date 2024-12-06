package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.resolver.TokenResolver;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;

public class ClientCreateStep {
    private final IAuthorizationClient authorizationClientCreate;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    private ResponseEntity<HttpStatus> createClientResponse;

    public ClientCreateStep(IAuthorizationClient authorizationClientCreate,
                            TokenResolver tokenResolver,
                            CommonUtils commonUtils,
                            HttpCallExecutor httpCallExecutor) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("l'utente è un {string} di {string}")
    public void setRole(String role, String institution) {
        String token = commonUtils.getToken(institution, role);
        authorizationClientCreate.setBearerToken(token);
    }

    @When("l'utente richiede la creazione di un client {string}")
    public void createClient(String clientKind) {
        if ((clientKind == "CONSUMER")) {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createConsumerClient("", createClientSeed()));
        } else {
            httpCallExecutor.performCall(() -> authorizationClientCreate.createApiClient("", createClientSeed()));
        }
    }

    private ClientSeed createClientSeed() {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client %d", new Random().nextInt(1000)));
        clientSeed.setDescription("Descrizione client");
        clientSeed.setMembers(List.of());
        return clientSeed;
    }
}
