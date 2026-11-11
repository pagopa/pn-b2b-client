package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// TODO utile durante la stesura dei test, meno utile una volta stabilizzati i test. Rimuovere una volta concluso il quality gate di Evolutive Nuovi Operatori.
class PurposeRiskAnalysisReviewModeStepsTest {
    @Test
    void acceptsAbsentForm() {
        assertThat(PurposeRiskAnalysisReviewModeSteps.isRiskAnalysisReset(null)).isTrue();
    }

    @Test
    void acceptsNullAnswers() {
        assertThat(PurposeRiskAnalysisReviewModeSteps.isRiskAnalysisReset(
                new RiskAnalysisForm().answers(null))).isTrue();
    }

    @Test
    void acceptsEmptyAnswers() {
        assertThat(PurposeRiskAnalysisReviewModeSteps.isRiskAnalysisReset(
                new RiskAnalysisForm().answers(Map.of()))).isTrue();
    }

    @Test
    void rejectsResidualAnswers() {
        assertThat(PurposeRiskAnalysisReviewModeSteps.isRiskAnalysisReset(
                new RiskAnalysisForm().answers(Map.of("institutionalPurpose", List.of("dato residuo")))))
                .isFalse();
    }
}
