package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.service.IAuthorizationClientCreate;
import it.pagopa.interop.service.utils.SettableBearerToken;
import it.pagopa.pn.interop.cucumber.steps.authorization.factory.SessionTokenFactory;
import it.pagopa.pn.interop.cucumber.steps.authorization.resolver.TokenResolver;
import it.pagopa.pn.interop.cucumber.steps.authorization.utils.CommonSteps;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Random;

public class ClientCreateStep {
    private final IAuthorizationClientCreate authorizationClientCreate;
    private final TokenResolver tokenResolver;
    private final SessionTokenFactory sessionTokenFactory;
    private final CommonSteps commonSteps;
    private ResponseEntity<CreatedResource> createClientResponse;

    public ClientCreateStep(IAuthorizationClientCreate authorizationClientCreate,
                            TokenResolver tokenResolver,
                            SessionTokenFactory sessionTokenFactory,
                            CommonSteps commonSteps) {
        this.authorizationClientCreate = authorizationClientCreate;
        this.tokenResolver = tokenResolver;
        this.sessionTokenFactory = sessionTokenFactory;
        this.commonSteps = commonSteps;
    }

    @Given("l'utente è un {string} di {string}")
    public void setRole(String role, String institution) {
        String token = commonSteps.getToken(institution, role);
        authorizationClientCreate.setBearerToken(token);
    }

    @When("l'utente richiede la creazione di un client {string}")
    public void createClient(String clientKind) {
        try {
            CreatedResource createdResource = (clientKind == "CONSUMER")
                    ? authorizationClientCreate.createConsumerClient("", createClientSeed())
                    : authorizationClientCreate.createApiClient("", createClientSeed());
            createClientResponse = new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            createClientResponse = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Then("si ottiene status code {string}")
    public void verifyStatusCode(String statusCode) {
        Assertions.assertEquals(statusCode, createClientResponse.getStatusCode().toString());
    }

    private ClientSeed createClientSeed() {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setName(String.format("client %d", new Random().nextInt(1000)));
        clientSeed.setDescription("Descrizione client");
        clientSeed.setMembers(List.of());
        return clientSeed;
    }
}
