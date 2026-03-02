package it.pagopa.interop.producerkeychain;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceAdditionDetailsSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import java.util.List;
import java.util.UUID;

public interface ProducerKeychainClient extends IClient<ProducerKeychain, UUID> {
    UUID create(ProducerKeychainSeed seed);

    void linkEService(UUID producerKeychainId, EServiceAdditionDetailsSeed seed);

    void createProducerKey(UUID keychainId, KeySeed keySeed);

    List<String> getProducerKeysIds(UUID producerKeychainId);

    void deleteProducerKey(UUID keychainId, String keyId);

    void removeUserFromKeychain(UUID keychainId, UUID userId);

    void addUserToProducerKeychain(UUID keychainId, UUID userId);
}
