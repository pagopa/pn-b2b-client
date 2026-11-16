package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.ReviewerWorkflow;
import it.pagopa.interop.purpose.service.IPurposeApiClient;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Firma con versione corrente e validazione server dei payload: CDT 3.1 e 3.6. */
public class PurposeRiskAnalysisSignValidationSteps {
    public enum SignVersionCase { CORRENTE, ASSENTE, ZERO, NEGATIVA }

    private final SharedStepsContext context;
    private final ClientTokenConfigurator configurator;
    private Purpose initialPurpose;
    private Integer currentVersion;
    private UUID signerId;

    public PurposeRiskAnalysisSignValidationSteps(SharedStepsContext context, ClientTokenConfigurator configurator) {
        this.context = context;
        this.configurator = configurator;
    }

    /** Converte solo le varianti previste dall'Outline. */
    @ParameterType("corrente|assente|zero|negativa")
    public static SignVersionCase signVersionCase(String value) {
        return SignVersionCase.valueOf(value.toUpperCase(Locale.ROOT));
    }

    /** Acquisisce la baseline e invia la firma; le verifiche funzionali restano nei Given e nei Then. */
    @When("l'utente convalida l'analisi del rischio con versione metadati {signVersionCase}")
    public void signWithVersion(SignVersionCase versionCase) {
        configurator.setBearerToken(context.getUserToken());
        IPurposeApiClient client = configurator.getPurposeApiClient();
        initialPurpose = client.getPurpose(purposeId());
        currentVersion = client.getPurposeMetadataVersion(purposeId());
        if (currentVersion == null) {
            throw new IllegalStateException("Versione metadati corrente non disponibile per preparare il payload di firma");
        }
        // 0 è ammesso dallo schema: serve una baseline che lo renda una versione non corrente.
        if (versionCase == SignVersionCase.ZERO && currentVersion <= 0) {
            throw new IllegalStateException("Il caso zero richiede una baseline con versione metadati maggiore di zero");
        }
        signerId = context.getIdentityService().getUserId(context.getTenantType(), "reviewer", 0);
        RiskAnalysisSignSeed payload = signPayload(versionCase, currentVersion);
        context.getHttpCallExecutor().performCall(() -> client.signRiskAnalysis(purposeId(), payload));
    }

    /**
     * Verifica che la finalità risulti coerente con l'esito della firma:
     * in caso di esito positivo la RA deve essere passata a SIGNED con il firmatario corretto;
     * in caso di diniego nessun dato di workflow, form, versione metadati o purpose deve essere mutato.
     */
    @Then("lo stato della finalità e i dati dell'analisi del rischio risultano coerenti con la firma con esito {booleanResponse}")
    public void verifySignConsistency(boolean successful) {
        assertThat(initialPurpose).as("Eseguire prima il tentativo di firma").isNotNull();
        configurator.setBearerToken(context.getUserToken());
        IPurposeApiClient client = configurator.getPurposeApiClient();
        context.getPollingService().makePolling(() -> client.getPurpose(purposeId()), purpose -> {
            if (!successful) {
                // In caso di esito negativo (es. 400 o 409), l'intero oggetto Purpose deve essere rimasto immutato
                return initialPurpose.equals(purpose);
            }
            // In caso di firma positiva (204), la RA deve essere passata a SIGNED, con firmatario e data valorizzati
            ReviewerWorkflow workflow = purpose.getReviewerWorkflow();
            return workflow != null && workflow.getSigningState() == RiskAnalysisSigningState.SIGNED
                    && signerId.equals(workflow.getSignedBy()) && workflow.getSignedAt() != null;
        }, "Stato o dati RA non coerenti con l'esito della firma");
        if (!successful) {
            assertThat(client.getPurposeMetadataVersion(purposeId()))
                    .as("Il diniego non deve modificare la versione dei metadati").isEqualTo(currentVersion);
        }
    }

    static RiskAnalysisSignSeed signPayload(SignVersionCase versionCase, int currentVersion) {
        return switch (versionCase) {
            case CORRENTE -> new RiskAnalysisSignSeed().metadataVersionToSign(currentVersion);
            case ASSENTE -> new RiskAnalysisSignSeed();
            case ZERO -> new RiskAnalysisSignSeed().metadataVersionToSign(0);
            case NEGATIVA -> new RiskAnalysisSignSeed().metadataVersionToSign(-1);
        };
    }

    private UUID purposeId() {
        return UUID.fromString(context.getPurposeCommonContext().getPurposeId());
    }
}
