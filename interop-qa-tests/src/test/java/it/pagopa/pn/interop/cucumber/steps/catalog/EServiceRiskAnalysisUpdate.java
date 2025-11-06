package it.pagopa.pn.interop.cucumber.steps.catalog;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class EServiceRiskAnalysisUpdate {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final BFFDataPreparationService dataPreparationService;

    public EServiceRiskAnalysisUpdate(ClientTokenConfigurator clientTokenConfigurator,
                                      SharedStepsContext sharedStepsContext,
                                      BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} ha già aggiunto un'analisi del rischio a quell'e-service")
    public void tenantAddRiskAnalysisToEService(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        UUID riskAnalysisId = dataPreparationService.addRiskAnalysisToEService(
                sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                new EServiceRiskAnalysisSeed()
                        .name(riskAnalysis.getName())
                        .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
        );
        sharedStepsContext.getRiskAnalysisCommonContext().setRiskAnalysisId(riskAnalysisId);
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
                                .name(riskAnalysis.getName() + "- update")
                                .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm())
                )
        );
    }
}
