package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class PurposeLatestRiskAnalysisTemplateReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;

    public PurposeLatestRiskAnalysisTemplateReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                              SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente richiede il template dell'analisi del rischio")
    public void requiredRiskAnalysisTemplate() {
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().retrieveLatestRiskAnalysisConfiguration()
        );
    }
}
