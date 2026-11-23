package it.pagopa.interop.purpose.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: utili durante la stesura di test, meno utili una volta stabilizzati i test. Rimuovere una volta concluso il quality gate di Evolutive Nuovi Operatori.
class RiskAnalysisAssignmentRequestTest {
    @Test
    void convertsAllModesToBothContracts() {
        for (RiskAnalysisReviewMode mode : RiskAnalysisReviewMode.values()) {
            List<UUID> reviewers = mode == RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS
                    ? List.of() : List.of(UUID.randomUUID(), UUID.randomUUID());
            RiskAnalysisAssignmentRequest request = RiskAnalysisAssignmentRequest.builder()
                    .reviewMode(mode).reviewerIds(reviewers).build();

            assertThat(request.toBFFRequest().getReviewMode().getValue()).isEqualTo(mode.getValue());
            assertThat(request.toBFFRequest().getReviewerIds()).containsExactlyElementsOf(reviewers);
            assertThat(request.toM2MRequest().getReviewMode().getValue()).isEqualTo(mode.getValue());
            assertThat(request.toM2MRequest().getReviewerIds()).containsExactlyElementsOf(reviewers);
        }
    }

    @Test
    void preservesNullReviewerIds() {
        RiskAnalysisAssignmentRequest request = RiskAnalysisAssignmentRequest.builder()
                .reviewMode(RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS).reviewerIds(null).build();
        assertThat(request.toBFFRequest().getReviewerIds()).isNull();
        assertThat(request.toM2MRequest().getReviewerIds()).isNull();
    }
}
