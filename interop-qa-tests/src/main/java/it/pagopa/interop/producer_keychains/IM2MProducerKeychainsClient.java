package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;

import java.util.UUID;

public interface IM2MProducerKeychainsClient extends Authenticable {

    ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed);

    void deleteProducerKeychainKeyByKid(UUID keychainId, String keyId);

    ProducerKey getProducerKey(String kid);
}
