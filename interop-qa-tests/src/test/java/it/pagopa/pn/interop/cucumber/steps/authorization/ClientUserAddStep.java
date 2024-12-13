package it.pagopa.pn.interop.cucumber.steps.authorization;

import java.util.UUID;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject2;
import it.pagopa.pn.interop.cucumber.steps.utils.HttpCallExecutor;

public class ClientUserAddStep {

    private final IAuthorizationClient authorizationClient;
    private final ClientCommonSteps clientCommonSteps;
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;

    public ClientUserAddStep(IAuthorizationClient authorizationClient,
            ClientCommonSteps clientCommonSteps,
            CommonUtils commonUtils,
            HttpCallExecutor httpCallExecutor) {
        this.authorizationClient = authorizationClient;
        this.clientCommonSteps = clientCommonSteps;
        this.commonUtils = commonUtils;
        this.httpCallExecutor = httpCallExecutor;
    }

    @When("l'utente richiede l'aggiunta di un admin di {string} al client")
    public void addUsersToClient(String tenantType) {
        commonUtils.setBearerToken(commonUtils.getUserToken());
        UUID userId = commonUtils.getUserId(tenantType, "admin");
        InlineObject2 inlineObject = new InlineObject2().addUserIdsItem(userId);
        httpCallExecutor.performCall(
                () -> authorizationClient.addUsersToClient("", clientCommonSteps.getClients().get(0), inlineObject));
    }

}
