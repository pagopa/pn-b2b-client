package it.pagopa.common.http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Executes HTTP calls and retains their latest result for subsequent assertions.
 * Consumers configure its lifecycle; scenarios must use distinct instances.
 * Calls are executed once. Exceptions without an HTTP response propagate to the caller.
 */
public class HttpCallExecutor {
    private HttpCallResult<?> lastResult;

    /**
     * Executes a call that exposes the actual HTTP status and body.
     * The preceding result is discarded before the supplier is invoked.
     *
     * @return the response body, or {@code null} for an HTTP error or an empty response body
     * @throws IllegalStateException if the call returns no {@link ResponseEntity}
     */
    public <T> T callForEntity(Supplier<ResponseEntity<T>> call) {
        lastResult = null;
        Objects.requireNonNull(call, "HTTP call supplier must not be null");
        try {
            ResponseEntity<T> entity = call.get();
            if (entity == null) {
                throw new IllegalStateException("HTTP call returned no ResponseEntity");
            }
            lastResult = HttpCallResult.fromResponse(entity.getStatusCodeValue(), entity.getBody());
            return entity.getBody();
        } catch (RestClientResponseException exception) {
            lastResult = HttpCallResult.fromHttpError(exception.getRawStatusCode(),
                    exception.getResponseBodyAsString(), exception.getMessage());
            return null;
        }
    }

    public HttpCallResult<?> getLastResult() {
        return lastResult;
    }

    public Integer getStatusCode() {
        return hasResult() ? lastResult.getStatusCode() : null;
    }

    public Object getResponse() {
        return hasResult() ? lastResult.getResponse() : null;
    }

    /**
     * Reads the latest response body without changing the retained result.
     * Failure diagnostics contain only status and type information, never the body.
     *
     * @throws IllegalStateException if no result or body is available, or its type is incompatible
     */
    public <T> T getResponseBody(Class<T> responseType) {
        Objects.requireNonNull(responseType, "Response body type must not be null");
        if (!hasResult()) {
            throw new IllegalStateException("No HTTP result is available");
        }
        Object response = lastResult.getResponse();
        if (response == null) {
            throw new IllegalStateException("No response body is available for HTTP status " + getStatusCode());
        }
        if (!responseType.isInstance(response)) {
            throw new IllegalStateException("Expected response body of type " + responseType.getName()
                    + " but received " + response.getClass().getName()
                    + " for HTTP status " + getStatusCode());
        }
        return responseType.cast(response);
    }

    public String getErrorBody() {
        return hasResult() ? lastResult.getErrorBody() : null;
    }

    public String getErrorMessage() {
        return hasResult() ? lastResult.getErrorMessage() : null;
    }

    public boolean hasResult() {
        return lastResult != null;
    }

    public boolean isSuccessful() {
        return hasResult() && lastResult.isSuccessful();
    }

    /**
     * Whether the latest call raised an exception containing an HTTP response.
     */
    public boolean isHttpError() {
        return hasResult() && lastResult.isHttpError();
    }
}
