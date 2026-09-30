package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;
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
        AssignedReviewerActorRef reviewerActor = new AssignedReviewerActorRef(tenantType, "reviewer", 0);
        UUID reviewerId = identityService.getUserId(reviewerActor.tenantType(), reviewerActor.role(), reviewerActor.index());

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId));

        assignReviewer(payload, List.of(reviewerActor));
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} specificando un utente con ruolo {string}")
    public void userAssignsReviewerWithModeAndRole(String mode, String role) {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor = new AssignedReviewerActorRef(tenantType, role, 0);
        UUID reviewerId = identityService.getUserId(reviewerActor.tenantType(), reviewerActor.role(), reviewerActor.index());

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId));

        assignReviewer(payload, List.of(reviewerActor));
    }

    @Given("l'utente assegna un valutatore alla finalità in modalità {string} con successo")
    public void userAssignsReviewerWithModeSuccessfully(String mode) {
        userAssignsReviewerWithMode(mode);

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> purpose.getReviewerWorkflow() != null,
                String.format("Reviewer workflow non creato per la finalita %s", purposeId)
        );
    }

    // NOTA: al momento questo step non e' riusabile in esecuzione ordinaria,
    // perche' la piattaforma espone una sola utenza reviewer disponibile.
    // Mantenerlo solo per readiness futura quando saranno disponibili >= 2 reviewer.
    @When("l'utente assegna i reviewer previsti alla finalità in modalità {string}")
    public void userAssignsExpectedReviewersWithMode(String mode) {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor1 = new AssignedReviewerActorRef(tenantType, "reviewer", 0);
        AssignedReviewerActorRef reviewerActor2 = new AssignedReviewerActorRef(tenantType, "reviewer", 1);
        UUID reviewerId1 = identityService.getUserId(reviewerActor1.tenantType(), reviewerActor1.role(), reviewerActor1.index());
        UUID reviewerId2 = identityService.getUserId(reviewerActor2.tenantType(), reviewerActor2.role(), reviewerActor2.index());

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId1, reviewerId2));

        assignReviewer(payload, List.of(reviewerActor1, reviewerActor2));
    }

    @Given("l'utente assegna i reviewer previsti alla finalità in modalità {string} con successo")
    public void userAssignsExpectedReviewersWithModeSuccessfully(String mode) {
        userAssignsExpectedReviewersWithMode(mode);

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> purpose.getReviewerWorkflow() != null,
                String.format("Reviewer workflow non creato per la finalita %s", purposeId)
        );
    }

    @When("l'utente assegna un valutatore alla finalità senza specificare la modalità")
    public void userAssignsReviewerWithoutMode() {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor = new AssignedReviewerActorRef(tenantType, "reviewer", 0);
        UUID reviewerId = identityService.getUserId(reviewerActor.tenantType(), reviewerActor.role(), reviewerActor.index());

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewerIds(List.of(reviewerId));

        assignReviewer(payload, List.of(reviewerActor));
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} senza specificare utenti valutatori")
    public void userAssignsReviewerWithoutUsers(String mode) {
        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of());

        assignReviewer(payload, List.of());
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} specificando più di un utente valutatore")
    public void userAssignsReviewerWithMultipleUsers(String mode) {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor1 = new AssignedReviewerActorRef(tenantType, "admin", 0);
        AssignedReviewerActorRef reviewerActor2 = new AssignedReviewerActorRef(tenantType, "admin", 1);
        UUID reviewerId1 = identityService.getUserId(reviewerActor1.tenantType(), reviewerActor1.role(), reviewerActor1.index());
        UUID reviewerId2 = identityService.getUserId(reviewerActor2.tenantType(), reviewerActor2.role(), reviewerActor2.index());

        RiskAnalysisAssignmentSeed payload = new RiskAnalysisAssignmentSeed()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId1, reviewerId2));

        assignReviewer(payload, List.of(reviewerActor1, reviewerActor2));
    }

    private void assignReviewer(RiskAnalysisAssignmentSeed payload, List<AssignedReviewerActorRef> assignedReviewerActors) {
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        if (assignedReviewerActors != null && !assignedReviewerActors.isEmpty()) {
            sharedStepsContext.getRiskAnalysisCommonContext().setAssignedReviewerActors(List.copyOf(assignedReviewerActors));
        }

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
