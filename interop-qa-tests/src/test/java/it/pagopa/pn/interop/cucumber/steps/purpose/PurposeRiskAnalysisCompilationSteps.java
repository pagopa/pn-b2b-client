package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisRejectionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.util.List;
import java.util.UUID;

public class PurposeRiskAnalysisCompilationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;

    public PurposeRiskAnalysisCompilationSteps(
            ClientTokenConfigurator clientTokenConfigurator,
            SharedStepsContext sharedStepsContext,
            BFFDataPreparationService dataPreparationService
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("il valutatore assegnato compila l'analisi del rischio della finalità")
    public void assignedReviewerCompilesRiskAnalysis() {
        String previousToken = sharedStepsContext.getUserToken();
        List<AssignedReviewerActorRef> assignedReviewerActors = sharedStepsContext.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        if (assignedReviewerActors == null || assignedReviewerActors.isEmpty()) {
            throw new IllegalStateException("Nessun valutatore assegnato presente in contesto");
        }
        AssignedReviewerActorRef assignedReviewerActor = assignedReviewerActors.get(assignedReviewerActors.size() - 1);
        String reviewerToken = resolveAssignedReviewerToken(assignedReviewerActor);

        try {
            clientTokenConfigurator.setBearerToken(reviewerToken);

            RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
            RiskAnalysisFormSeed riskAnalysisForm = new RiskAnalysisFormSeed()
                    .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                    .answers(riskAnalysis.getRiskAnalysisForm().getAnswers());

            UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
            httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().compileRiskAnalysisForm(purposeId, riskAnalysisForm));
        } finally {
            clientTokenConfigurator.setBearerToken(previousToken);
        }
    }

    private String resolveAssignedReviewerToken(AssignedReviewerActorRef assignedReviewerActor) {
        IdentityService identityService = sharedStepsContext.getIdentityService();
        try {
            return identityService.getToken(
                    assignedReviewerActor.tenantType(),
                    assignedReviewerActor.role(),
                    assignedReviewerActor.index()
            );
        } catch (Exception ex) {
            throw new IllegalStateException(String.format(
                    "Impossibile risolvere il token per il valutatore assegnato (%s,%s,%d)",
                    assignedReviewerActor.tenantType(),
                    assignedReviewerActor.role(),
                    assignedReviewerActor.index()
            ), ex);
        }
    }

    @And("lo stato della compilazione dell'analisi del rischio è {string}")
    public void verifyRiskAnalysisSigningState(String expectedState) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        RiskAnalysisSigningState expectedSigningState = RiskAnalysisSigningState.valueOf(expectedState.toUpperCase());

        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> purpose.getReviewerWorkflow() != null &&
                          purpose.getReviewerWorkflow().getSigningState() == expectedSigningState,
                String.format("The risk analysis signing state is not %s", expectedState)
        );
    }

    @When("compila l'analisi del rischio della finalità")
    public void compilesRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().compileRiskAnalysisForm(purposeId, riskAnalysis.getRiskAnalysisForm()));
    }

    @When("compila l'analisi del rischio tramite endpoint generico")
    public void compilesRiskAnalysisViaGenericEndpoint() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());

        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        riskAnalysis.getRiskAnalysisForm().getAnswers().put("institutionalPurpose", List.of("a caso"));

        // Use generic purpose update endpoint instead of dedicated risk analysis form endpoint
        PurposeUpdateContent updateContent = new PurposeUpdateContent()
                .title("Updated title")
                .description("Updated description")
                .dailyCalls(1)
                .isFreeOfCharge(true)
                .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().updatePurpose(purposeId, updateContent));
    }

    @When("il valutatore assegnato rifiuta la propria compilazione dell'analisi del rischio")
    public void assignedReviewerRejectsOwnRiskAnalysisCompilation() {
        String previousToken = sharedStepsContext.getUserToken();
        List<AssignedReviewerActorRef> assignedReviewerActors = sharedStepsContext.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        if (assignedReviewerActors == null || assignedReviewerActors.isEmpty()) {
            throw new IllegalStateException("Nessun valutatore assegnato presente in contesto");
        }
        AssignedReviewerActorRef assignedReviewerActor = assignedReviewerActors.get(assignedReviewerActors.size() - 1);
        String reviewerToken = resolveAssignedReviewerToken(assignedReviewerActor);

        try {
            clientTokenConfigurator.setBearerToken(reviewerToken);

            UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
            RiskAnalysisRejectionSeed payload = new RiskAnalysisRejectionSeed()
                    .rejectionReason("Rifiuto della propria compilazione");

            httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().rejectRiskAnalysis(purposeId, payload));
        } finally {
            clientTokenConfigurator.setBearerToken(previousToken);
        }
    }
}

