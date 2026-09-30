package it.pagopa.interop.authorization.service.utils.voucher;

import static dev.turingcomplete.textcaseconverter.StandardTextCases.SNAKE_CASE;
import static dev.turingcomplete.textcaseconverter.StandardTextCases.SOFT_CAMEL_CASE;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.exc.KidCalculationException;
import it.pagopa.interop.utils.InteropAPIErrorResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VoucherService {
    private static class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {
        private NoOpResponseErrorHandler() {
        }

        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }

    private final String clientAssertionJwtAudience;
    private final String authorizationServerTokenCreationUrl;
    private final RestTemplate restTemplate;
    private static final String OBJECT_INSPECTION_ERROR = "Ispezione dell'oggetto %s fallita";

    public VoucherService(
            @Value("${client.assertion.jwt.audience}") String clientAssertionJwtAudience,
            @Value("${authorization.server.token.creation.url}") String authorizationServerTokenCreationUrl,
            @Autowired RestTemplate restTemplate)
    {
        this.clientAssertionJwtAudience = clientAssertionJwtAudience;
        this.authorizationServerTokenCreationUrl = authorizationServerTokenCreationUrl;
        this.restTemplate = restTemplate;
    }

    public static String calculateKidFromPublicKey(PublicKey publicKey) {
        try {
            PublicJwk<PublicKey> publicJwk = Jwks.builder().key(publicKey).build();

            LinkedHashMap<String, Object> sortedJwk = publicJwk.entrySet().stream()
                    .sorted(Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            String jsonJwk = new ObjectMapper().writeValueAsString(sortedJwk);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(jsonJwk.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new KidCalculationException(e);
        }
    }

    public String createClientAssertion(ClientAssertionOptions options) {
        // get timestamp in Epoch seconds
        long issuedAt = Instant.now().getEpochSecond();

        /* Define JWT body */
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("iss", options.getClientId())
                .claim("sub", options.getClientId())
                .claim("aud", this.clientAssertionJwtAudience)
                .claim("jti", UUID.randomUUID().toString())
                .claim("iat", issuedAt)
                .claim("exp", issuedAt + 43200 * 60);
        if(options.getClientType() == ClientType.CONSUMER) {
            jwtBuilder.claim("purposeId", options.getPurposeId());
        }
        if (options.isDigestIncluded()) {
            jwtBuilder.claim("digest", Map.of(
                    "alg", "SHA256",
                    "value", "5db26201b684761d2b970329ab8596773164ba1b43b1559980e20045941b8065"
            ));
        }

        /* Define JWT header */
        jwtBuilder.header()
                .add("kid", calculateKidFromPublicKey(options.getPublicKey()))
                .add("alg", "RS256")
                .add("typ", "JWT");

        return jwtBuilder.signWith(options.getPrivateKey()).compact();
    }

    public ResponseEntity<InteropAPIErrorResponse> requestVoucherExpectingError(VoucherRequest request) {
        return requestVoucher(request, InteropAPIErrorResponse.class);
    }

    public Map<String, Object> requestVoucher(VoucherRequest request) {
        return doVoucherRequest(request, null);
    }

    public Map<String, Object> requestVoucher(VoucherRequest request, String dpopJwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("DPoP", dpopJwt);
        return doVoucherRequest(request, headers);
    }

    private byte[] getBodyFromResponse(ResponseEntity<Object> response) {
        return response.getBody() != null
                ? response.getBody().toString().getBytes(StandardCharsets.UTF_8)
                : null;
    }

    private Map<String, Object> doVoucherRequest(VoucherRequest request, HttpHeaders extraHeaders) {
        ResponseErrorHandler originalErrorHandler = restTemplate.getErrorHandler();
        try {

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(this.authorizationServerTokenCreationUrl)
                    .build()
                    .toUri();

            // Header base + eventuali extra (es. DPoP)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            if (extraHeaders != null) {
                headers.addAll(extraHeaders);
            }

            Map<String, Object> properties = PropertyUtils.describe(request);
            MultiValueMap<String, Object> map = properties.entrySet().stream()
                    .map(e -> Map.entry(
                            SNAKE_CASE.convertFrom(SOFT_CAMEL_CASE, e.getKey()),
                            List.of(e.getValue())))
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedMultiValueMap::new
                    ));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

            restTemplate.setErrorHandler(new NoOpResponseErrorHandler());
            ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, Object.class);

            if (response.getStatusCode().isError()) {
                if (response.getStatusCode().is4xxClientError()) {
                    throw new HttpClientErrorException(
                            response.getStatusCode(),
                            response.getStatusCode().getReasonPhrase(),
                            response.getHeaders(),
                            getBodyFromResponse(response),
                            StandardCharsets.UTF_8
                    );
                } else if (response.getStatusCode().is5xxServerError()) {
                    throw new HttpServerErrorException(
                            response.getStatusCode(),
                            response.getStatusCode().getReasonPhrase(),
                            response.getHeaders(),
                            getBodyFromResponse(response),
                            StandardCharsets.UTF_8
                    );
                } else {
                    throw new RuntimeException("Errore durante la richiesta al token di accesso: " + response.getStatusCode());
                }
            }

            Object responseBody = response.getBody();
            if (responseBody instanceof Map<?, ?> mapResponse) {
                return mapResponse.entrySet().stream()
                        .filter(e -> e.getKey() instanceof String)
                        .collect(Collectors.toMap(
                                e -> (String) e.getKey(),
                                Entry::getValue
                        ));
            } else {
                throw new RuntimeException("Risposta non valida: atteso un oggetto JSON (Map<String, Object>)");
            }

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(OBJECT_INSPECTION_ERROR.formatted(request.getClass().getName()), e);
        } finally {
            restTemplate.setErrorHandler(originalErrorHandler);
        }
    }

    private <T> ResponseEntity<T> requestVoucher(VoucherRequest request, Class<T> clss) {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(this.authorizationServerTokenCreationUrl)
                    .build()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            /* TODO 21/05/2025 provare a riformulare la creazione usando Jackson al posto
             *   di apache.commons e textcaseconverter */
            Map<String, Object> voucherRequestProperties = PropertyUtils.describe(request);
            MultiValueMap<String, Object> map = voucherRequestProperties.entrySet().stream()
                    .map(e -> Map.entry(
                            SNAKE_CASE.convertFrom(SOFT_CAMEL_CASE, e.getKey()),
                            List.of(e.getValue())))
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedMultiValueMap::new
                    ));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
            /*ResponseEntity<T> exchange = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
                clss);*/
            ResponseEntity<Object> exchange = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
                    Object.class);
            return (ResponseEntity<T>) exchange;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Ispezione dell'oggetto %s fallita".formatted(request.getClass().getName()), e);
        }
    }
}
