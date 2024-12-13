package it.pagopa.pn.interop.cucumber.steps.authorization;

import java.util.List;

import it.pagopa.interop.authorization.service.utils.CommonUtils;
import org.junit.jupiter.api.Assertions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientUsersListingStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientUsersListingStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            HttpCallExecutor httpCallExecutor,
            CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede una operazione di listing dei membri di quel client")
    public void getClientUsers() {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        httpCallExecutor
                .performCall(() -> authorizationClient.getClientUsers("", clientCommonSteps.getClients().get(0)));
    }

    @Then("si ottiene status code 200 e la lista di {int} utenti")
    public void verifyStatusCodeAndListLength(int userListSize) {
        Assertions.assertEquals(200, httpCallExecutor.getClientResponse().value());
        Assertions.assertEquals(userListSize, ((List<CompactUser>) httpCallExecutor.getResponse()).size());
    }

}
