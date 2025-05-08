package it.pagopa.interop.authorization.service.utils.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Jwks;
import io.jsonwebtoken.security.PublicJwk;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.authorization.service.utils.voucher.exc.KidCalculationException;
import it.pagopa.interop.utils.InteropAPIErrorResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VoucherService {
    private final String clientAssertionJwtAudience;
    private final String authorizationServerTokenCreationUrl;
    private final RestTemplate restTemplate;

    public VoucherService(
        @Value("${client.assertion.jwt.audience}") String clientAssertionJwtAudience,
        @Value("${authorization.server.token.creation.url}") String authorizationServerTokenCreationUrl,
        @Autowired RestTemplate restTemplate)
    {
        this.clientAssertionJwtAudience = clientAssertionJwtAudience;
        this.authorizationServerTokenCreationUrl = authorizationServerTokenCreationUrl;
        this.restTemplate = restTemplate;
    }

    public String calculateKidFromPublicKey(PublicKey publicKey) {
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

    public ResponseEntity<VoucherResponse> requestVoucher(VoucherRequest request) {
        return requestVoucher(request, VoucherResponse.class);
    }

    public ResponseEntity<InteropAPIErrorResponse> requestVoucherExpectingError(VoucherRequest request) {
        return requestVoucher(request, InteropAPIErrorResponse.class);
    }

    private <T> ResponseEntity<T> requestVoucher(VoucherRequest request, Class<T> clss) {
        URI uri = UriComponentsBuilder
            .fromPath(this.authorizationServerTokenCreationUrl)
            .queryParam("client_id", request.getClientId())
            .queryParam("client_assertion", request.getClientAssertion())
            .queryParam("client_assertion_type", request.getClientAssertionType())
            .queryParam("grant_type", request.getGrantType())
            .build()
            .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<VoucherRequest> requestEntity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, requestEntity, clss);
    }
}
