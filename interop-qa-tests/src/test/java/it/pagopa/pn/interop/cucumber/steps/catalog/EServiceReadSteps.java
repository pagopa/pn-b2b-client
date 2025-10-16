package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class EServiceReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final PollingService pollingService;

    public EServiceReadSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.pollingService = sharedStepsContext.getPollingService();
    }

    @When("l'utente richiede la lettura di quell'e-service")
    public void requireEServiceRead() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        IHttpExecutor httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        pollingService.makePolling(
            () -> httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId())),
            res -> httpCallExecutor.getResponseStatus().is2xxSuccessful(),
            "Lettura e-service non riuscita"
        );
    }
}
