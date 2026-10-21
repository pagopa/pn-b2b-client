package it.pagopa.pn.interop.cucumber.steps;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewerListParameterTypeTest {
    @Test
    void distinguishesNullFromEmptyAndNonEmptyReviewers() {
        assertThat(ParameterTypes.reviewerList("NULL")).isNull();
        assertThat(ParameterTypes.reviewerList("nessuno")).isEmpty();
        assertThat(ParameterTypes.reviewerList("1,2")).containsExactly(1, 2);
    }
}

