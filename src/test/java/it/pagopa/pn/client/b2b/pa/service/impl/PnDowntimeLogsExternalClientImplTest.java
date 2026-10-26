package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.LegalFactDownloadMetadataResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnDowntimeHistoryResponse;
import it.pagopa.pn.client.b2b.web.generated.openapi.clients.externalDowntimeLogs.model.PnFunctionality;
import it.pagopa.pn.cucumber.steps.pa.DowntimeLogsSteps;
import it.pagopa.pn.cucumber.steps.pa.LegalFactContentVerifySteps;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PnDowntimeLogsExternalClientImplTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
    private final PnDowntimeLogsExternalClientImpl client = new PnDowntimeLogsExternalClientImpl(
            restTemplate, "http://localhost", "test-agent");
    private final OffsetDateTime from = OffsetDateTime.parse("2024-01-01T00:00:00Z");
    private final OffsetDateTime to = OffsetDateTime.parse("2024-02-01T00:00:00Z");

    @Test
    void resolvedAdapterPreservesTheActualStatusHeadersAndBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test-Response", "test-value");
        server.expect(requestTo("http://localhost/downtime/v1/resolved?year=2024&month=1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.CREATED).headers(headers)
                        .contentType(MediaType.APPLICATION_JSON).body("{\"result\":[]}"));

        ResponseEntity<PnDowntimeHistoryResponse> response = client.getResolvedWithHttpInfo(2024, 1);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("test-value", response.getHeaders().getFirst("X-Test-Response"));
        assertTrue(response.getBody().getResult().isEmpty());
        server.verify();
    }

    @Test
    void legalFactAdapterPreservesTheActualStatusAndDownloadMetadata() {
        server.expect(requestTo("http://localhost/downtime/v1/legal-facts/test-legal-fact"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"url\":\"https://example.test/document\",\"retryAfter\":42}"));

        ResponseEntity<LegalFactDownloadMetadataResponse> response = client.getLegalFactWithHttpInfo("test-legal-fact");

        assertEquals(202, response.getStatusCodeValue());
        assertEquals("https://example.test/document", response.getBody().getUrl());
        assertEquals(42, response.getBody().getRetryAfter());
        server.verify();
    }

    @Test
    void historyAdapterUsesTheExistingEndpointAndKeepsTheBody() {
        server.expect(requestTo(startsWith("http://localhost/downtime/v1/history?")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("functionality", PnFunctionality.NOTIFICATION_CREATE.getValue()))
                .andExpect(queryParam("page", "0"))
                .andExpect(queryParam("size", "50"))
                .andRespond(withSuccess("{\"result\":[]}", MediaType.APPLICATION_JSON));

        ResponseEntity<PnDowntimeHistoryResponse> response = client.statusHistoryWithHttpInfo(
                from, to, List.of(PnFunctionality.NOTIFICATION_CREATE), "0", "50");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getResult().isEmpty());
        server.verify();
    }

    @Test
    void theExistingBodyOnlyMethodsRemainCompatible() {
        server.expect(requestTo("http://localhost/downtime/v1/resolved?year=2024&month=1"))
                .andRespond(withSuccess("{\"result\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(startsWith("http://localhost/downtime/v1/history?")))
                .andRespond(withSuccess("{\"result\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://localhost/downtime/v1/legal-facts/test-legal-fact"))
                .andRespond(withSuccess("{\"retryAfter\":42}", MediaType.APPLICATION_JSON));

        assertTrue(client.getResolved(2024, 1).getResult().isEmpty());
        assertTrue(client.statusHistory(from, to, List.of(PnFunctionality.NOTIFICATION_CREATE), "0", "50").getResult().isEmpty());
        assertEquals(42, client.getLegalFact("test-legal-fact").getRetryAfter());
        server.verify();
    }

    @Test
    void theServerErrorBodyReachesTheExecutorAndStatusAssertion() {
        String errorBody = "{\"detail\":\"test invalid period\"}";
        server.expect(requestTo("http://localhost/downtime/v1/resolved?year=2022&month=12"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errorBody));
        HttpCallExecutor executor = new HttpCallExecutor();
        DowntimeLogsSteps steps = new DowntimeLogsSteps(client, mock(LegalFactContentVerifySteps.class), executor);

        steps.siChiamaLApiDiRecuperoElencoDisserviziNellAnnoEMese(2022, 12);

        steps.siControllaCheLApiRestituisceUnCodiceDiErrore(400);
        assertEquals(errorBody, executor.getErrorBody());
        server.verify();
    }

    @Test
    void aMissingLegalFactIdKeepsTheGeneratedClientValidationWithoutSendingHttp() {
        HttpCallExecutor executor = new HttpCallExecutor();
        DowntimeLogsSteps steps = new DowntimeLogsSteps(client, mock(LegalFactContentVerifySteps.class), executor);

        steps.vieneChiamataLAPIPerIlDownloadDellAttoOpponibileAiTerziConId("null");

        steps.siControllaCheLApiRestituisceUnCodiceDiErrore(400);
        server.verify();
    }
}
