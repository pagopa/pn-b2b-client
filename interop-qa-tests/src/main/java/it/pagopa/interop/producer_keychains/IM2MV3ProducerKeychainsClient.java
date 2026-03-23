package it.pagopa.interop.producer_keychains;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.*;

import java.util.UUID;

public interface IM2MV3ProducerKeychainsClient extends Authenticable, SettableHttpCallExecutor {
    ProducerKeychain createProducerKeychain(ProducerKeychainSeed producerKeychainSeed);
    void deleteProducerKeychain(UUID producerKeychainId);
    ProducerKeychain getProducerKeychains(UUID producerKeychainId);
    ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed);
    void deleteProducerKeychainKeyByKid(UUID keychainId, String keyId);
    ProducerKey getProducerKey(String kid);
    void createProducerKeychainUserAssociation(UUID producerKeychainId, LinkUser linkUser);
    Users getProducerKeychainUsers(UUID producerKeychainId, Integer limit, Integer offset);
    void deleteProducerKeychainUserAssociationById(UUID keychainId, UUID keyId);
}