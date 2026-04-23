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

import java.util.UUID;

import static it.pagopa.interop.authorization.service.DPoPTokenService.generateKeyPair;

public class ProducerKeychainsResolver extends AbstractResolver {

    private final ProducerKeychainsContext context;

    public ProducerKeychainsResolver(ProducerKeychainsContext context, SharedStepsContext sharedStepsContext) {
        super(sharedStepsContext);
        this.context = context;
    }

    public KeySeed resolveKeySeed(String keyType, String key, String name, String alg, String use) {
        KeyPairDecorator keyPair = generateKeyPair(keyType);
        String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();
        it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed bffKeySeed = KeyPairGeneratorUtil.createKeySeed(encodedPublicKey, KeyType.parse(keyType)).get(0);

        String nKey = StepParser.nullOrValue(key);
        String nName = StepParser.nullOrValue(name);
        String nAlg = StepParser.nullOrValue(alg);
        String nUse = StepParser.nullOrValue(use);

        KeySeed keySeed = new KeySeed();
        keySeed.setKey(resolveKey(isValidToken(nKey) ? bffKeySeed.getKey() : nKey));
        keySeed.setName(resolveName(isValidToken(nName) ? bffKeySeed.getName() : nName));
        keySeed.setAlg(resolveAlg(isValidToken(nAlg) ? bffKeySeed.getAlg() : nAlg));
        keySeed.setUse(resolveUse(isValidToken(nUse) ? bffKeySeed.getUse().getValue() : nUse));

        return keySeed;
    }

    private boolean isValidToken(String raw){
        final String VALID_TOKEN = "%valid";
        return raw != null && raw.equals(VALID_TOKEN);
    }
    private String resolveKey(String raw){
        return resolveOrParse(
                raw,
                (value) -> value,
                () -> context.getActualKeySeed().getKey(),
                () -> context.getActualKeySeed().getKey()
        );
    }

    private String resolveName(String raw){
        return resolveOrParse(
                raw,
                (value) -> value,
                () -> context.getActualKeySeed().getName(),
                () -> context.getActualKeySeed().getName()
        );
    }

    private String resolveAlg(String raw){
        return resolveOrParse(
                raw,
                (value) -> value,
                () -> context.getActualKeySeed().getAlg(),
                () -> context.getActualKeySeed().getAlg()
        );
    }

    private KeyUse resolveUse(String raw){
        return resolveOrParse(
                raw,
                (value) -> value != null ? KeyUse.valueOf(value) : null,
                () -> context.getActualKeySeed().getUse(),
                () -> context.getActualKeySeed().getUse()
        );
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

    public Integer resolveInteger(String raw) {
        return resolveOrParse(raw, Integer::valueOf, null, null, null, () -> null);
    }
}