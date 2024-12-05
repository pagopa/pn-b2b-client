package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ClientKeyListingSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;

    public ClientKeyListingSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps, CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client")
    public void userAskClientKeyLists() {
        clientCommonSteps.performCall(() -> authorizationClient.getClientKeys("", clientCommonSteps.getClients().get(0), null));
    }

    @Then("si ottiene status code {int} e la lista di {int} chiavi")
    public void verifyStatusCodeAndListLength(int statusCode, int keyListSize) {
        Assertions.assertEquals(statusCode, clientCommonSteps.getClientResponse().value());
        Assertions.assertEquals(keyListSize, ((PublicKeys) clientCommonSteps.getResponse()).getKeys().size());
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client create dall'utente {string}")
    public void retrieveKeysCreatedByUser(String tenantType, String role) {
        clientCommonSteps.performCall(() -> authorizationClient.getClientKeys("", clientCommonSteps.getClients().get(0),
                List.of(commonUtils.getUserId(tenantType, role))));
    }
}
