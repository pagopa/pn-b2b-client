package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientPurposeAddSteps {
    private final IAuthorizationClient authorizationClient;
    private final HttpCallExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;

    public ClientPurposeAddSteps(IAuthorizationClient authorizationClient,
                                 HttpCallExecutor httpCallExecutor,
                                 SharedStepsContext sharedStepsContext,
                                 CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.httpCallExecutor = httpCallExecutor;
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = commonUtils;
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        commonUtils.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose("", sharedStepsContext.getClientCommonContext().getFirstClient(),
                        new PurposeAdditionDetailsSeed().purposeId(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())))
        );
    }
}
