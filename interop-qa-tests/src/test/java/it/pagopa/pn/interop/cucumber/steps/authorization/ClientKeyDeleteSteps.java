package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.service.IAuthorizationClient;
import it.pagopa.interop.service.utils.CommonUtils;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClientKeyDeleteSteps {
    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;

    public ClientKeyDeleteSteps(IAuthorizationClient authorizationClient, ClientCommonSteps clientCommonSteps, CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
    }

    @Given("{string} ha già rimosso l'utente con ruolo {string} dai membri di quel client")
    public void removeClientMemberByRole(String tenantType, String role) {
        removeMemberFromClient(clientCommonSteps.getClients().get(0), commonUtils.getUserId(tenantType, role));
    }

    @When("l'utente richiede una operazione di cancellazione della chiave di quel client")
    public void deleteClientKeyById() {
        clientCommonSteps.performCall(() -> authorizationClient.deleteClientKeyById("",
                clientCommonSteps.getClients().get(0), clientCommonSteps.getClientPublicKey()));
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
