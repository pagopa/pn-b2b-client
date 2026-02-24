package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;

import java.util.UUID;

public interface IProducerKeychainsClient extends SettableBearerToken {

    CreatedResource createProducerKeychain(ProducerKeychainSeed producerKeychainSeed);

    ProducerKeychain getProducerKeychain(UUID producerKeychainId);

    void createProducerKeychainKey(UUID producerKeychainId, KeySeed keySeed);
}
