package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientDeleteSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClientCreate;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientDeleteSteps(ClientTokenConfigurator clientTokenConfigurator,
                             SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClientCreate = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @When("l'utente richiede una operazione di cancellazione di quel client")
    public void deleteClient() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClientCreate.deleteClient(sharedStepsContext.getXCorrelationId(), sharedStepsContext.getClientCommonContext().getFirstClient()));
    }
}
