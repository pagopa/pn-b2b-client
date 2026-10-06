package it.pagopa.common.http;

/**
 * An immutable description of one HTTP call. The response body is retained by reference.
 * Diagnostic messages and HTTP error bodies are kept separate from successful response bodies.
 *
 * @param <T> the response body type
 */
public final class HttpCallResult<T> {
    private final Integer statusCode;
    private final T response;
    private final String errorBody;
    private final String errorMessage;
    private final boolean httpError;

    private HttpCallResult(Integer statusCode, T response, String errorBody,
                           String errorMessage, boolean httpError) {
        this.statusCode = statusCode;
        this.response = response;
        this.errorBody = errorBody;
        this.errorMessage = errorMessage;
        this.httpError = httpError;
    }

    static <T> HttpCallResult<T> fromResponse(int statusCode, T response) {
        return new HttpCallResult<>(statusCode, response, null, null, false);
    }

    static <T> HttpCallResult<T> fromHttpError(int statusCode, String errorBody, String errorMessage) {
        return new HttpCallResult<>(statusCode, null, errorBody, errorMessage, true);
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public T getResponse() {
        return response;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccessful() {
        return !httpError && statusCode >= 200 && statusCode < 300;
    }

    public boolean isHttpError() {
        return httpError;
    }
}
