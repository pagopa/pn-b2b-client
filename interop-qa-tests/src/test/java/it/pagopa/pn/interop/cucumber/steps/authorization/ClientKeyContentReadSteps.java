package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyContentReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;

    public ClientKeyContentReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                     SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.commonUtils = sharedStepsContext.getCommonUtils();
    }

    @When("l'utente richiede la lettura del contenuto della chiave pubblica")
    public void readPublicKey() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.getEncodedClientKeyById(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getClientCommonContext().getFirstClient(),
                sharedStepsContext.getClientCommonContext().getKeyId()));
    }

}
