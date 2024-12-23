package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class ClientPurposeAddSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final HttpCallExecutor httpCallExecutor;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;

    public ClientPurposeAddSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.sharedStepsContext = sharedStepsContext;
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @When("l'utente richiede l'associazione della finalità al client")
    public void userRetrievesFinalization() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() ->
                authorizationClient.addClientPurpose(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient(),
                        new PurposeAdditionDetailsSeed().purposeId(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())))
        );
    }
}
