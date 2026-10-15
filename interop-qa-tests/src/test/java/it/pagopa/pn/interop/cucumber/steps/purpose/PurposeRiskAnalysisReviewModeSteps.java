package it.pagopa.pn.interop.cucumber.steps.purpose;

import io.cucumber.java.en.Then;
import io.cucumber.java.ParameterType;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Azioni e verifiche dei CDT 1.2, 1.3 e 7.1; preparazione condivisa negli step di compilazione. */
public class PurposeRiskAnalysisReviewModeSteps {
    public enum RiskAnalysisDataEffect { CONSERVATI, AZZERATI }

    @ParameterType("conservati|azzerati")
    public static RiskAnalysisDataEffect riskAnalysisDataEffect(String value) {
        return switch (value) {
            case "conservati" -> RiskAnalysisDataEffect.CONSERVATI;
            case "azzerati" -> RiskAnalysisDataEffect.AZZERATI;
            default -> throw new IllegalArgumentException("Effetto RA non supportato: " + value);
        };
    }

    private final SharedStepsContext context;
    private final ClientTokenConfigurator tokens;
    public PurposeRiskAnalysisReviewModeSteps(SharedStepsContext context, ClientTokenConfigurator tokens) {
        this.context = context;
        this.tokens = tokens;
    }

    @Then("i dati RA dopo il cambio modalità risultano {riskAnalysisDataEffect}")
    public void verifyRiskAnalysisData(RiskAnalysisDataEffect expectedEffect) {
        var snapshot = context.getRiskAnalysisCommonContext().getInitialRiskAnalysis();
        assertThat(snapshot).as("La preparazione deve memorizzare la RA iniziale").isNotNull();
        assertThat(snapshot.purposeId()).isEqualTo(purposeId());
        assertThat(snapshot.answers()).isNotEmpty();
        tokens.setBearerToken(context.getUserToken());
        context.getPollingService().makePolling(
                () -> tokens.getPurposeApiClient().getPurpose(purposeId()),
                purpose -> expectedEffect == RiskAnalysisDataEffect.AZZERATI
                        ? isRiskAnalysisReset(purpose.getRiskAnalysisForm())
                        : purpose.getRiskAnalysisForm() != null
                          && Objects.equals(snapshot.version(), purpose.getRiskAnalysisForm().getVersion())
                          && snapshot.answers().equals(purpose.getRiskAnalysisForm().getAnswers()),
                "I dati RA non risultano " + expectedEffect + " dopo il cambio modalità"
        );
    }

    /** Unico punto da aggiornare se il contratto di reset cambia. Non basta un form diverso. */
    static boolean isRiskAnalysisReset(RiskAnalysisForm form) {
        return form == null || form.getAnswers() == null || form.getAnswers().isEmpty();
    }

    private UUID purposeId() {
        return UUID.fromString(context.getPurposeCommonContext().getPurposeId());
    }

}




