package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class PurposeRiskAnalysisCompilationSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IHttpExecutor httpCallExecutor;
    private RiskAnalysis compiledRiskAnalysis;

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
        AssignedReviewerActorRef actor = getLastAssignedReviewerActor();
        withReviewerToken(actor, () -> {
            RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
            RiskAnalysisFormSeed riskAnalysisForm = new RiskAnalysisFormSeed()
                    .version(riskAnalysis.getRiskAnalysisForm().getVersion())
                    .answers(riskAnalysis.getRiskAnalysisForm().getAnswers());
            UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
            httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().compileRiskAnalysisForm(purposeId, riskAnalysisForm));
        });
    }

    @Given("il valutatore assegnato compila l'analisi del rischio della finalità con successo")
    public void assignedReviewerCompilesRiskAnalysisSuccessfully() {
        assignedReviewerCompilesRiskAnalysis();
        verifyRiskAnalysisSigningState("ASSIGNED");
    }

    @When("uno dei reviewer assegnati compila l'analisi del rischio della finalità")
    public void oneAssignedReviewerCompilesRiskAnalysis() {
        assignedReviewerCompilesRiskAnalysis();
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

    /**
     * Salva il token corrente, imposta il token del reviewer indicato, esegue l'azione,
     * e ripristina il token originale in un blocco try/finally.
     */
    private void withReviewerToken(AssignedReviewerActorRef actor, Runnable action) {
        String previousToken = sharedStepsContext.getUserToken();
        String reviewerToken = resolveAssignedReviewerToken(actor);
        try {
            clientTokenConfigurator.setBearerToken(reviewerToken);
            action.run();
        } finally {
            clientTokenConfigurator.setBearerToken(previousToken);
        }
    }

    /**
     * Restituisce l'ultimo reviewer assegnato in contesto, o lancia eccezione se assente.
     */
    private AssignedReviewerActorRef getLastAssignedReviewerActor() {
        List<AssignedReviewerActorRef> actors = getAssignedReviewerActorsOrThrow(
                1, "Nessun valutatore assegnato presente in contesto"
        );
        return actors.get(actors.size() - 1);
    }

    /**
     * Esegue il rifiuto dell'analisi del rischio come ultimo reviewer assegnato,
     * usando il payload fornito.
     */
    private void rejectAsLastAssignedReviewer(RiskAnalysisRejectionSeed payload) {
        AssignedReviewerActorRef actor = getLastAssignedReviewerActor();
        withReviewerToken(actor, () -> {
            UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
            httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().rejectRiskAnalysis(purposeId, payload));
        });
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

    @Then("non sussiste alcun workflow di revisione in corso")
    public void verifyNoReviewerWorkflowInProgress() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> purpose.getReviewerWorkflow() == null,
                "Reviewer workflow presente ma non atteso"
        );
    }

    @When("compila l'analisi del rischio della finalità")
    public void compilesRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        compiledRiskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().compileRiskAnalysisForm(purposeId, compiledRiskAnalysis.getRiskAnalysisForm()));
    }

    @When("compila l'analisi del rischio tramite endpoint generico")
    public void compilesRiskAnalysisViaGenericEndpoint() {
        compilesRiskAnalysisViaGenericEndpoint(a -> {});
    }

    @When("compila l'analisi del rischio tramite endpoint generico introducendo una variazione")
    public void compilesRiskAnalysisViaGenericEndpointIntroducingVariation() {
        Consumer<RiskAnalysis> institutionalPurposeVariation = riskAnalysis ->
                riskAnalysis.getRiskAnalysisForm().getAnswers().put("institutionalPurpose", List.of("purpose variata"));
        compilesRiskAnalysisViaGenericEndpoint(institutionalPurposeVariation);
    }

    private void compilesRiskAnalysisViaGenericEndpoint(Consumer<RiskAnalysis> riskAnalysisModifier) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());

        compiledRiskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        riskAnalysisModifier.accept(compiledRiskAnalysis);

        // Use generic purpose update endpoint instead of dedicated risk analysis form endpoint
        PurposeUpdateContent updateContent = new PurposeUpdateContent()
                .title("Updated title")
                .description("Updated description")
                .dailyCalls(1)
                .isFreeOfCharge(true)
                .riskAnalysisForm(compiledRiskAnalysis.getRiskAnalysisForm());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().updatePurpose(purposeId, updateContent));
    }

    @Given("compila l'analisi del rischio tramite endpoint generico con successo")
    public void compilesRiskAnalysisViaGenericEndpointSuccessfully() {
        compilesRiskAnalysisViaGenericEndpoint();
        verifyRiskAnalysisSigningState("DRAFT");
    }

    @Then("la variazione nell'analisi del rischio è stata persistita")
    public void verifyRiskAnalysisVariationPersisted() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        IPurposeApiClient purposeApiClient = clientTokenConfigurator.getPurposeApiClient();

        RiskAnalysisFormSeed expectedVariation = sharedStepsContext.getRiskAnalysisCommonContext().getRiskAnalysisVariation();
        if (expectedVariation == null) {
            throw new IllegalStateException("Nessuna variazione registrata nel contesto");
        }

        sharedStepsContext.getPollingService().makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                purpose -> {
                    if (purpose.getRiskAnalysisForm() == null) {
                        return false;
                    }
                    var persistedAnswers = purpose.getRiskAnalysisForm().getAnswers();
                    var expectedAnswers = expectedVariation.getAnswers();
                    return expectedAnswers.equals(persistedAnswers);
                },
                "The risk analysis variation is not persisted"
        );
    }

    @When("l'utente invia il submit dell'analisi del rischio della finalità")
    public void userSubmitsRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        RiskAnalysisSubmissionSeed submissionSeed = new RiskAnalysisSubmissionSeed()
                .riskAnalysisForm(
                        nonNull(this.compiledRiskAnalysis)
                                ? this.compiledRiskAnalysis.getRiskAnalysisForm()
                                : dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true).getRiskAnalysisForm());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().submitRiskAnalysis(purposeId, submissionSeed));
    }

    @When("l'utente invia il submit dell'analisi del rischio della finalità con successo")
    public void userSubmitsRiskAnalysisSuccessfully() {
        userSubmitsRiskAnalysis();
        verifyRiskAnalysisSigningState("SUBMITTED");
    }

    @When("l'utente invia il submit dell'analisi del rischio della finalità introducendo una variazione")
    public void userSubmitsRiskAnalysisWithVariation() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(sharedStepsContext.getTenantType(), true);
        // Variazione minima richiesta dal CDT: aggiorno institutionalPurpose nel payload di submit.
        riskAnalysis.getRiskAnalysisForm().getAnswers().put("institutionalPurpose", List.of("variazione introdotta per il test"));
        RiskAnalysisSubmissionSeed payload = new RiskAnalysisSubmissionSeed()
                .riskAnalysisForm(riskAnalysis.getRiskAnalysisForm());

        sharedStepsContext.getRiskAnalysisCommonContext().setRiskAnalysisVariation(riskAnalysis.getRiskAnalysisForm());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient()
                .submitRiskAnalysis(purposeId, payload));
    }

    @When("il valutatore assegnato rifiuta la propria compilazione dell'analisi del rischio")
    public void assignedReviewerRejectsOwnRiskAnalysisCompilation() {
        rejectAsLastAssignedReviewer(new RiskAnalysisRejectionSeed().rejectionReason("Rifiuto della propria compilazione"));
    }

    @When("un reviewer assegnato rifiuta l'analisi del rischio")
    public void assignedReviewerRejectsRiskAnalysis() {
        assignedReviewerRejectsOwnRiskAnalysisCompilation();
    }

    @Given("un reviewer assegnato rifiuta l'analisi del rischio con successo")
    public void assignedReviewerRejectsRiskAnalysisSuccessfully() {
        assignedReviewerRejectsRiskAnalysis();
        verifyRiskAnalysisSigningState("REJECTED");
    }

    @When("l'utente rifiuta l'analisi del rischio della finalità")
    public void userRejectsRiskAnalysis() {
        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        RiskAnalysisRejectionSeed payload = new RiskAnalysisRejectionSeed().rejectionReason("Rifiuto del valutatore");
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().rejectRiskAnalysis(purposeId, payload));
    }

    @When("il valutatore assegnato convalida l'analisi del rischio della finalità")
    public void assignedReviewerSignsRiskAnalysis() {
        signRiskAnalysisAsReviewer(getLastAssignedReviewerActor());
    }

    @Given("il valutatore assegnato convalida l'analisi del rischio della finalità con successo")
    public void assignedReviewerSignsRiskAnalysisSuccessfully() {
        assignedReviewerSignsRiskAnalysis();
        verifyRiskAnalysisSigningState("SIGNED");
    }

    @When("l'utente convalida l'analisi del rischio della finalità")
    public void userSignsRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
        httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().signRiskAnalysis(purposeId));
    }

    @When("un altro reviewer assegnato convalida l'analisi del rischio della finalità")
    public void anotherAssignedReviewerSignsRiskAnalysis() {
        List<AssignedReviewerActorRef> assignedReviewerActors = getAssignedReviewerActorsOrThrow(
                2,
                "Sono necessari almeno due reviewer assegnati in contesto"
        );
        AssignedReviewerActorRef anotherAssignedReviewerActor = assignedReviewerActors.get(0);
        signRiskAnalysisAsReviewer(anotherAssignedReviewerActor);
    }

    private List<AssignedReviewerActorRef> getAssignedReviewerActorsOrThrow(int minRequired, String errorMessage) {
        List<AssignedReviewerActorRef> assignedReviewerActors = sharedStepsContext.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        if (assignedReviewerActors == null || assignedReviewerActors.size() < minRequired) {
            throw new IllegalStateException(errorMessage);
        }
        return assignedReviewerActors;
    }

    private void signRiskAnalysisAsReviewer(AssignedReviewerActorRef reviewerActor) {
        withReviewerToken(reviewerActor, () -> {
            UUID purposeId = UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId());
            httpCallExecutor.performCall(() -> clientTokenConfigurator.getPurposeApiClient().signRiskAnalysis(purposeId));
        });
    }

    @When("un reviewer assegnato tenta di rifiutare l'analisi del rischio senza motivazione")
    public void assignedReviewerAttemptsRejectRiskAnalysisWithoutReason() {
        rejectAsLastAssignedReviewer(new RiskAnalysisRejectionSeed());
    }
}
