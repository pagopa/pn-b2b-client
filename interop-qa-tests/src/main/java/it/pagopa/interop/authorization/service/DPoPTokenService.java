package it.pagopa.interop.authorization.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.authorization.enums.TokenKey;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.DpopProofService;
import it.pagopa.interop.authorization.service.utils.voucher.DPoPVoucherService;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions.ClientType;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherRequest;
import it.pagopa.interop.authorization.service.utils.voucher.domain.VoucherResponse;
import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@ToString
@EqualsAndHashCode(callSuper = false)
public class DPoPTokenService extends AbstractClient {

    @Setter
    private IdentityService identityService;

    private final DPoPVoucherService voucherService;
    private final DpopProofService dpopProofService;
    private final Map<TokenKey, Pair<String, VoucherResponse>> tokenCache = new ConcurrentHashMap<>();

    @Getter
    private String usedClientAssertion;

    @Value("${authorization.server.token.creation.url}")
    private String dpopHtu;

    public record PreparedClient(UUID clientId, KeyPairDecorator keyPair, KeyType keyType) {
    }

    public DPoPTokenService(IdentityService identityService, DPoPVoucherService voucherService, DpopProofService dpopProofService, HttpCallExecutor httpCallExecutor) {
        this.identityService = identityService;
        this.voucherService = voucherService;
        this.dpopProofService = dpopProofService;
        super.setHttpCallExecutor(httpCallExecutor);
    }

    public Pair<String, VoucherResponse> getAccessToken(String dpopProof, String clientId, @NonNull KeyPair keyPair, @NonNull ClientType clientType, @NonNull String tenantType, String purposeId) {
        TokenKey tokenKey = TokenKey.of(tenantType, M2MRole.M2M_ADMIN);

        return tokenCache.computeIfAbsent(tokenKey, key -> {
            log.info("Richiesta access token (cached) - Tenant: {}, Client: {}", tenantType, clientId);
            return retrieveAccessToken(clientId, keyPair, clientType, purposeId, dpopProof);
        });
    }

    public Pair<String, VoucherResponse> getAccessTokenWithoutCache(String dpopProof, String clientId, KeyPair keyPair, ClientType clientType, @NonNull String tenantType, String purposeId) {
        log.info("Richiesta access token (no cache) - Tenant: {}, Client: {}", tenantType, clientId);
        return retrieveAccessToken(clientId, keyPair, clientType, purposeId, dpopProof);
    }

    private Pair<String, VoucherResponse> retrieveAccessToken(String clientId, KeyPair keyPair, ClientType clientKind, String purposeId, String dpopProof) {
        try {
            dpopProofService.verifyDpopProof(dpopProof);
        } catch (RuntimeException e) {
            log.warn("Proof DPoP non valida: {}", e.getMessage());
        }

        String clientAssertion = generateClientAssertion(clientId, keyPair, clientKind, purposeId);
        this.usedClientAssertion = clientAssertion;
        VoucherRequest request = VoucherRequest.builder()
                .clientId(clientId)
                .clientAssertion(clientAssertion)
                .build();

        return this.performOperation(SimpleOperation.of(
                () -> voucherService.requestVoucher(request, dpopProof),
                response -> Pair.of(dpopProof, new ObjectMapper().convertValue(response, VoucherResponse.class))
        )).orElse(Pair.of(dpopProof, new VoucherResponse()));
    }

    private Pair<String, VoucherResponse> retrieveAccessToken(String clientId, ClientType clientType, KeyPair keyPair, String purposeId, String dpopProof) {
        try {
            dpopProofService.verifyDpopProof(dpopProof);
        } catch (RuntimeException e) {
            log.warn("Proof DPoP non valida: {}", e.getMessage());
        }

        String clientAssertion = generateClientAssertion(clientId, keyPair, clientType, purposeId);
        VoucherRequest request = VoucherRequest.builder()
                .clientId(clientId)
                .clientAssertion(clientAssertion)
                .build();

        return this.performOperation(SimpleOperation.of(
                () -> voucherService.requestVoucher(request, dpopProof),
                response -> Pair.of(dpopProof, new ObjectMapper().convertValue(response, VoucherResponse.class))
        )).orElse(Pair.of(dpopProof, new VoucherResponse()));
    }

