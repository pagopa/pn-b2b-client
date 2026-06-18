package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.UUID;

public class PurposeRiskAnalysisAssignSteps {
    private final SharedStepsContext sharedStepsContext;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final IHttpExecutor httpCallExecutor;

    public PurposeRiskAnalysisAssignSteps(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator,
            @Qualifier("interopIdentityService") IdentityService identityService
    ) {
        this.sharedStepsContext = sharedStepsContext;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = identityService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string}")
    public void userAssignsReviewerWithMode(String mode) {
        String tenantType = sharedStepsContext.getTenantType();
        UUID reviewerId = identityService.getUserId(tenantType, "admin", 0);

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId));

        assignReviewer(payload);
    }

    @When("l'utente assegna un valutatore alla finalità senza specificare la modalità")
    public void userAssignsReviewerWithoutMode() {
        String tenantType = sharedStepsContext.getTenantType();
        UUID reviewerId = identityService.getUserId(tenantType, "admin", 0);

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewerIds(List.of(reviewerId));

        assignReviewer(payload);
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} senza specificare utenti valutatori")
    public void userAssignsReviewerWithoutUsers(String mode) {
        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of());

        assignReviewer(payload);
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} specificando più di un utente valutatore")
    public void userAssignsReviewerWithMultipleUsers(String mode) {
        String tenantType = sharedStepsContext.getTenantType();
        UUID reviewerId1 = identityService.getUserId(tenantType, "admin", 0);
        UUID reviewerId2 = identityService.getUserId(tenantType, "admin", 1);

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId1, reviewerId2));

        assignReviewer(payload);
    }

    private void assignReviewer(RiskAnalysisAssignmentSeed payload) {
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        httpCallExecutor.performCall(() -> purposeApiClient.assignRiskAnalysis(purposeId, payload));
    }

    private RiskAnalysisReviewMode toRiskAnalysisReviewMode(String mode) {
        return switch (mode) {
            case "ReviewerWritesReviewerSigns" -> RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS;
            case "AdminWritesReviewerSigns" -> RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS;
            default -> throw new IllegalArgumentException("Modalita di review non supportata: " + mode);
        };
    }
}
