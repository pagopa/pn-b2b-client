package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.ClientAdminConfig;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient.Users;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

import static java.util.Objects.nonNull;

public class ClientUserAddStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;

    public ClientUserAddStep(ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente richiede l'aggiunta di un admin di {string} al client")
    public void addUsersToClient(String tenantType) {
        String role = "admin";
        addUsersToClient(role, tenantType);
    }

    @When("l'utente richiede l'aggiunta di un {string} di {string} al client")
    public void addUsersToClient(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID userId = identityService.getUserId(tenantType, role);
        Users users = new Users().addUserId(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), users));
    }

    @Given("l'utente effettua la modifica dell'amministratore del client indicando se stesso con successo")
    public void editClientAdminSuccessfully() {
        editClientAdmin();
        checkClientAdminEdited();
    }

    @When("l'utente tenta la modifica dell'amministratore del client indicando se stesso")
    public void editClientAdmin() {
        UUID userId = identityService.getUserId(sharedStepsContext.getTenantType(), "admin");
        var adminEditRequest = buildClientAdminEditBody(userId);
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        editClientAdmin(clientId, adminEditRequest);
    }

    @When("l'utente tenta la modifica dell'amministratore del client indicando l'admin numero {int} del suo ente")
    public void editClientAdminWithOtherAdmin(int adminIndex) {
        UUID userId = identityService.getUserId(sharedStepsContext.getTenantType(), "admin", --adminIndex);
        var adminEditRequest = buildClientAdminEditBody(userId);
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        editClientAdmin(clientId, adminEditRequest);
    }

    @When("l'utente tenta la modifica dell'amministratore di un client inesistente")
    public void editClientAdminNotFound() {
        UUID userId = identityService.getUserId(sharedStepsContext.getTenantType(), "admin");
        var adminEditRequest = buildClientAdminEditBody(userId);
        UUID clientId = UUID.randomUUID();
        editClientAdmin(clientId, adminEditRequest);
    }

    @When("l'utente tenta la modifica dell'amministratore indicando delle specifiche vuote")
    public void editClientAdminEmpty() {
        editClientAdmin(null, new ClientAdminConfig());
    }

    @When("l'utente tenta di impostare {string} con ruolo {string} come amministratore del client")
    public void editClientAdminWithUser(String tenantType, String role) {
        UUID userId = identityService.getUserId(tenantType, role);
        var adminEditRequest = buildClientAdminEditBody(userId);
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        editClientAdmin(clientId, adminEditRequest);
    }

    private void editClientAdmin(UUID clientId, ClientAdminConfig adminEditRequest) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> authorizationClient.editClientAdmin(
                    clientId,
                    adminEditRequest));
        if(httpCallExecutor.getResponseStatus().is2xxSuccessful()){
            sharedStepsContext.getClientCommonContext().setAdminId(adminEditRequest.getAdminId());
        }
    }

    @Then("l'amministratore del client è stato modificato correttamente")
    public void checkClientAdminEdited() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        pollingService.makePolling(
            () -> authorizationClient.getClient(clientId),
            client -> nonNull(client.getAdmin()) && client.getAdmin().getUserId().equals(sharedStepsContext.getClientCommonContext().getAdminId()),
            "L'amministratore del client non è stato modificato correttamente: adminId vuoto oppure difforme da quello indicato");
    }

    private ClientAdminConfig buildClientAdminEditBody(UUID adminId) {
        return ClientAdminConfig.builder()
            .adminId(adminId)
            .build();
    }
}
