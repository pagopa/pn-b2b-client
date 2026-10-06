package it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche;

import it.pagopa.common.http.HttpCallExecutor;
import it.pagopa.pn.client.b2b.pa.service.impl.EmdIntegrationApiImpl;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.PaymentUrlResponse;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.RetrievalPayload;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageRequestBody;
import it.pagopa.pn.client.b2b.radd.generated.openapi.clients.emd.model.SendMessageResponse;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaBanche.domain.EmdCheckTppEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class MessaggiCortesiaBancheStepsTest {

    private final EmdIntegrationApiImpl client = mock(EmdIntegrationApiImpl.class);
    private final HttpCallExecutor executor = new HttpCallExecutor();
    private final MessaggiCortesiaBancheSteps steps = new MessaggiCortesiaBancheSteps(client, executor);

    @Test
    void sendMessageExposesStatusAndOutcomeToFollowingSteps() {
        SendMessageRequestBody request = new SendMessageRequestBody();
        SendMessageResponse body = new SendMessageResponse()
                .outcome(SendMessageResponse.OutcomeEnum.NO_CHANNELS_ENABLED);
        when(client.sendMessage(request)).thenReturn(ResponseEntity.ok(body));

        steps.callEmdSendMessage(List.of(request));

        steps.verifyStatusCode(200);
        steps.verifyOutcomeResponse("NO_CHANNELS_ENABLED");
        assertSame(body, executor.getResponse());
    }

    @ParameterizedTest
    @EnumSource(EmdCheckTppEndpoint.class)
    void checkTppUsesTheSelectedEndpointAndPreservesItsStatus(EmdCheckTppEndpoint endpoint) {
        RetrievalPayload body = new RetrievalPayload();
        ResponseEntity<RetrievalPayload> response = ResponseEntity.status(HttpStatus.CREATED).body(body);
        if (endpoint == EmdCheckTppEndpoint.TOKEN_CHECK_TPP) {
            when(client.tokenCheckTPP("test-retrieval")).thenReturn(response);
        } else {
            when(client.emdCheckTPP("test-retrieval")).thenReturn(response);
        }

        steps.callEmdCheckTPP(endpoint, "test-retrieval");

        steps.verifyStatusCode(201);
        assertSame(body, executor.getResponse());
        if (endpoint == EmdCheckTppEndpoint.TOKEN_CHECK_TPP) {
            verify(client).tokenCheckTPP("test-retrieval");
        } else {
            verify(client).emdCheckTPP("test-retrieval");
        }
        verifyNoMoreInteractions(client);
    }

    @Test
    void paymentUrlPassesTheAmountAndPreservesTheResponseBody() {
        PaymentUrlResponse body = new PaymentUrlResponse();
        when(client.getPaymentUrl("test-retrieval", "test-notice", "test-pa", 42))
                .thenReturn(ResponseEntity.ok(body));

        steps.callEmdPaymentUrl(paymentParameters("42"));

        steps.verifyStatusCode(200);
        assertSame(body, executor.getResponse());
    }

    @Test
    void expectedServerErrorIsAvailableToTheStatusAssertionWithItsBody() {
        SendMessageRequestBody request = new SendMessageRequestBody();
        String errorBody = "{\"detail\":\"test EMD error\"}";
        when(client.sendMessage(request)).thenThrow(HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "test error", null,
                errorBody.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

        steps.callEmdSendMessage(List.of(request));

        steps.verifyStatusCode(500);
        assertEquals(errorBody, executor.getErrorBody());
        assertThrows(AssertionError.class, () -> steps.verifyOutcomeResponse("NO_CHANNELS_ENABLED"));
    }

    @Test
    void technicalFailurePropagatesFromTheWhenWithoutAnEarlierHttpResult() {
        SendMessageRequestBody request = new SendMessageRequestBody();
        ResourceAccessException failure = new ResourceAccessException("test connection failure");
        when(client.sendMessage(request)).thenReturn(ResponseEntity.ok(new SendMessageResponse()))
                .thenThrow(failure);
        steps.callEmdSendMessage(List.of(request));

        assertSame(failure, assertThrows(ResourceAccessException.class,
                () -> steps.callEmdSendMessage(List.of(request))));

        assertFalse(executor.hasResult());
    }

    @Test
    void invalidAmountFailsWithoutLeavingThePreviousHttpResult() {
        executor.callForEntity(() -> ResponseEntity.noContent().build());

        assertThrows(NumberFormatException.class, () -> steps.callEmdPaymentUrl(paymentParameters("invalid")));

        assertFalse(executor.hasResult());
        verifyNoMoreInteractions(client);
    }

    private Map<String, String> paymentParameters(String amount) {
        return Map.of("retrievalId", "test-retrieval", "noticeCode", "test-notice",
                "paTaxId", "test-pa", "amount", amount);
    }
}
