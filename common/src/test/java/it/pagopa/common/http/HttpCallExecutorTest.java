package it.pagopa.common.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpCallExecutorTest {
    private final HttpCallExecutor executor = new HttpCallExecutor();

    @Test
    void startsWithoutAResult() {
        assertNoResult();
        assertThrows(IllegalStateException.class, () -> executor.getResponseBody(String.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201})
    void retainsTheActualSuccessStatusAndBody(int statusCode) {
        String body = "response-body";

        String returnedBody = executor.callForEntity(() -> ResponseEntity.status(statusCode).body(body));

        assertSame(body, returnedBody);
        assertEquals(statusCode, executor.getStatusCode());
        assertSame(body, executor.getResponse());
        assertSame(body, executor.getResponseBody(String.class));
        assertTrue(executor.hasResult());
        assertTrue(executor.isSuccessful());
        assertFalse(executor.isHttpError());
        assertNull(executor.getErrorBody());
        assertNull(executor.getErrorMessage());
    }

    @Test
    void retainsNoContentAsARealSuccessfulResultWithoutABody() {
        executor.callForEntity(() -> ResponseEntity.noContent().build());

        assertEquals(204, executor.getStatusCode());
        assertTrue(executor.hasResult());
        assertTrue(executor.isSuccessful());
        assertFalse(executor.isHttpError());
        assertNull(executor.getResponse());
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> executor.getResponseBody(String.class));
        assertTrue(exception.getMessage().contains("204"));
    }

    @ParameterizedTest
    @MethodSource("httpFailures")
    void retainsHttpErrorStatusAndBodySeparatelyFromTheDiagnostic(RestClientResponseException failure) {
        Object returnedBody = executor.callForEntity(() -> {
            throw failure;
        });

        assertNull(returnedBody);
        assertEquals(failure.getRawStatusCode(), executor.getStatusCode());
        assertNull(executor.getResponse());
        assertEquals("error-body", executor.getErrorBody());
        assertEquals(failure.getMessage(), executor.getErrorMessage());
        assertTrue(executor.hasResult());
        assertFalse(executor.isSuccessful());
        assertTrue(executor.isHttpError());
        assertThrows(IllegalStateException.class, () -> executor.getResponseBody(String.class));
    }

    private static Stream<RestClientResponseException> httpFailures() {
        byte[] body = "error-body".getBytes(StandardCharsets.UTF_8);
        return Stream.of(
                HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request",
                        HttpHeaders.EMPTY, body, StandardCharsets.UTF_8),
                HttpServerErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                        HttpHeaders.EMPTY, body, StandardCharsets.UTF_8),
                new UnknownHttpStatusCodeException(599, "Custom Error", HttpHeaders.EMPTY,
                        body, StandardCharsets.UTF_8));
    }

    @Test
    void retainsTheCompleteLongErrorBodyUsingTheResponseCharset() {
        String body = "èéàü".repeat(250);
        HttpClientErrorException failure = HttpClientErrorException.create("short diagnostic",
                HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY,
                body.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);

        executor.callForEntity(() -> {
            throw failure;
        });

        assertEquals(body, executor.getErrorBody());
        assertEquals("short diagnostic", executor.getErrorMessage());
    }

    @Test
    void clearsEachPreviousResultBeforeCallingAndAcrossErrorSuccessErrorSequences() {
        recordHttpFailure();

        executor.callForEntity(() -> {
            assertNoResult();
            return ResponseEntity.status(HttpStatus.CREATED).body("new-response");
        });
        assertEquals(201, executor.getStatusCode());
        assertEquals("new-response", executor.getResponse());
        assertNull(executor.getErrorBody());
        assertNull(executor.getErrorMessage());

        executor.callForEntity(() -> {
            assertNoResult();
            throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
        });
        assertEquals(503, executor.getStatusCode());
        assertNull(executor.getResponse());
        assertTrue(executor.isHttpError());
    }

    @Test
    void anEmptySuccessDoesNotRetainThePrecedingResponseBody() {
        executor.callForEntity(() -> ResponseEntity.ok("previous-response"));

        executor.callForEntity(() -> ResponseEntity.noContent().build());

        assertEquals(204, executor.getStatusCode());
        assertNull(executor.getResponse());
        assertNull(executor.getErrorBody());
        assertNull(executor.getErrorMessage());
    }

    @ParameterizedTest
    @MethodSource("technicalFailures")
    void propagatesTechnicalFailuresWithoutRetainingThePreviousResult(RuntimeException failure) {
        recordHttpFailure();

        RuntimeException propagated = assertThrows(failure.getClass(), () -> executor.callForEntity(() -> {
            assertNoResult();
            throw failure;
        }));

        assertSame(failure, propagated);
        assertNoResult();
    }

    private static Stream<RuntimeException> technicalFailures() {
        return Stream.of(new ResourceAccessException("network failure"),
                new IllegalArgumentException("invalid call argument"));
    }

    @Test
    void rejectsANullEntityAndLeavesNoPreviousResult() {
        executor.callForEntity(() -> ResponseEntity.ok("previous-response"));

        assertThrows(IllegalStateException.class, () -> executor.callForEntity(() -> null));

        assertNoResult();
    }

    @Test
    void doesNotRetryAnHttpConflict() {
        AtomicInteger calls = new AtomicInteger();

        executor.callForEntity(() -> {
            calls.incrementAndGet();
            throw new HttpClientErrorException(HttpStatus.CONFLICT,
                    "Request conflicts with an ongoing operation on the same resource");
        });

        assertEquals(1, calls.get());
        assertEquals(409, executor.getStatusCode());
        assertTrue(executor.isHttpError());
    }

    @Test
    void typedReadAcceptsCompatibleTypesAndRejectsMismatchesWithoutPrintingTheBody() {
        Integer body = 42;
        executor.callForEntity(() -> ResponseEntity.ok(body));
        assertSame(body, executor.getResponseBody(Number.class));

        executor.callForEntity(() -> ResponseEntity.ok(new StringBuilder("payload-marker")));
        IllegalStateException mismatch = assertThrows(IllegalStateException.class,
                () -> executor.getResponseBody(String.class));

        assertTrue(mismatch.getMessage().contains(String.class.getName()));
        assertTrue(mismatch.getMessage().contains(StringBuilder.class.getName()));
        assertFalse(mismatch.getMessage().contains("payload-marker"));
        assertTrue(executor.hasResult());
        assertTrue(executor.isSuccessful());
    }

    @Test
    void readingTheResultIsNonDestructiveAndLaterCallsDoNotMutatePreviousResults() {
        executor.callForEntity(() -> ResponseEntity.ok("first-response"));
        HttpCallResult<?> firstResult = executor.getLastResult();

        for (int read = 0; read < 3; read++) {
            assertSame(firstResult, executor.getLastResult());
            assertEquals(200, executor.getStatusCode());
            assertEquals("first-response", executor.getResponseBody(String.class));
            assertNull(executor.getErrorBody());
            assertNull(executor.getErrorMessage());
            assertTrue(executor.isSuccessful());
            assertFalse(executor.isHttpError());
        }

        recordHttpFailure();

        assertEquals(200, firstResult.getStatusCode());
        assertEquals("first-response", firstResult.getResponse());
        assertNull(firstResult.getErrorBody());
        assertNull(firstResult.getErrorMessage());
        assertTrue(firstResult.isSuccessful());
        assertFalse(firstResult.isHttpError());
        assertTrue(executor.getLastResult().isHttpError());
    }

    private void recordHttpFailure() {
        executor.callForEntity(() -> {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request",
                    HttpHeaders.EMPTY, "previous-error-body".getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
        });
    }

    private void assertNoResult() {
        assertFalse(executor.hasResult());
        assertFalse(executor.isSuccessful());
        assertFalse(executor.isHttpError());
        assertNull(executor.getLastResult());
        assertNull(executor.getStatusCode());
        assertNull(executor.getResponse());
        assertNull(executor.getErrorBody());
        assertNull(executor.getErrorMessage());
    }
}
