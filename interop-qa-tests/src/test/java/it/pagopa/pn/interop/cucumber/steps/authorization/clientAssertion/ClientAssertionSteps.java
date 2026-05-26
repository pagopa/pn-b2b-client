package it.pagopa.pn.interop.cucumber.steps.authorization.clientAssertion;

import io.cucumber.java.en.When;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.authorization.service.utils.voucher.domain.ClientAssertionOptions;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.authorization.model.VoucherContext;
import it.pagopa.pn.interop.cucumber.steps.dev_tools.config.DevToolsRequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.security.KeyPair;
import java.time.Instant;
import java.util.*;
import static it.pagopa.interop.authorization.service.utils.voucher.VoucherService.calculateKidFromPublicKey;
import static it.pagopa.pn.interop.cucumber.utility.CodecUtils.applyOverridesToEncodedJwt;

@Slf4j
public class ClientAssertionSteps {

    private final String clientAssertionJwtAudience;
    private final SharedStepsContext sharedStepsContext;
    private final VoucherContext voucherContext;

    public ClientAssertionSteps(
            @Value("${client.assertion.jwt.audience}") String clientAssertionJwtAudience,
            SharedStepsContext sharedStepsContext,
            VoucherContext voucherContext
    ) {
        this.clientAssertionJwtAudience = clientAssertionJwtAudience;
        this.sharedStepsContext = sharedStepsContext;
        this.voucherContext = voucherContext;
    }

    @When("{string} crea una client assertion valida per un client di tipo {interopClientType}")
    public void createValidClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType) {
        createCustomClientAssertion(clientType, Collections.emptyList());
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int}")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, String keyType, int keySize) {
        createCustomClientAssertionWithKey(clientType, Collections.emptyList(), keyType, keySize);
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int} con:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, String keyType, int keySize, List<DevToolsRequestConfig.JwtClaimOverride> overrides) {
        createCustomClientAssertionWithKey(clientType, overrides, keyType, keySize);
    }

    @When("{string} crea una client assertion per un client di tipo {interopClientType} con:")
    public void createClientAssertion(String tenantType, ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides) {
        createCustomClientAssertion(clientType, overrides);
    }

    private void createCustomClientAssertion(ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides) {
        DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        String clientAssertion = validClientAssertion.signWith(preparedClient.keyPair().getPrivate()).compact();

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, preparedClient.keyPair().getKeyPair());
        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        voucherContext.setActualClientAssertion(clientAssertion);
    }

    private void createCustomClientAssertionWithKey(ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides, String keyType, int keySize) {
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair(keyType, keySize);
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType);

        String clientAssertion = validClientAssertion.signWith(keyPair.getPrivate()).compact();

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, keyPair);
        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        voucherContext.setActualClientAssertion(clientAssertion);
    }

    private JwtBuilder buildValidClientAssertion(ClientAssertionOptions.ClientType clientType) {
        DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
        String rawClientId = preparedClient != null ? preparedClient.clientId().toString() : null;
        String rawKid = preparedClient != null ? calculateKidFromPublicKey(preparedClient.keyPair().getPublic()) : null;

        // Builder base "valido", poi alterato con DataTable
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(rawClientId)
                .subject(rawClientId)
                .audience().add(this.clientAssertionJwtAudience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(43200)))
                .header().add("kid", rawKid).and();

        if (clientType == ClientAssertionOptions.ClientType.CONSUMER) {
            UUID purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId();
            jwtBuilder.claim("purposeId", purposeId);
        }

        return jwtBuilder;
    }

    private void logClientAssertion(String clientAssertion) {
        log.info("Client assertion header: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[0])));
        log.info("Client assertion payload: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[1])));
        log.info("Client assertion: '{}'", clientAssertion);
    }
}
