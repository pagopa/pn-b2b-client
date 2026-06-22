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

    @When("il tenant {currentActor} {string} crea una client assertion valida per un client di tipo {interopClientType}")
    public void createValidClientAssertion(String actor, String tenant, ClientAssertionOptions.ClientType clientType) {
        String purposeId = null;
        KeyPair keyPair = null;
        String clientId = null;
        switch (actor) {
            case "fruitore" -> {
                if (clientType == ClientAssertionOptions.ClientType.CONSUMER) {
                    purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId().toString();
                }
                DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
                keyPair = preparedClient.keyPair().getKeyPair();
                clientId = preparedClient.clientId().toString();
            }
            case "erogatore" -> {
                keyPair = sharedStepsContext.getProducerKeychainCommonContext().getProducerKeyPairs().get(0).getKeyPair();
                clientId = sharedStepsContext.getProducerKeychainCommonContext().getFirstProducerKeychainId().toString();
            }
        }
        createCustomClientAssertion(clientType, Collections.emptyList(), keyPair, clientId, purposeId);
    }

    @When("il tenant {currentActor} {string} crea una client assertion per un client di tipo {interopClientType} utilizzando una chiave {string} di lunghezza {int}")
    public void createClientAssertion(String actor, String tenant, ClientAssertionOptions.ClientType clientType, String keyType, int keySize) {
        String purposeId = null;
        String clientId = null;
        if (actor.equals("erogatore")) {
            // Producer has no prepared client
        } else if (actor.equals("fruitore")) {
            if (clientType == ClientAssertionOptions.ClientType.CONSUMER) {
                purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId().toString();
            }
            DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
            clientId = preparedClient.clientId().toString();
        } else {
            throw new RuntimeException("Actor not recognized");
        }
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair(keyType, keySize);
        createCustomClientAssertionWithKey(clientType, Collections.emptyList(), keyPair, clientId, purposeId);
    }

    @When("il tenant {currentActor} {string} crea una client assertion per un client di tipo {interopClientType} con:")
    public void createClientAssertion(String actor, String tenant, ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides) {
        String purposeId = null;
        KeyPair keyPair = null;
        String clientId = null;
        if (actor.equals("erogatore")) {
            keyPair = sharedStepsContext.getProducerKeychainCommonContext().getProducerKeyPairs().get(0).getKeyPair();
            clientId = sharedStepsContext.getProducerKeychainCommonContext().getFirstProducerKeychainId().toString();
        } else if (actor.equals("fruitore")) {
            if (clientType.equals(ClientAssertionOptions.ClientType.CONSUMER)) {
                purposeId = sharedStepsContext.getPurposeCommonContext().getLastPurposeId().toString();
            }
            DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getClientCommonContext().getLastPreparedClient();
            keyPair = preparedClient.keyPair().getKeyPair();
            clientId = preparedClient.clientId().toString();
        } else {
            throw new RuntimeException("Actor not recognized");
        }
        createCustomClientAssertionWithKey(clientType, overrides, keyPair, clientId, purposeId);
    }

    private void createCustomClientAssertion(ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides, KeyPair keyPair, String clientId, String purposeId) {

        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType, keyPair, clientId, purposeId);

        String clientAssertion = validClientAssertion.signWith(keyPair.getPrivate()).compact();

        if (voucherContext.getActualInteractionId() != null && !voucherContext.getActualInteractionId().isBlank()) {
            overrides.add(new DevToolsRequestConfig.JwtClaimOverride("interactionId", voucherContext.getActualInteractionId()));
        }

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, keyPair);
        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        voucherContext.setActualClientAssertion(clientAssertion);
    }

    private void createCustomClientAssertionWithKey(ClientAssertionOptions.ClientType clientType, List<DevToolsRequestConfig.JwtClaimOverride> overrides, KeyPair keyPair, String clientId, String purposeId) {
        JwtBuilder validClientAssertion = buildValidClientAssertion(clientType, keyPair, clientId, purposeId);

        String clientAssertion = validClientAssertion.signWith(keyPair.getPrivate()).compact();

        if (overrides.stream().noneMatch(i -> "interactionId".equals(i.claim())) &&
            voucherContext.getActualInteractionId() != null &&
            !voucherContext.getActualInteractionId().isBlank()) {
            overrides.add(new DevToolsRequestConfig.JwtClaimOverride("interactionId", voucherContext.getActualInteractionId()));
        }

        try {
            clientAssertion = applyOverridesToEncodedJwt(clientAssertion, overrides, keyPair);
        } catch (Exception e) {
            throw new RuntimeException("Error processing JSON for client assertion header: " + e.getMessage(), e);
        }

        logClientAssertion(clientAssertion);
        voucherContext.setActualClientAssertion(clientAssertion);
    }

    private JwtBuilder buildValidClientAssertion(ClientAssertionOptions.ClientType clientType, KeyPair keyPair, String clientId, String purposeId) {
        String rawKid = keyPair != null ? calculateKidFromPublicKey(keyPair.getPublic()) : null;

        // Builder base "valido", poi alterato con DataTable
        JwtBuilder jwtBuilder = Jwts.builder()
                .issuer(clientId)
                .subject(clientId)
                .audience().add(this.clientAssertionJwtAudience).and()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(43200)))
                .header().add("kid", rawKid).and();

        if (purposeId != null && clientType == ClientAssertionOptions.ClientType.CONSUMER) {
            jwtBuilder.claim("purposeId", UUID.fromString(purposeId));
        }

        return jwtBuilder;
    }

    private void logClientAssertion(String clientAssertion) {
        log.info("Client assertion header: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[0])));
        log.info("Client assertion payload: '{}'", new String(Base64.getUrlDecoder().decode(clientAssertion.split("\\.")[1])));
        log.info("Client assertion: '{}'", clientAssertion);
    }
}
