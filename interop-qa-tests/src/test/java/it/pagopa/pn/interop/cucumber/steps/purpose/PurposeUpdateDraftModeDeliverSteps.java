package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.UUID;

public class PurposeUpdateDraftModeDeliverSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;
    private final BFFDataPreparationService dataPreparationService;

    public PurposeUpdateDraftModeDeliverSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext, BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.dataPreparationService = dataPreparationService;
    }

    @When("l'utente aggiorna quella finalità per quell'e-service in erogazione diretta")
    public void updatePurposeDirect() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().updatePurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        new PurposeUpdateContent()
                                .title("some new title")
                                .description("some new description")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("some new free of charge reason")
                                .dailyCalls(49)
                )
        );
    }

    @When("l'utente aggiorna quella finalità per quell'e-service in erogazione diretta con una riskAnalysis in versione diversa da quella attualmente pubblicata")
    public void updatePurposeWithDifferentRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        int currentVersion = (int) Double.parseDouble(riskAnalysis.getRiskAnalysisForm().getVersion());
        riskAnalysis.getRiskAnalysisForm().setVersion(String.valueOf(currentVersion + 1));
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().updatePurpose(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()),
                        new PurposeUpdateContent()
                                .title("some new title")
                                .description("some new description")
                                .isFreeOfCharge(true)
                                .freeOfChargeReason("some new free of charge reason")
                                .dailyCalls(49)
                                .riskAnalysisForm(new RiskAnalysisFormSeed()
                                        .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                                        .answers(riskAnalysis.getRiskAnalysisForm().getAnswers()))
                )
        );
    }
}
