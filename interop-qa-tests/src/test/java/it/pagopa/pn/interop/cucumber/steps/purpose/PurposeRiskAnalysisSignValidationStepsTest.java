package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;

import org.junit.jupiter.api.Test;

import static it.pagopa.pn.interop.cucumber.steps.purpose.PurposeRiskAnalysisSignValidationSteps.SignVersionCase.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

// TODO utile in fase di sviluppo, potenzialmente superfluo a regime. Considerare rimozione una volta chiuso Evolutive Nuovi Operatori.
class PurposeRiskAnalysisSignValidationStepsTest {
    @Test
    void setsCurrentVersionInGeneratedSeed() {
        RiskAnalysisSignSeed seed = PurposeRiskAnalysisSignValidationSteps.signPayload(CORRENTE, 12);
        assertThat(seed.getMetadataVersionToSign()).isEqualTo(12);
    }

    @Test
    void leavesFieldNullInGeneratedSeedForMissingCase() {
        RiskAnalysisSignSeed seed = PurposeRiskAnalysisSignValidationSteps.signPayload(ASSENTE, 12);
        assertNull(seed.getMetadataVersionToSign());
    }

    @Test
    void setsZeroAndNegativeValuesInGeneratedSeed() {
        assertThat(PurposeRiskAnalysisSignValidationSteps.signPayload(ZERO, 12).getMetadataVersionToSign()).isZero();
        assertThat(PurposeRiskAnalysisSignValidationSteps.signPayload(NEGATIVA, 12).getMetadataVersionToSign()).isEqualTo(-1);
    }
}
