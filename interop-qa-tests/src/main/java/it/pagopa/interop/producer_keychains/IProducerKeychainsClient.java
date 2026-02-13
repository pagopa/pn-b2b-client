package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;

import java.util.UUID;

public interface IProducerKeychainsClient {

    CreatedResource createProducerKeychain(ProducerKeychainSeed producerKeychainSeed);

    ProducerKeychain getProducerKeychain(UUID producerKeychainId);
}
