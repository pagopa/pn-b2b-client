package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientKeyDeleteSteps {
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyDeleteSteps(IAuthorizationClient authorizationClient,
                                SharedStepsContext sharedStepsContext,
                                CommonUtils commonUtils,
                                HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @Given("{string} ha già rimosso l'utente con ruolo {string} dai membri di quel client")
    public void removeClientMemberByRole(String tenantType, String role) {
        removeMemberFromClient(sharedStepsContext.getClientCommonContext().getFirstClient(), commonUtils.getUserId(tenantType, role));
    }

    @When("l'utente richiede una operazione di cancellazione della chiave di quel client")
    public void deleteClientKeyById() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.deleteClientKeyById("",
                sharedStepsContext.getClientCommonContext().getFirstClient(), sharedStepsContext.getClientCommonContext().getKeyId()));
    }

    public void removeMemberFromClient(UUID clientId, UUID userId) {
        authorizationClient.removeUserFromClient("", clientId, userId);
        commonUtils.makePolling(
                () -> authorizationClient.getClientUsers("", clientId),
                res -> res.stream().noneMatch(user -> user.getUserId().equals(userId)),
                ""
        );
    }


}
