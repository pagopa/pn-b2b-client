package it.pagopa.pn.interop.cucumber.steps.authorization;

import java.util.UUID;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject3;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientUserAddStep {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientUserAddStep(ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede l'aggiunta di un admin di {string} al client")
    public void addUsersToClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID userId = commonUtils.getUserId(tenantType, "admin");
        InlineObject3 inlineObject = new InlineObject3().addUserIdsItem(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(), inlineObject));
    }

}
