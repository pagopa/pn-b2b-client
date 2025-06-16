package it.pagopa.pn.interop.cucumber.steps.authorization;

import static java.util.Objects.isNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class ClientUserRemoveStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final IdentityService identityService;

    public ClientUserRemoveStep(ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede la rimozione di quel membro dal client")
    public void removeUserFromClient() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.removeUserFromClient(
                sharedStepsContext.getClientCommonContext().getFirstClient(),
            sharedStepsContext.getClientCommonContext().getFirstUser()));
    }

    @Given("l'utente effettua la rimozione dell'amministratore del client con successo")
    public void removeClientAdminSuccessfully() {
        removeClientAdmin();
        checkClientAdminRemoved();
    }

    @When("l'utente tenta la rimozione dell'amministratore del client")
    public void removeClientAdmin() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID adminId = sharedStepsContext.getClientCommonContext().getAdminId();
        removeClientAdmin(clientId, adminId);
    }

    @When("l'utente tenta la rimozione dell'amministratore del client specificando un clientId inesistente ed il proprio adminId")
    public void removeClientAdminWithInvalidClientId() {
        UUID clientId = UUID.randomUUID();
        UUID adminId = identityService.getUserId(sharedStepsContext.getTenantType(), "admin");
        removeClientAdmin(clientId, adminId);
    }

    @When("l'utente tenta la rimozione dell'amministratore del client specificando un adminId inesistente")
    public void removeClientAdminWithInvalidAdminId() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID adminId = UUID.randomUUID();
        removeClientAdmin(clientId, adminId);
    }

    @When("l'utente tenta la rimozione dell'amministratore del client indicando delle specifiche vuote")
    public void removeClientAdminWithEmptySpec() {
        removeClientAdmin(null, null);
    }

    private void removeClientAdmin(UUID clientId, UUID adminId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
            () -> authorizationClient.deleteClientAdmin(clientId, adminId));
    }

    @Then("l'amministratore del client è stato rimosso correttamente")
    public void checkClientAdminRemoved() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        pollingService.makePolling(
            () -> authorizationClient.getClient(clientId),
            client -> isNull(client.getAdmin()),
            "L'amministratore del client non è stato rimosso correttamente: adminId non vuoto come previsto");
        sharedStepsContext.getClientCommonContext().setAdminId(null);
    }
}
