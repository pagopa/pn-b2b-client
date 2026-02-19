package it.pagopa.interop.common.interceptor.dpop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Slf4j
public class DigestValidationInterceptor implements ClientHttpRequestInterceptor {

    private static final String DIGEST_HEADER = "Digest";
    private static final String ALG_PREFIX = "SHA-256=";

    private final boolean failOnMissingDigest;

    public DigestValidationInterceptor(boolean failOnMissingDigest) {
        this.failOnMissingDigest = failOnMissingDigest;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);

        // Legge TUTTO il body (consuma lo stream)
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());

        String digestHeader = firstHeaderValue(response.getHeaders(), DIGEST_HEADER);

        if (digestHeader == null || digestHeader.isBlank()) {
            if (failOnMissingDigest) {
                throw new DigestValidationException("Missing Digest header for " + request.getMethod() + " " + request.getURI());
            } else {
                log.warn("Missing Digest header for {} {}", request.getMethod(), request.getURI());
            }
            return new CachedBodyClientHttpResponse(response, responseBody);
        }

        String expected = extractSha256Base64(digestHeader);
        if (expected == null) {
            // Non troviamo SHA-256=... (es. header con altri algoritmi)
            throw new DigestValidationException("Unsupported Digest header format: " + digestHeader);
        }

        String actual = sha256Base64(responseBody);

        if (!constantTimeEquals(expected, actual)) {
            // per debug utile loggare anche qualche info
            String preview = new String(responseBody, 0, Math.min(responseBody.length, 256), StandardCharsets.UTF_8);
            throw new DigestValidationException(
                    "Digest mismatch for " + request.getMethod() + " " + request.getURI()
                            + " expected=" + expected + " actual=" + actual
                            + " bodyLen=" + responseBody.length
                            + " bodyPreview=" + preview
            );
        }

        log.debug("Digest OK for {} {} (len={})", request.getMethod(), request.getURI(), responseBody.length);

        // Restituiamo una response con body “riutilizzabile”
        return new CachedBodyClientHttpResponse(response, responseBody);
    }

    private static String firstHeaderValue(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    /**
     * Supporta:
     * Digest: SHA-256=BASE64
     * Digest: SHA-256=BASE64, SHA-512=...
     */
    private static String extractSha256Base64(String digestHeader) {
        // header può contenere più valori separati da virgola
        String[] parts = digestHeader.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.regionMatches(true, 0, ALG_PREFIX, 0, ALG_PREFIX.length())) {
                return trimmed.substring(ALG_PREFIX.length()).trim();
            }
        }
        return null;
    }

    private static String sha256Base64(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute SHA-256", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    /**
     * Wrapper che “rimpiazza” lo stream consumato con un ByteArrayInputStream.
     */
    private static final class CachedBodyClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse delegate;
        private final byte[] body;

        private CachedBodyClientHttpResponse(ClientHttpResponse delegate, byte[] body) {
            this.delegate = delegate;
            this.body = body;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return delegate.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return delegate.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return delegate.getStatusText();
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }
    }

    public static class DigestValidationException extends RuntimeException {
        public DigestValidationException(String message) {
            super(message);
        }
    }
}

