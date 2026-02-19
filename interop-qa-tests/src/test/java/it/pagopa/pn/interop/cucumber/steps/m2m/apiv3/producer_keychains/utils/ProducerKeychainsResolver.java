package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.producer_keychains.utils;

import com.nimbusds.jose.jwk.KeyType;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.domain.Role;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeyUse;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.utils.AbstractResolver;
import it.pagopa.pn.interop.cucumber.steps.producer_keychains.model.ProducerKeychainsContext;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static it.pagopa.interop.authorization.service.DPoPTokenService.generateKeyPair;

@RequiredArgsConstructor
public class ProducerKeychainsResolver extends AbstractResolver {

    private final ProducerKeychainsContext context;
    private final SharedStepsContext sharedStepsContext;

    public KeySeed resolveKeySeed(String keyType, String key, String name, String alg, String use) {
        KeyPairDecorator keyPair = generateKeyPair(keyType);
        String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();
        it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed bffKeySeed = KeyPairGeneratorUtil.createKeySeed(encodedPublicKey, KeyType.parse(keyType)).get(0);

        String nKey = StepParser.nullOrValue(key);
        String nName = StepParser.nullOrValue(name);
        String nAlg = StepParser.nullOrValue(alg);
        String nUse = StepParser.nullOrValue(use);

        KeySeed keySeed = new KeySeed();
        keySeed.setKey(nKey != null ? bffKeySeed.getKey() : null);
        keySeed.setName(nName != null ? bffKeySeed.getName() : null);
        keySeed.setAlg(nAlg != null ? bffKeySeed.getAlg() : null);
        keySeed.setUse(nUse == null ? null : KeyUse.valueOf(nUse));

        return keySeed;
    }

    public UUID resolveKeychain(String raw) {
        return resolveOrParse(
                raw,
                UUID::fromString,
                context::getProducerKeychainId,
                context::getProducerKeychainId,
                UUID::randomUUID,
                () -> null

        );
    }

    public UUID resolveUserId(String raw) {
        final Role role = sharedStepsContext.getRole();
        final String tenant = sharedStepsContext.getTenantType();

        return resolveOrParse(
                raw,
                UUID::fromString,
                () -> sharedStepsContext.getIdentityService().getUserId(tenant, role.getValue()),
                () -> sharedStepsContext.getIdentityService().getUserId(tenant, role.getValue()),
                UUID::randomUUID,
                () -> null
        );
    }

    public String resolveKid(String raw) {
        return resolveOrParse(
                raw,
                (r) -> r,
                () -> context.getProducerKey().getJwk().getKid(),
                () -> context.getProducerKey().getJwk().getKid(),
                () -> UUID.randomUUID().toString(),
                () -> null
        );
    }
}