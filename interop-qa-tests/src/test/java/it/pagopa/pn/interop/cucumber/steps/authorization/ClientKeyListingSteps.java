package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ClientKeyListingSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyListingSteps(IAuthorizationClient authorizationClient,
                                 ClientCommonSteps clientCommonSteps,
                                 CommonUtils commonUtils,
                                 HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client")
    public void userAskClientKeyLists() {
        httpCallExecutor.performCall(() -> authorizationClient.getClientKeys("", clientCommonSteps.getClients().get(0), null));
    }

    @Then("si ottiene status code {int} e la lista di {int} chiavi")
    public void verifyStatusCodeAndListLength(int statusCode, int keyListSize) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(keyListSize, ((PublicKeys) httpCallExecutor.getResponse()).getKeys().size());
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client create dall'utente {string}")
    public void retrieveKeysCreatedByUser(String role) {
        httpCallExecutor.performCall(() -> authorizationClient.getClientKeys("", clientCommonSteps.getClients().get(0),
                List.of(commonUtils.getUserId(commonUtils.getTenantType(), role))));
    }
}
