package it.pagopa.interop.producerkeychain;

import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;

import java.util.List;
import java.util.UUID;

public interface ProducerKeychainClient extends IClient<ProducerKeychain, UUID>, SettableHttpCallExecutor {
    UUID create(ProducerKeychainSeed seed);

    void linkEService(UUID producerKeychainId, EServiceAdditionDetailsSeed seed);

    void createProducerKey(UUID keychainId, KeySeed keySeed);

    List<String> getProducerKeysIds(UUID producerKeychainId);

    void deleteProducerKey(UUID keychainId, String keyId);

    void removeUserFromKeychain(UUID keychainId, UUID userId);

    void addUserToProducerKeychain(UUID keychainId, UUID userId);

    CreatedResource createProducerKeychain(ProducerKeychainSeed producerKeychainSeed);

    ProducerKeychain getProducerKeychain(UUID producerKeychainId);

    void createProducerKeychainKey(UUID producerKeychainId, KeySeed keySeed);
}
