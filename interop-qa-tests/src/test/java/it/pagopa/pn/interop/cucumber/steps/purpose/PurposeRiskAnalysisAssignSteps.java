package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.ReviewerWorkflow;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.ReviewerPayloadOwner;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.interop.purpose.service.RiskAnalysisReviewMode;
import it.pagopa.interop.purpose.service.IM2MV3PurposeClient;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.RiskAnalysisAssignmentRequest;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.HashSet;
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

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId))
                .build();

        assignReviewer(payload, List.of(reviewerActor));
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} specificando un utente con ruolo {string}")
    public void userAssignsReviewerWithModeAndRole(String mode, String role) {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor = new AssignedReviewerActorRef(tenantType, role, 0);
        UUID reviewerId = identityService.getUserId(reviewerActor.tenantType(), reviewerActor.role(), reviewerActor.index());

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId))
                .build();

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

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId1, reviewerId2))
                .build();

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

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewerIds(List.of(reviewerId))
                .build();

        assignReviewer(payload, List.of(reviewerActor));
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} senza specificare utenti valutatori")
    public void userAssignsReviewerWithoutUsers(String mode) {
        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of())
                .build();

        assignReviewer(payload, List.of());
    }

    @When("l'utente assegna un valutatore alla finalità in modalità {string} specificando più di un utente valutatore")
    public void userAssignsReviewerWithMultipleUsers(String mode) {
        String tenantType = sharedStepsContext.getTenantType();
        AssignedReviewerActorRef reviewerActor1 = new AssignedReviewerActorRef(tenantType, "admin", 0);
        AssignedReviewerActorRef reviewerActor2 = new AssignedReviewerActorRef(tenantType, "admin", 1);
        UUID reviewerId1 = identityService.getUserId(reviewerActor1.tenantType(), reviewerActor1.role(), reviewerActor1.index());
        UUID reviewerId2 = identityService.getUserId(reviewerActor2.tenantType(), reviewerActor2.role(), reviewerActor2.index());

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(toRiskAnalysisReviewMode(mode))
                .reviewerIds(List.of(reviewerId1, reviewerId2))
                .build();

        assignReviewer(payload, List.of(reviewerActor1, reviewerActor2));
    }

    @Given("l'utente assegna con successo i valutatori {integerList} alla finalità in modalità {riskAnalysisReviewMode}")
    public void assignReviewersAndModeSuccessfully(List<Integer> reviewers, RiskAnalysisReviewMode mode) {
        assignReviewersAndMode(reviewers, mode);
        verifyAssignmentResponseSuccessful();
        // Perché è accettato, come valore correttamente interpretato come vuoto, "null", non la lista vuota.
        verifyReviewersAndMode(reviewers == null ? List.of() : reviewers, mode);
        sharedStepsContext.getRiskAnalysisCommonContext().rememberInitialReviewers();
    }

    @When("l'utente tenta di assegnare la finalità senza valutatori in modalità {riskAnalysisReviewMode}")
    public void assignWithoutReviewers(RiskAnalysisReviewMode mode) {
        assignReviewersAndMode(List.of(), mode);
    }

    @Given("l'utente assegna con successo la finalità senza valutatori in modalità {riskAnalysisReviewMode}")
    public void assignWithoutReviewersSuccessfully(RiskAnalysisReviewMode mode) {
        assignReviewersAndModeSuccessfully(null, mode);
    }

    @Then("alla finalità non sono associati valutatori e la modalità è {riskAnalysisReviewMode}")
    public void verifyNoReviewersAndMode(RiskAnalysisReviewMode mode) {
        verifyReviewersAndMode(List.of(), mode);
    }

    private void verifyAssignmentResponseSuccessful() {
        org.assertj.core.api.Assertions.assertThat(httpCallExecutor.getResponseStatus())
                .as("L'assegnazione dei valutatori deve restituire uno status HTTP di successo")
                .isNotNull();
        org.assertj.core.api.Assertions.assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful())
                .as("Status HTTP dell'assegnazione: %s", httpCallExecutor.getResponseStatus())
                .isTrue();
    }

    @Then("alla finalità risultano associati esclusivamente i seguenti valutatori: {reviewerList}, in modalità {riskAnalysisReviewMode}")
    public void verifyReviewersAndMode(List<Integer> reviewers, RiskAnalysisReviewMode mode) {
        String tenantType = sharedStepsContext.getTenantType();
        List<UUID> expectedIds = reviewers.stream()
                .map(index -> identityService.getUserId(tenantType, "reviewer", index - 1))
                .toList();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> {
                    ReviewerWorkflow workflow = purpose.getReviewerWorkflow();
                    if (purpose.getRiskAnalysisReviewMode() == null
                            || !purpose.getRiskAnalysisReviewMode().getValue().equals(mode.getValue())) {
                        return false;
                    }
                    if (workflow == null) {
                        return mode == RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS && expectedIds.isEmpty();
                    }
                    if (workflow.getReviewers() == null) {
                        return mode == RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS && expectedIds.isEmpty();
                    }
                    List<UUID> actualIds = workflow.getReviewers().stream()
                            .map(reviewer -> reviewer.getUserId()).toList();
                    return actualIds.size() == expectedIds.size()
                            && new HashSet<>(actualIds).equals(new HashSet<>(expectedIds));
                },
                "Reviewer o modalità non corrispondenti alla nuova assegnazione: " + reviewers + ", " + mode
        );
    }

    @When("l'utente tenta di assegnare alla finalità i seguenti valutatori: {reviewerList}, in modalità {riskAnalysisReviewMode}")
    public void assignReviewersAndMode(List<Integer> reviewers, RiskAnalysisReviewMode mode) {
        if (reviewers != null && reviewers.stream().anyMatch(index -> index < 1)) {
            throw new IllegalArgumentException("Gli indici dei valutatori devono essere positivi");
        }
        ReviewerPayloadOwner payloadOwner = sharedStepsContext.getRiskAnalysisCommonContext().getReviewerPayloadOwner();
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        String tenantType = payloadOwner != null && payloadOwner.purposeId().equals(purposeId)
                ? payloadOwner.tenantType() : sharedStepsContext.getTenantType();
        List<AssignedReviewerActorRef> reviewerActors = (reviewers == null ? List.<Integer>of() : reviewers).stream()
                .map(index -> new AssignedReviewerActorRef(tenantType, "reviewer", index-1))
                .toList();
        List<UUID> reviewerIds = reviewerActors.stream()
                .map(actor -> identityService.getUserId(actor.tenantType(), actor.role(), actor.index()))
                .toList();

        RiskAnalysisAssignmentRequest payload = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(mode)
                .reviewerIds(reviewers == null ? null : reviewerIds)
                .build();

        assignReviewer(payload, reviewerActors);
    }

    private void assignReviewer(RiskAnalysisAssignmentRequest payload, List<AssignedReviewerActorRef> assignedReviewerActors) {
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        IM2MV3PurposeClient m2mV3PurposeClient = clientTokenConfigurator.getM2mV3PurposeClient();

        if (assignedReviewerActors != null) {
            sharedStepsContext.getRiskAnalysisCommonContext().setAssignedReviewerActors(List.copyOf(assignedReviewerActors));
        }

        // Si esegue anche se == null per mantenere compatibilità con i test scritti in precedenza
        if (sharedStepsContext.getApiSet() == null || sharedStepsContext.getApiSet() == ApiProfile.ApiSet.BFF) {
            httpCallExecutor.performCall(() -> purposeApiClient.assignRiskAnalysis(purposeId, payload.toBFFRequest()));
        } else if(ApiProfile.ApiSet.M2M.equals(sharedStepsContext.getApiSet())) {
            httpCallExecutor.performCall(() -> m2mV3PurposeClient.assignRiskAnalysis(purposeId, payload));
        }
    }

    private RiskAnalysisReviewMode toRiskAnalysisReviewMode(String mode) {
        return switch (mode) {
            case "AdminWritesAdminSigns" -> RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS;
            case "ReviewerWritesReviewerSigns" -> RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS;
            case "AdminWritesReviewerSigns" -> RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS;
            default -> throw new IllegalArgumentException("Modalita di review non supportata: " + mode);
        };
    }
}
