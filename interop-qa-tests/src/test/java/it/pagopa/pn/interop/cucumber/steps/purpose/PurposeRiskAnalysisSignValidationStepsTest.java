package it.pagopa.pn.interop.cucumber.steps.purpose;

import org.junit.jupiter.api.Test;

import static it.pagopa.pn.interop.cucumber.steps.purpose.PurposeRiskAnalysisSignValidationSteps.SignVersionCase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class PurposeRiskAnalysisSignValidationStepsTest {
    @Test
    void setsCurrentVersionInGeneratedSeed() {
        var seed = PurposeRiskAnalysisSignValidationSteps.signPayload(CORRENTE, 12);
        assertThat(seed.getMetadataVersionToSign()).isEqualTo(12);
    }

    @Test
    void leavesFieldNullInGeneratedSeedForMissingCase() {
        var seed = PurposeRiskAnalysisSignValidationSteps.signPayload(ASSENTE, 12);
        assertNull(seed.getMetadataVersionToSign());
    }

    @Test
    void setsZeroAndNegativeValuesInGeneratedSeed() {
        assertThat(PurposeRiskAnalysisSignValidationSteps.signPayload(ZERO, 12).getMetadataVersionToSign()).isZero();
        assertThat(PurposeRiskAnalysisSignValidationSteps.signPayload(NEGATIVA, 12).getMetadataVersionToSign()).isEqualTo(-1);
    }
}