    public Pair<Integer, String> sendRequestWithDuplicateDpopHeaders(@NonNull String clientId, @NonNull KeyPair keyPair, @NonNull ClientType clientType, String purposeId, @NonNull String dpopProof) {
        try {
            String clientAssertion = generateClientAssertion(clientId, keyPair, clientType, purposeId);
            String requestBody = buildFormRequestBody(clientId, clientAssertion);

            HttpPost post = new HttpPost(dpopHtu);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("DPoP", dpopProof);
            post.addHeader("DPoP", dpopProof);
            post.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_FORM_URLENCODED));

            log.info("\nHTTP REQUEST\nPOST {}\nHeaders:{}\nBody:\n  {}", dpopHtu,
                    Arrays.stream(post.getHeaders())
                            .map(h -> "  " + h.getName() + ": " + h.getValue())
                            .collect(Collectors.joining("\n")),
                    requestBody);

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {

                int statusCode = response.getCode();
                String responseBody = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)
                        : "";

                log.info("Response Code: {}, Body: {}", statusCode, responseBody);
                return Pair.of(statusCode, responseBody);
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore nell'invio della richiesta con header DPoP duplicati", e);
        }
    }

    public DpopProofService.ValidationResult validateCnfJkt(String accessToken, String dpopJwt) {
        return dpopProofService.validateCnfJkt(accessToken, dpopJwt);
    }

    public static KeyPairDecorator generateKeyPair(String keyType) {
        return switch (keyType) {
            case "EC" -> KeyPairDecorator.of("EC", 256);
            case "RSA" -> KeyPairDecorator.of("RSA", 2048);
            default -> throw new IllegalArgumentException("Unsupported key type: " + keyType);
        };
    }

    public String buildDpopProof(KeyPairDecorator keyPair) {
        return dpopProofService.buildProof(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                "POST",
                dpopHtu
        );
    }

    public String buildDpopProof(KeyPair keyPair) {
        return dpopProofService.buildProof(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                "POST",
                dpopHtu
        );
    }

    public String buildProofWith(KeyPairDecorator keyPair, String typ, HttpMethod httpMethod, String htu) {
        return dpopProofService.buildProofWith(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                httpMethod.toString(),
                htu,
                typ
        );
    }

    public String buildProofWith(KeyPair keyPair, String typ, HttpMethod httpMethod, String htu) {
        return dpopProofService.buildProofWith(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                httpMethod.toString(),
                htu,
                typ
        );
    }

    public String buildProofWithAth(KeyPair keyPair, String typ, HttpMethod httpMethod, String htu, String accessToken) {
        return dpopProofService.buildProofWithAth(
                keyPair.getPrivate(),
                keyPair.getPublic(),
                httpMethod.toString(),
                htu,
                typ,
                accessToken
        );
    }

    // === UTILITY ===
    private String generateClientAssertion(@NonNull String clientId, @NonNull KeyPair keyPair, ClientType clientKind, String purposeId) {

        ClientAssertionOptions.ClientAssertionOptionsBuilder builder =
                ClientAssertionOptions.builder()
                        .clientType(clientKind)
                        .clientId(clientId)
                        .publicKey(keyPair.getPublic())
                        .privateKey(keyPair.getPrivate())
                        .assertionTtlSeconds(300);

        if (ClientType.CONSUMER.equals(clientKind)) {
            builder.purposeId(purposeId);
        }

        return voucherService.createClientAssertion(builder.build());
    }


    private String buildFormRequestBody(String clientId, String clientAssertion) {
        return "grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_assertion_type=" + URLEncoder.encode("urn:ietf:params:oauth:client-assertion-type:jwt-bearer", StandardCharsets.UTF_8)
                + "&client_assertion=" + URLEncoder.encode(clientAssertion, StandardCharsets.UTF_8);
    }
}
