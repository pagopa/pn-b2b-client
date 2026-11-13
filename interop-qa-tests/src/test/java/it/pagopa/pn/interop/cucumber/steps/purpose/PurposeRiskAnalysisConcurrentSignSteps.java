package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.purpose.service.impl.PurposeApiClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.ReviewerWorkflow;
import it.pagopa.pn.interop.cucumber.steps.common.RiskAnalysisCommonContext.AssignedReviewerActorRef;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/** CDT 3.3: richieste quasi simultanee con identità e risultati HTTP indipendenti. */
public class PurposeRiskAnalysisConcurrentSignSteps {
    private static final long REQUEST_TIMEOUT_SECONDS = 60;
    private final SharedStepsContext context;
    private final ClientTokenConfigurator configurator;
    private final RestTemplate restTemplate;
    private final InteropClientConfigs configs;
    private UUID purposeId;
    private int initialVersion;
    private List<SignResult> results;

    public PurposeRiskAnalysisConcurrentSignSteps(SharedStepsContext context, ClientTokenConfigurator configurator,
            RestTemplate restTemplate, InteropClientConfigs configs) {
        this.context = context;
        this.configurator = configurator;
        this.restTemplate = restTemplate;
        this.configs = configs;
    }

    record SignResult(UUID reviewerId, ResponseEntity<?> response) { }

    /** Configura le identità prima dei task; nessun configuratore o esecutore HTTP condiviso nei thread. */
    @When("i due valutatori assegnati convalidano contemporaneamente l'analisi del rischio con la stessa versione acquisita")
    public void signConcurrently() throws InterruptedException, ExecutionException, TimeoutException {
        purposeId = UUID.fromString(context.getPurposeCommonContext().getPurposeId());
        configurator.setBearerToken(context.getUserToken());
        initialVersion = configurator.getPurposeApiClient().getPurposeMetadataVersion(purposeId);
        List<AssignedReviewerActorRef> reviewers = context.getRiskAnalysisCommonContext().getAssignedReviewerActors();
        if (reviewers == null || reviewers.size() != 2) {
            throw new IllegalStateException("La firma concorrente richiede esattamente due valutatori assegnati");
        }
        IdentityService identityService = context.getIdentityService();
        AssignedReviewerActorRef firstReviewer = reviewers.get(0);
        AssignedReviewerActorRef secondReviewer = reviewers.get(1);
        UUID firstReviewerId = identityService.getUserId(firstReviewer.tenantType(), firstReviewer.role(), firstReviewer.index());
        UUID secondReviewerId = identityService.getUserId(secondReviewer.tenantType(), secondReviewer.role(), secondReviewer.index());
        if (firstReviewerId.equals(secondReviewerId)) {
            throw new IllegalStateException("Le due utenze reviewer devono avere UUID distinti");
        }
        // Eccezione alla DI dei wrapper: nessun proxy @Retryable durante la race.
        // RestTemplate Spring condiviso in sola lettura; ogni wrapper ha un ApiClient/token distinto.
        IPurposeApiClient firstClient = new PurposeApiClientImpl(restTemplate, configs);
        IPurposeApiClient secondClient = new PurposeApiClientImpl(restTemplate, configs);
        firstClient.setBearerToken(identityService.getToken(firstReviewer.tenantType(), firstReviewer.role(), firstReviewer.index()));
        secondClient.setBearerToken(identityService.getToken(secondReviewer.tenantType(), secondReviewer.role(), secondReviewer.index()));
        results = executeRace(purposeId, initialVersion, firstReviewerId, firstClient, secondReviewerId, secondClient);
    }

    static List<SignResult> executeRace(UUID purposeId, int version, UUID firstId, IPurposeApiClient firstClient,
            UUID secondId, IPurposeApiClient secondClient) throws InterruptedException, ExecutionException, TimeoutException {
        // Registrare entrambe le azioni prima del via evita attese bloccanti nei worker del pool comune.
        CompletableFuture<Void> start = new CompletableFuture<>();
        CompletableFuture<SignResult> first = start.thenApplyAsync(
                ignored -> signOnce(firstClient, firstId, purposeId, version));
        CompletableFuture<SignResult> second = start.thenApplyAsync(
                ignored -> signOnce(secondClient, secondId, purposeId, version));
        try {
            start.complete(null);
            CompletableFuture.allOf(first, second).get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return List.of(first.join(), second.join());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } finally {
            // La cancellazione non interrompe necessariamente una richiesta HTTP già in corso.
            first.cancel(true);
            second.cancel(true);
        }
    }

    private static SignResult signOnce(IPurposeApiClient client, UUID reviewerId, UUID purposeId, int version) {
        RiskAnalysisSignSeed payload = new RiskAnalysisSignSeed().metadataVersionToSign(version);
        try {
            return new SignResult(reviewerId, client.signRiskAnalysisWithHttpInfo(purposeId, payload));
        } catch (HttpStatusCodeException e) {
            // Il diniego è un risultato della race; errori di rete/timeout invece fanno fallire lo step.
            return new SignResult(reviewerId, ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders()).body(e.getResponseBodyAsString()));
        }
    }

    @Then("una sola firma concorrente riesce e la versione dei metadati è incrementata di uno")
    public void verifySingleWinner() {
        assertThat(results).as("Eseguire prima entrambe le richieste concorrenti").hasSize(2);
        assertThat(results.stream().map(result -> result.response().getStatusCodeValue()).toList())
                .as("Una sola approvazione e un solo conflitto").containsExactlyInAnyOrder(204, 409);
        UUID winner = results.stream().filter(result -> result.response().getStatusCodeValue() == 204)
                .findFirst().orElseThrow().reviewerId();
        configurator.setBearerToken(context.getUserToken());
        context.getPollingService().makePolling(
                () -> configurator.getPurposeApiClient().getPurposeWithHttpInfo(purposeId), response -> {
                    Purpose purpose = response.getBody();
                    if (purpose == null || purpose.getReviewerWorkflow() == null) return false;
                    ReviewerWorkflow workflow = purpose.getReviewerWorkflow();
                    String version = response.getHeaders().getFirst(PurposeApiClientImpl.METADATA_VERSION_HEADER);
                    return workflow.getSigningState() == RiskAnalysisSigningState.SIGNED
                            && winner.equals(workflow.getSignedBy()) && workflow.getSignedAt() != null
                            && String.valueOf((long) initialVersion + 1).equals(version);
                }, "Firma concorrente inconsistente: firmatario, stato SIGNED o versione V+1 non corrispondenti");
    }
}

