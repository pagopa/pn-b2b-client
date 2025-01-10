package it.pagopa.pn.interop.cucumber.steps.authorization;

import java.util.UUID;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject3;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientUserAddStep {

    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;

    public ClientUserAddStep(IAuthorizationClient authorizationClient,
            SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede l'aggiunta di un admin di {string} al client")
    public void addUsersToClient(String tenantType) {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        UUID userId = identityService.getUserId(tenantType, "admin");
        InlineObject3 inlineObject = new InlineObject3().addUserIdsItem(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(), inlineObject));
    }

}
