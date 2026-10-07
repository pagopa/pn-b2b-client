package it.pagopa.pn.cucumber.steps.pa;

import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.service.IPnDowntimeLogsClient;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeEntry;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DowntimeLogsStepsTest {
    private final IPnDowntimeLogsClient client = mock(IPnDowntimeLogsClient.class);
    private final HttpCallExecutor executor = new HttpCallExecutor();
    private final DowntimeLogsSteps steps = new DowntimeLogsSteps(client, mock(LegalFactContentVerifySteps.class), executor);

    @ParameterizedTest
    @CsvSource(value = {"2024, 1", "NULL, NULL"}, nullValues = "NULL")
    void explicitAndDefaultPeriodsExposeTheActualStatusAndEmptyHistory(Integer year, Integer month) {
        PnDowntimeHistoryResponse body = new PnDowntimeHistoryResponse().result(List.of());
        when(client.getResolvedWithHttpInfo(year, month)).thenReturn(ResponseEntity.status(201).body(body));

        if (year == null) {
            steps.siChiamaLApiDiRecuperoElencoDisserviziConMeseEAnnoVuoti();
        } else {
            steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(year, month);
        }

        steps.vieneRestituitoUnElencoDiDisserviziVuoto();
        assertEquals(201, executor.getStatusCode());
        assertSame(body, executor.getResponse());
        verify(client).getResolvedWithHttpInfo(year, month);
    }

    @Test
    void currentPeriodStillValidatesTheAvailabilityTimestampOnReturnedEntries() {
        LocalDate today = LocalDate.now();
        OffsetDateTime endDate = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        PnDowntimeEntry entry = new PnDowntimeEntry().endDate(endDate).fileAvailableTimestamp(endDate);
        PnDowntimeHistoryResponse body = new PnDowntimeHistoryResponse().result(List.of(entry));
        when(client.getResolvedWithHttpInfo(today.getYear(), today.getMonthValue())).thenReturn(ResponseEntity.ok(body));

        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMeseCorrente();

        steps.vieneRestituitoLElencoDeiDisserviziDelMeseCorrente();
        entry.setFileAvailableTimestamp(null);
        assertThrows(AssertionError.class, steps::vieneRestituitoLElencoDeiDisserviziDelMeseCorrente);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void anHttpErrorPreservesItsBodyAndCannotReuseAnEarlierHistory(int status) {
        String errorBody = "{\"detail\":\"test downtime error\"}";
        when(client.getResolvedWithHttpInfo(2024, 1))
                .thenReturn(ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of())))
                .thenThrow(httpFailure(status, errorBody));
        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1);

        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1);

        steps.siControllaCheLApiRestituisceUnCodiceDiErrore(status);
        assertEquals(errorBody, executor.getErrorBody());
        assertThrows(AssertionError.class, steps::vieneRestituitoUnElencoDiDisserviziVuoto);
    }

    @Test
    void aSuccessfulCallClearsAnEarlierHttpError() {
        when(client.getResolvedWithHttpInfo(2024, 1)).thenThrow(httpFailure(400, "test error"))
                .thenReturn(ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of())));
        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1);

        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1);

        steps.vieneRestituitoUnElencoDiDisserviziVuoto();
        assertThrows(AssertionError.class, () -> steps.siControllaCheLApiRestituisceUnCodiceDiErrore(400));
    }

    @Test
    void aTechnicalFailurePropagatesAndClearsTheEarlierResponse() {
        ResourceAccessException failure = new ResourceAccessException("test connection failure");
        when(client.getResolvedWithHttpInfo(2024, 1))
                .thenReturn(ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of())))
                .thenThrow(failure);
        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1);

        assertSame(failure, assertThrows(ResourceAccessException.class,
                () -> steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2024, 1)));

        assertFalse(executor.hasResult());
    }

    @ParameterizedTest
    @CsvSource({"creazione notifiche, NOTIFICATION_CREATE", "visualizzazione notifiche, NOTIFICATION_VISUALIZATION",
            "workflow notifiche, NOTIFICATION_WORKFLOW"})
    void historicalQueriesKeepTheFunctionalityAndShareTheirResponse(String eventType, PnFunctionality functionality) {
        PnDowntimeHistoryResponse body = new PnDowntimeHistoryResponse().result(List.of());
        when(client.statusHistoryWithHttpInfo(any(OffsetDateTime.class), any(OffsetDateTime.class),
                eq(List.of(functionality)), eq("0"), eq("50"))).thenReturn(ResponseEntity.ok(body));

        steps.vengonoLettiGliEventiDegliUltimiGiorniRelativiAlla(30, eventType);

        steps.vieneIndividuatoSePresenteLEventoPiùRecente();
        assertSame(body, executor.getResponse());
    }

    @Test
    void anUnexpectedHttpErrorStillStopsTheHistoricalQuery() {
        when(client.statusHistoryWithHttpInfo(any(), any(), eq(List.of(PnFunctionality.NOTIFICATION_CREATE)),
                eq("0"), eq("50"))).thenThrow(httpFailure(500, "test history error"));

        assertThrows(AssertionError.class,
                () -> steps.vengonoLettiGliEventiDegliUltimiGiorniRelativiAlla(30, "creazione notifiche"));

        assertEquals(500, executor.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "ERRATO"})
    void invalidLegalFactReferencesExposeTheHttpErrorToTheSharedThen(String idType) {
        String id = idType.equals("null") ? null : "1234567890";
        when(client.getLegalFactWithHttpInfo(id)).thenThrow(httpFailure(400, "test invalid legal fact"));

        steps.vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId(idType);

        steps.siControllaCheLApiRestituisceUnCodiceDiErrore(400);
        verify(client).getLegalFactWithHttpInfo(id);
    }

    @Test
    void aCurrentLegalFactIsSelectedBeforeTheExecutorReplacesTheHistory() {
        LocalDate today = LocalDate.now();
        PnDowntimeEntry entry = new PnDowntimeEntry().legalFactId("test-legal-fact");
        LegalFactDownloadMetadataResponse body = new LegalFactDownloadMetadataResponse().url("https://example.test/document");
        when(client.getResolvedWithHttpInfo(today.getYear(), today.getMonthValue()))
                .thenReturn(ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of(entry))));
        when(client.getLegalFactWithHttpInfo("test-legal-fact")).thenReturn(ResponseEntity.ok(body));

        steps.vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId("CORRETTO");

        steps.vieneScaricatoLAttoOpponibileAiTerziDiMalfunzionamentoERipristino();
        assertSame(body, executor.getResponse());
        verify(client).getLegalFactWithHttpInfo("test-legal-fact");
    }

    @Test
    void theHistoricalLegalFactStillExposesRetryAfter() {
        PnDowntimeEntry entry = new PnDowntimeEntry().legalFactId("test-old-legal-fact")
                .endDate(OffsetDateTime.parse("2024-01-20T00:00:00Z"));
        LegalFactDownloadMetadataResponse body = new LegalFactDownloadMetadataResponse().retryAfter(42);
        when(client.getResolvedWithHttpInfo(2024, 1))
                .thenReturn(ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of(entry))));
        when(client.getLegalFactWithHttpInfo("test-old-legal-fact")).thenReturn(ResponseEntity.ok(body));

        steps.vieneChiamataLAPIPerIlDownloadDellAttoOpponibileProdottoPiuDiGiorniPrecedenti();

        steps.laChiamataVaConSuccessoELaRispostaContieneIlCampoPopolato();
        assertSame(body, executor.getResponse());
    }

    @Test
    void anUnsupportedLegalFactReferenceClearsTheLastHttpResult() {
        executor.callForEntity(() -> ResponseEntity.ok(new PnDowntimeHistoryResponse().result(List.of())));

        assertThrows(IllegalArgumentException.class,
                () -> steps.vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId("invalid"));

        assertFalse(executor.hasResult());
    }

    private RestClientResponseException httpFailure(int status, String body) {
        return new RestClientResponseException("test HTTP error", status, "test error", new HttpHeaders(),
                body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }
}
