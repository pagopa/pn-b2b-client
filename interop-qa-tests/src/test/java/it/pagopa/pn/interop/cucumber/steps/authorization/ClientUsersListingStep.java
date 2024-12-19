package it.pagopa.pn.interop.cucumber.steps.authorization;

import java.util.List;

import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.utils.HttpCallExecutor;

public class ClientUsersListingStep {

    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientUsersListingStep(IAuthorizationClient authorizationClient,
            SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @When("l'utente richiede una operazione di listing dei membri di quel client")
    public void getClientUsers() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor
                .performCall(() -> authorizationClient.getClientUsers(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient()));
    }

    @Then("si ottiene status code 200 e la lista di {int} utenti")
    public void verifyStatusCodeAndListLength(int userListSize) {
        Assertions.assertEquals(200, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(userListSize, ((List<CompactUser>) httpCallExecutor.getResponse()).size());
    }

}
