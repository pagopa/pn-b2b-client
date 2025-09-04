package it.pagopa.pn.client.b2b.pa.wrapper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

public final class ApiCallHelper {

    private ApiCallHelper() {}

    @FunctionalInterface
    public interface RestCall<T> {
        ResponseEntity<T> execute() throws Exception;
    }

    /** Chiamata generica che cattura errori e ritorna ApiResult<T>. */
    public static <T> ApiResult<T> call(RestCall<T> call) {
        try {
            ResponseEntity<T> resp = call.execute();
            return new ApiResult<>(resp.getStatusCode(), resp.getBody(), null, resp.getHeaders());

        } catch (RestClientResponseException httpEx) {
            HttpHeaders h = safeHeaders(httpEx);
            String problem = extractProblemAsString(httpEx);
            HttpStatus status = HttpStatus.resolve(httpEx.getRawStatusCode());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ApiResult<>(status, null, problem, h);

        } catch (ResourceAccessException ioEx) {
            return new ApiResult<>(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    null,
                    "Upstream unavailable: " + ioEx.getClass().getSimpleName(),
                    new HttpHeaders()
            );

        } catch (Exception ex) {
            return new ApiResult<>(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    "Unexpected: " + ex.getClass().getSimpleName(),
                    new HttpHeaders()
            );
        }
    }

    /** Ritorna un ApiResult 400 già pronto. */
    public static <T> ApiResult<T> badRequest(String detail) {
        return new ApiResult<>(HttpStatus.BAD_REQUEST, null, detail, new HttpHeaders());
    }

    /* ===================== Helpers interni ===================== */

    private static HttpHeaders safeHeaders(RestClientResponseException ex) {
        HttpHeaders h = ex.getResponseHeaders();
        return (h != null) ? h : new HttpHeaders();
    }

    private static String extractProblemAsString(RestClientResponseException ex) {
        MediaType ct = null;
        HttpHeaders h = ex.getResponseHeaders();
        if (h != null) ct = h.getContentType();

        String body;
        try {
            body = ex.getResponseBodyAsString();
        } catch (Exception ignore) {
            body = "";
        }
        if (body == null) body = "";

        boolean isProblem =
                ct != null &&
                        "application".equalsIgnoreCase(ct.getType()) &&
                        "problem+json".equalsIgnoreCase(ct.getSubtype());

        String out = isProblem ? body : (body.isBlank() ? null : body);
        return truncate(out, 4000);
    }

    private static String truncate(String s, int max) {
        return (s == null || s.length() <= max) ? s : s.substring(0, max);
    }
}
