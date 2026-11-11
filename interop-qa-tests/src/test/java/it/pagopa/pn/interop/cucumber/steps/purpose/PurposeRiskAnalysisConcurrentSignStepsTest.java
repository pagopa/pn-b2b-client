package it.pagopa.pn.interop.cucumber.steps.purpose;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.pn.interop.cucumber.steps.purpose.PurposeRiskAnalysisConcurrentSignSteps.SignResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// TODO: utile in fase di implementazione, potenzialmente superfluo a regime. Considerare rimozione una volta chiuso Evolutive nuovi operatori.
class PurposeRiskAnalysisConcurrentSignStepsTest {
    @Test
    void firstReviewerCanWin() throws Exception {
        verifyRace(true);
    }

    @Test
    void secondReviewerCanWin() throws Exception {
        verifyRace(false);
    }

    private void verifyRace(boolean firstWins) throws Exception {
        IPurposeApiClient first = mock(IPurposeApiClient.class);
        IPurposeApiClient second = mock(IPurposeApiClient.class);
        UUID purposeId = UUID.randomUUID();
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        CountDownLatch bothCalling = new CountDownLatch(2);
        when(first.signRiskAnalysisWithHttpInfo(eq(purposeId), any())).thenAnswer(invocation -> {
            bothCalling.countDown();
            assertThat(bothCalling.await(5, TimeUnit.SECONDS)).as("Entrambe le chiamate devono essere in corso").isTrue();
            if (!firstWins) throw new HttpClientErrorException(HttpStatus.CONFLICT);
            return ResponseEntity.noContent().build();
        });
        when(second.signRiskAnalysisWithHttpInfo(eq(purposeId), any())).thenAnswer(invocation -> {
            bothCalling.countDown();
            assertThat(bothCalling.await(5, TimeUnit.SECONDS)).isTrue();
            if (firstWins) throw new HttpClientErrorException(HttpStatus.CONFLICT);
            return ResponseEntity.noContent().build();
        });

        List<SignResult> results = PurposeRiskAnalysisConcurrentSignSteps.executeRace(purposeId, 7, firstId, first, secondId, second);

        assertThat(results.stream().map(result -> result.response().getStatusCodeValue()).toList())
                .containsExactlyInAnyOrder(204, 409);
        assertThat(results.stream().filter(result -> result.response().getStatusCodeValue() == 204)
                .findFirst().orElseThrow().reviewerId()).isEqualTo(firstWins ? firstId : secondId);
        ArgumentCaptor<RiskAnalysisSignSeed> firstPayload = ArgumentCaptor.forClass(RiskAnalysisSignSeed.class);
        ArgumentCaptor<RiskAnalysisSignSeed> secondPayload = ArgumentCaptor.forClass(RiskAnalysisSignSeed.class);
        verify(first).signRiskAnalysisWithHttpInfo(eq(purposeId), firstPayload.capture());
        verify(second).signRiskAnalysisWithHttpInfo(eq(purposeId), secondPayload.capture());
        assertThat(firstPayload.getValue()).isNotSameAs(secondPayload.getValue());
        assertThat(firstPayload.getValue().getMetadataVersionToSign()).isEqualTo(7);
        assertThat(secondPayload.getValue().getMetadataVersionToSign()).isEqualTo(7);
        verifyNoMoreInteractions(first, second);
    }

    @Test
    void transportFailureIsNotTreatedAsExpectedConflict() {
        IPurposeApiClient first = mock(IPurposeApiClient.class);
        IPurposeApiClient second = mock(IPurposeApiClient.class);
        when(first.signRiskAnalysisWithHttpInfo(any(), any())).thenThrow(new ResourceAccessException("timeout"));
        when(second.signRiskAnalysisWithHttpInfo(any(), any())).thenReturn(ResponseEntity.noContent().build());

        assertThatThrownBy(() -> PurposeRiskAnalysisConcurrentSignSteps.executeRace(UUID.randomUUID(), 7,
                UUID.randomUUID(), first, UUID.randomUUID(), second))
                .isInstanceOf(ExecutionException.class).hasCauseInstanceOf(ResourceAccessException.class);
    }
}

