package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject4;
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

    public ClientUserAddStep(ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede l'aggiunta di un admin di {string} al client")
    public void addUsersToClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID userId = identityService.getUserId(tenantType, "admin");
        InlineObject4 inlineObject = new InlineObject4().addUserIdsItem(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(), inlineObject));
    }

}
