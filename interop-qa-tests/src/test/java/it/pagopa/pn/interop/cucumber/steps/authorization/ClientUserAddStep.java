package it.pagopa.pn.interop.cucumber.steps.authorization;

import static java.util.Objects.nonNull;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject3;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.util.UUID;

public class ClientUserAddStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
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
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID userId = identityService.getUserId(tenantType, "admin");
        InlineObject3 inlineObject = new InlineObject3().addUserIdsItem(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient(sharedStepsContext.getClientCommonContext().getFirstClient(), inlineObject));
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
        // QA-7236 TODO sharedStepsContext.getClientCommonContext().setAdminId(adminEditRequest.getAdminId());

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
        editClientAdmin(null, null);
    }

    @When("l'utente tenta di impostare {string} con ruolo {string} come amministratore del client")
    public void editClientAdminWithUser(String tenantType, String role) {
        UUID userId = identityService.getUserId(tenantType, role);
        var adminEditRequest = buildClientAdminEditBody(userId);
        // QA-7236 TODO sharedStepsContext.getClientCommonContext().setAdminId(adminEditRequest.getAdminId());

        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        editClientAdmin(clientId, adminEditRequest);
    }

    private void editClientAdmin(UUID clientId, Object adminEditRequest) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> authorizationClient.editClientAdmin(
                    clientId,
                    adminEditRequest));
    }

    @Then("l'amministratore del client è stato modificato correttamente")
    public void checkClientAdminEdited() {
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        pollingService.makePolling(
            () -> authorizationClient.getClient(clientId),
            client -> nonNull(client.getAdmin()) && client.getAdmin().getUserId().equals(sharedStepsContext.getClientCommonContext().getAdminId()),
            "L'amministratore del client non è stato modificato correttamente: adminId vuoto oppure difforme da quello indicato");
    }

    // QA-7236 TODO 07/05/2025: da adeguare non appena sarà rilasciata l'API in oggetto nel body
    // e nel tipo restituito, che dovrà essere coerente con IAuthorizationClient#editClientAdmin()
    private Object buildClientAdminEditBody(UUID adminId) {
        return new Object();
    }

}
