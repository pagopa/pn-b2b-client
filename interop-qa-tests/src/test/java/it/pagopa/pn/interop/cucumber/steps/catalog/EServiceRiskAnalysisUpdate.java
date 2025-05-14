package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

public class EServiceRiskAnalysisUpdate {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public EServiceRiskAnalysisUpdate(ClientTokenConfigurator clientTokenConfigurator,
                                      SharedStepsContext sharedStepsContext,
                                      DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già aggiunto un'analisi del rischio a quell'e-service")
    public void tenantAddRiskAnalysisToEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        dataPreparationService.addRiskAnalysisToEService(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                new EServiceRiskAnalysisSeed()
                        .name(riskAnalysis.getName())
                        .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
        );
    }

    @When("l'utente aggiorna l'analisi del rischio di quell'e-service")
    public void updateRiskAnanlysisToEService() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(
                sharedStepsContext.getTenantType(), true);
        sharedStepsContext.getHttpCallExecutor().performCall(
                () -> clientTokenConfigurator.getEServiceClient().updateEServiceRiskAnalysis(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        sharedStepsContext.getRiskAnalysisCommonContext().getRiskAnalysisId(),
                        new EServiceRiskAnalysisSeed()
                                .name(riskAnalysis.getName())
                                .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
                )
        );
    }
}
