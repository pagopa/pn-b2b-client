package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;

import java.util.UUID;

public interface IM2MProducerKeychainsClient {

    ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed);
    void deleteProducerKeychainKeyById(UUID keychainId, String keyId);

    ProducerKey getProducerKey(String kid);
}
