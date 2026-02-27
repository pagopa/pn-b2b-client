package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.List;
import org.junit.jupiter.api.Assertions;

public class ClientUsersListingStep {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public ClientUsersListingStep(ClientTokenConfigurator clientTokenConfigurator,
                                  SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede una operazione di listing dei membri di quel client")
    public void getClientUsers() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor
                .performCall(() -> authorizationClient.getClientUsers(sharedStepsContext.getClientCommonContext().getFirstClient()));
    }

    @Then("si ottiene status code 200 e la lista di {int} utenti")
    public void verifyStatusCodeAndListLength(int userListSize) {
        Assertions.assertEquals(200, httpCallExecutor.getResponseStatus().value());
        Assertions.assertEquals(userListSize, ((List<CompactUser>) httpCallExecutor.getResponse()).size());
    }

}
