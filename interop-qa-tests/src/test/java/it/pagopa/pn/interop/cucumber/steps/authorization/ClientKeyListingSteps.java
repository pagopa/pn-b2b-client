package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ClientKeyListingSteps {
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyListingSteps(IAuthorizationClient authorizationClient,
                                 SharedStepsContext sharedStepsContext,
                                 CommonUtils commonUtils,
                                 HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client")
    public void userAskClientKeyLists() {
        httpCallExecutor.performCall(() -> authorizationClient.getClientKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(), null));
    }

    @Then("si ottiene status code {int} e la lista di {int} chiavi")
    public void verifyStatusCodeAndListLength(int statusCode, int keyListSize) {
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(keyListSize, ((PublicKeys) httpCallExecutor.getResponse()).getKeys().size());
    }

    @When("l'utente richiede una operazione di listing delle chiavi di quel client create dall'utente {string}")
    public void retrieveKeysCreatedByUser(String role) {
        httpCallExecutor.performCall(() -> authorizationClient.getClientKeys(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                List.of(commonUtils.getUserId(sharedStepsContext.getTenantType(), role))));
    }
}
