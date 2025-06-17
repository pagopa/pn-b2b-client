package it.pagopa.interop.authorization.service.utils.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.exc.KidCalculationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static dev.turingcomplete.textcaseconverter.StandardTextCases.SNAKE_CASE;
import static dev.turingcomplete.textcaseconverter.StandardTextCases.SOFT_CAMEL_CASE;

@Component
@RequiredArgsConstructor
public class DPoPVoucherService {

    @Value("${client.assertion.jwt.audience}")
    private String jwtAudience;

    @Value("${authorization.server.token.creation.url}")
    private String tokenEndpoint;

    private final RestTemplate restTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Effettua una request.
     */
    public Map<String, Object> requestVoucher(VoucherRequest request, String dpopJwt) {
        HttpHeaders headers = new HttpHeaders();
        if (dpopJwt != null && !dpopJwt.isBlank()) {
            headers.set("DPoP", dpopJwt);
        }
        return doVoucherRequest(request, headers);
    }


    /**
     * Crea una client assertion JWT firmata con la chiave privata del client.
     */
    public String createClientAssertion(ClientAssertionOptions options) {
        long issuedAt = Instant.now().getEpochSecond();

        // Se viene fornito un TTL valido (> 0), calcola exp in base a esso
        long expiration = options.getAssertionTtlSeconds() > 0
                ? issuedAt + options.getAssertionTtlSeconds()
                : issuedAt + 43200 * 60; // Default: 30 giorni in secondi

        JwtBuilder builder = Jwts.builder()
                .claim("iss", options.getClientId())
                .claim("sub", options.getClientId())
                .claim("aud", jwtAudience)
                .claim("jti", UUID.randomUUID().toString())
                .claim("iat", issuedAt)
                .claim("exp", expiration);

        if (options.getClientType() == ClientAssertionOptions.ClientType.CONSUMER) {
            builder.claim("purposeId", options.getPurposeId());
        }

        if (options.isDigestIncluded()) {
            builder.claim("digest", Map.of(
                    "alg", "SHA256",
                    "value", "5db26201b684761d2b970329ab8596773164ba1b43b1559980e20045941b8065"
            ));
        }

        /*
        if (options.getConfirmationKeyThumbprint() != null) {
            builder.claim("cnf", Map.of(
                    "jkt", options.getConfirmationKeyThumbprint()
            ));
        }

         */

        builder.header()
                .add("kid", calculateKidFromPublicKey(options.getPublicKey()))
                .add("alg", "RS256")
                .add("typ", "JWT");

        return builder.signWith(options.getPrivateKey()).compact();
    }


    /**
     * Calcola il "kid" per la chiave pubblica fornita secondo RFC7638.
     */
    public String calculateKidFromPublicKey(PublicKey publicKey) {
        try {
            PublicJwk<PublicKey> jwk = Jwks.builder().key(publicKey).build();

            LinkedHashMap<String, Object> sortedJwk = jwk.entrySet().stream()
                    .sorted(Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            String jwkJson = objectMapper.writeValueAsString(sortedJwk);
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(jwkJson.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new KidCalculationException(e);
        }
    }

    private Map<String, Object> doVoucherRequest(VoucherRequest request, HttpHeaders headers) {
        URI uri = UriComponentsBuilder.fromHttpUrl(tokenEndpoint).build().toUri();

        HttpHeaders finalHeaders = new HttpHeaders();
        finalHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (headers != null) finalHeaders.addAll(headers);

        try {
            Map<String, Object> props = org.apache.commons.beanutils.PropertyUtils.describe(request);
            MultiValueMap<String, Object> body = props.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> Map.entry(SNAKE_CASE.convertFrom(SOFT_CAMEL_CASE, e.getKey()), List.of(e.getValue())))
                    .collect(Collectors.toMap(
                            Entry::getKey,
                            Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedMultiValueMap::new
                    ));

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, finalHeaders);

            ResponseEntity<Object> response = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);
            Object responseBody = response.getBody();

            if (responseBody instanceof Map<?, ?> map) {
                return map.entrySet().stream()
                        .filter(e -> e.getKey() instanceof String)
                        .collect(Collectors.toMap(
                                e -> (String) e.getKey(),
                                Entry::getValue
                        ));
            }
            throw new RuntimeException("Risposta non valida: atteso un oggetto JSON");
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Errore durante l'ispezione dell'oggetto " + request.getClass().getSimpleName(), e);
        }
    }
}
