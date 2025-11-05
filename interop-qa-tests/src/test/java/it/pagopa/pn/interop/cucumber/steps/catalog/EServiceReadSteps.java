package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class EServiceReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public EServiceReadSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede la lettura di quell'e-service")
    public void requireEServiceRead() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        httpCallExecutor.performCall(
            () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                    sharedStepsContext.getEServicesCommonContext().getEserviceId()));
    }
}
