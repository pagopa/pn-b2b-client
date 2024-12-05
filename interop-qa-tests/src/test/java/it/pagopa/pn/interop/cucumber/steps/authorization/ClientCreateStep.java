package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.factory.SessionTokenFactory;
import it.pagopa.interop.resolver.TokenResolver;
import it.pagopa.interop.service.utils.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;

public class ClientCreateStep {
    private final IAuthorizationClient authorizationClientCreate;
    private final TokenResolver tokenResolver;
    private final SessionTokenFactory sessionTokenFactory;
    private final CommonUtils commonUtils;
    private final ClientCommonSteps clientCommonSteps;
    private ResponseEntity<HttpStatus> createClientResponse;

    public ClientCreateStep(IAuthorizationClient authorizationClientCreate,
                            TokenResolver tokenResolver,
                            SessionTokenFactory sessionTokenFactory,
                            CommonUtils commonUtils,
                            ClientCommonSteps clientCommonSteps) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.tokenResolver = tokenResolver;
        this.sessionTokenFactory = sessionTokenFactory;
        this.commonUtils = commonUtils;
        this.clientCommonSteps = clientCommonSteps;
    }

    @Given("l'utente è un {string} di {string}")
    public void setRole(String role, String institution) {
        String token = commonUtils.getToken(institution, role);
        authorizationClientCreate.setBearerToken(token);
    }

    @When("l'utente richiede la creazione di un client {string}")
    public void createClient(String clientKind) {
        if ((clientKind == "CONSUMER")) {
            clientCommonSteps.performCall(() -> authorizationClientCreate.createConsumerClient("", createClientSeed()));
        } else {
            clientCommonSteps.performCall(() -> authorizationClientCreate.createApiClient("", createClientSeed()));
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
