package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class PurposeSpecificRiskAnalysisTemplateReadSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final HttpCallExecutor httpCallExecutor;

    public PurposeSpecificRiskAnalysisTemplateReadSteps(ClientTokenConfigurator clientTokenConfigurator,
                                                        SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente richiede la versione {string} del template dell'analisi del rischio")
    public void userRequireTemplateVersion(String version) {
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().retrieveRiskAnalysisConfigurationByVersion(
                        version, sharedStepsContext.getEServicesCommonContext().getEserviceId()
                )
        );
    }
}
