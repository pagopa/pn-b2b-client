package it.pagopa.pn.interop.cucumber.steps.authorization;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class ClientKeyContentReadSteps {
    private final IAuthorizationClient authorizationClient;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;
    private final IdentityService identityService;

    public ClientKeyContentReadSteps(IAuthorizationClient authorizationClient,
                                     SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.identityService = sharedStepsContext.getIdentityService();
    }

    @When("l'utente richiede la lettura del contenuto della chiave pubblica")
    public void readPublicKey() {
        identityService.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(() -> authorizationClient.getEncodedClientKeyById(sharedStepsContext.getXCorrelationId(),
                sharedStepsContext.getClientCommonContext().getFirstClient(),
                sharedStepsContext.getClientCommonContext().getKeyId()));
    }

}
