package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientKeyDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;

    public ClientKeyDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("{string} ha già rimosso l'utente con ruolo {string} dai membri di quel client")
    public void removeClientMemberByRole(String tenantType, String role) {
        removeMemberFromClient(sharedStepsContext.getClientCommonContext().getFirstClient(), identityService.getUserId(tenantType, role));
    }

    @When("l'utente richiede una operazione di cancellazione della chiave di quel client")
    public void deleteClientKeyById() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.deleteClientKeyById(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getClientCommonContext().getFirstClient(), sharedStepsContext.getClientCommonContext().getKeyId()));
    }

    public void removeMemberFromClient(UUID clientId, UUID userId) {
        authorizationClient.removeUserFromClient(sharedStepsContext.getXCorrelationId(), clientId, userId);
        pollingService.makePolling(
                () -> authorizationClient.getClientUsers(sharedStepsContext.getXCorrelationId(), clientId),
                res -> res.stream().noneMatch(user -> user.getUserId().equals(userId)),
                "There was an error while retrieving the client user!"
        );
    }


}
