package it.pagopa.interop.common.interceptor.dpop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.common.interceptor.dpop.utils.AgidJwtSignatureVerifier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class IntegrityValidationInterceptor implements ClientHttpRequestInterceptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String DIGEST_HEADER = "Digest";
    private static final String AGID_JWT_SIGNATURE_HEADER = "Agid-JWT-Signature";
    private static final String X_CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String ALG_PREFIX = "SHA-256=";

    private final AgidJwtSignatureVerifier agidJwtSignatureVerifier;

    private final boolean failOnMissingDigest;
    private final boolean failOnMissingAgidJwtSignature;
    private final boolean treatMissingResponseHeaderAsEmptyString;
    private final Supplier<Auth> authSupplier;

    public IntegrityValidationInterceptor(
            boolean failOnMissingDigest,
            boolean failOnMissingAgidJwtSignature,
            boolean treatMissingResponseHeaderAsEmptyString,
            Supplier<Auth> authSupplier,
            AgidJwtSignatureVerifier agidJwtSignatureVerifier
    ) {
        this.failOnMissingDigest = failOnMissingDigest;
        this.failOnMissingAgidJwtSignature = failOnMissingAgidJwtSignature;
        this.treatMissingResponseHeaderAsEmptyString = treatMissingResponseHeaderAsEmptyString;
        this.authSupplier = authSupplier;
        this.agidJwtSignatureVerifier = agidJwtSignatureVerifier;
    }

    @Override
    public ClientHttpResponse intercept(
            org.springframework.http.HttpRequest request,
            byte[] requestBody,
            ClientHttpRequestExecution execution
    ) throws IOException {

        ClientHttpResponse response = execution.execute(request, requestBody);

        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());

        try {
            validateDigestIfPresentOrRequired(request, response, responseBody);
            validateAgidJwtSignatureIfPresentOrRequired(request, response);
        } catch (IntegrityValidationException ex) {
            logIntegrityFailure(request, response, responseBody, ex);
            throw ex;
        }

        return new CachedBodyClientHttpResponse(response, responseBody);
    }

    private void logIntegrityFailure(
            org.springframework.http.HttpRequest request,
            ClientHttpResponse response,
            byte[] responseBody,
            IntegrityValidationException ex
    ) {
        try {
            HttpStatus status = response.getStatusCode();
            HttpHeaders headers = response.getHeaders();

            String bodyPreview = previewBody(responseBody, 2048);
            Map<String, List<String>> safeHeaders = redactHeaders(headers);

            log.error(
                    "Integrity validation FAILED for {} {} -> status={} ({})\nResponse headers={}\nResponse bodyLen={} preview={}\nCause={}",
                    request.getMethod(),
                    request.getURI(),
                    status.value(),
                    status.getReasonPhrase(),
                    safeHeaders,
                    responseBody != null ? responseBody.length : -1,
                    bodyPreview,
                    ex.getMessage()
            );
        } catch (Exception logEx) {
            log.error(
                    "Integrity validation FAILED for {} {} (unable to log response details). Cause={}",
                    request.getMethod(),
                    request.getURI(),
                    ex.getMessage(),
                    logEx
            );
        }
    }

    private static String previewBody(byte[] body, int maxBytes) {
        if (body == null || body.length == 0) return "<empty>";
        int len = Math.min(body.length, maxBytes);
        String s = new String(body, 0, len, StandardCharsets.UTF_8);
        if (body.length > maxBytes) s += "...(truncated)";
        return s;
    }

    private static Map<String, List<String>> redactHeaders(HttpHeaders headers) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        headers.forEach((k, v) -> {
            if (k == null) return;
            String key = k.trim();
            if (key.equalsIgnoreCase("Authorization")
                    || key.equalsIgnoreCase("DPoP")
                    || key.equalsIgnoreCase("Cookie")
                    || key.equalsIgnoreCase("Set-Cookie")) {
                out.put(key, List.of("<redacted>"));
            } else {
                out.put(key, v);
            }
        });
        return out;
    }

    private void validateDigestIfPresentOrRequired(
            org.springframework.http.HttpRequest request,
            ClientHttpResponse response,
            byte[] responseBody
    ) {
        String digestHeader = firstHeaderValue(response.getHeaders(), DIGEST_HEADER);

        if (digestHeader == null || digestHeader.isBlank()) {
            if (failOnMissingDigest) {

                HttpStatus httpStatus = null;
                try {
                    httpStatus = response.getStatusCode();
                } catch (IOException e) {
                    // throw new RuntimeException(e);
                }

                throw new IntegrityValidationException(
                        "Missing Digest header for " + request.getMethod() + " " + request.getURI(),
                        httpStatus
                );
            }
            log.debug("Digest header missing for {} {}", request.getMethod(), request.getURI());
            return;
        }

        String expected = extractSha256Base64(digestHeader);
        if (expected == null) {
            throw new IntegrityValidationException("Unsupported Digest header format: " + digestHeader);
        }

        String actual = sha256Base64(responseBody);
        if (!constantTimeEquals(expected, actual)) {
            throw new IntegrityValidationException(
                    "Digest mismatch for " + request.getMethod() + " " + request.getURI()
                            + " expected=" + expected + " actual=" + actual + " bodyLen=" + responseBody.length
            );
        }
    }

    private void validateAgidJwtSignatureIfPresentOrRequired(
            org.springframework.http.HttpRequest request,
            ClientHttpResponse response
    ) {
        String agidJwt = firstHeaderValue(response.getHeaders(), AGID_JWT_SIGNATURE_HEADER);

        if (agidJwt == null || agidJwt.isBlank()) {
            if (failOnMissingAgidJwtSignature) {
                throw new IntegrityValidationException(
                        "Missing Agid-JWT-Signature header for " + request.getMethod() + " " + request.getURI()
                );
            }
            log.debug("Agid-JWT-Signature missing for {} {}", request.getMethod(), request.getURI());
            return;
        }

        agidJwtSignatureVerifier.verify(agidJwt);

        String[] parts = agidJwt.split("\\.");
        if (parts.length != 3) {
            throw new IntegrityValidationException("Agid-JWT-Signature is not a JWT (3 parts expected)");
        }

        JsonNode jwtHeader = readJsonB64Url(parts[0]);
        JsonNode jwtPayload = readJsonB64Url(parts[1]);

        String alg = optText(jwtHeader, "alg");
        if (!"RS256".equals(alg)) {
            throw new IntegrityValidationException("Agid-JWT-Signature alg is not RS256: " + alg);
        }

        require(jwtPayload, "iat");
        require(jwtPayload, "exp");
        require(jwtPayload, "iss");
        require(jwtPayload, "jti");
        require(jwtPayload, "signed_headers");

        requireAbsent(jwtPayload, "aud");
        requireAbsent(jwtPayload, "sub");
        requireAbsent(jwtPayload, "nbf");

        Auth auth = authSupplier != null ? authSupplier.get() : null;
        DpopHeaderPolicy.Mode mode = auth != null && auth.getDpopHeaderPolicy() != null
                ? auth.getDpopHeaderPolicy().getMode()
                : DpopHeaderPolicy.Mode.NORMAL;

        // client_id obbligatorio solo nel flusso standard
        if (mode == DpopHeaderPolicy.Mode.NORMAL) {
            require(jwtPayload, "client_id");

            String tokenClientId = optText(jwtPayload, "client_id");
            String expectedClientId = auth != null ? auth.getClientId() : null;

            if (expectedClientId == null || expectedClientId.isBlank()) {
                throw new IntegrityValidationException("Auth/clientId non disponibile per validare il claim client_id");
            }

            if (!expectedClientId.equals(tokenClientId)) {
                throw new IntegrityValidationException(
                        "client_id mismatch expected='" + expectedClientId + "' actual='" + tokenClientId + "'"
                );
            }
        }

        JsonNode signedHeaders = jwtPayload.get("signed_headers");
        if (!signedHeaders.isArray()) {
            throw new IntegrityValidationException("signed_headers claim is not an array");
        }

        String correlationId = firstHeaderValue(response.getHeaders(), X_CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            throw new IntegrityValidationException("Missing X-Correlation-Id header in response");
        }

        boolean correlationIdFoundInSignedHeaders = false;

        for (JsonNode headerObject : signedHeaders) {
            if (!headerObject.isObject() || headerObject.size() != 1) {
                throw new IntegrityValidationException(
                        "Each signed_headers element must contain exactly one header entry"
                );
            }

            Map.Entry<String, JsonNode> entry = headerObject.fields().next();

            String headerName = entry.getKey();
            String expectedValue = entry.getValue().asText();

            String actualValue = response.getHeaders().getFirst(headerName);

            if (actualValue == null) {
                actualValue = treatMissingResponseHeaderAsEmptyString ? "" : null;
            }

            if (!expectedValue.equals(actualValue)) {
                throw new IntegrityValidationException(
                        "signed_headers mismatch for '" + headerName +
                                "' expected='" + expectedValue +
                                "' actual='" + actualValue + "'"
                );
            }

            if (headerName.equalsIgnoreCase(X_CORRELATION_ID_HEADER)) {
                correlationIdFoundInSignedHeaders = true;
                if (!correlationId.equals(expectedValue)) {
                    throw new IntegrityValidationException(
                            "X-Correlation-Id mismatch between response header and signed_headers " +
                                    "expected='" + expectedValue + "' actual='" + correlationId + "'"
                    );
                }
            }
        }

        if (!correlationIdFoundInSignedHeaders) {
            throw new IntegrityValidationException("signed_headers does not contain X-Correlation-Id");
        }
    }

    private static String firstHeaderValue(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }

    private static String extractSha256Base64(String digestHeader) {
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
            return Base64.getEncoder().encodeToString(md.digest(data));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute SHA-256", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) result |= a.charAt(i) ^ b.charAt(i);
        return result == 0;
    }

    private static JsonNode readJsonB64Url(String b64url) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(b64url);
            return MAPPER.readTree(new String(decoded, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IntegrityValidationException("Cannot decode JWT part: " + e.getMessage(), e);
        }
    }

    private static void require(JsonNode payload, String field) {
        if (!payload.has(field)) {
            throw new IntegrityValidationException("Missing claim: " + field);
        }
    }

    private static void requireAbsent(JsonNode payload, String field) {
        if (payload.has(field)) {
            throw new IntegrityValidationException("Claim must be absent: " + field);
        }
    }

    private static String optText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }

    @Getter
    public static class IntegrityValidationException extends RuntimeException {
        private final HttpStatus httpStatus;

        public IntegrityValidationException(String message) {
            super(message);
            this.httpStatus = null;
        }
        public IntegrityValidationException(String message, Throwable cause) {
            super(message, cause);
            this.httpStatus = null;
        }
        public IntegrityValidationException(String message, HttpStatus httpStatus) {
            super(message);
            this.httpStatus = httpStatus;
        }
    }

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
}