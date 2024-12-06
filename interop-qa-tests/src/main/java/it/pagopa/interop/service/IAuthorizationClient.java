package it.pagopa.interop.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.service.utils.SettableBearerToken;

import java.util.List;
import java.util.UUID;

public interface IAuthorizationClient extends SettableBearerToken {

    CreatedResource createConsumerClient(String xCorrelationId, ClientSeed clientSeed);
    CreatedResource createApiClient(String xCorrelationId, ClientSeed clientSeed);
    void deleteClient(String xCorrelationId, UUID clientId);
    void getEncodedClientKeyById(String xCorrelationId, UUID clientId, String keyId);
    void deleteClientKeyById(String xCorrelationId, UUID clientId, String keyId);
    void removeUserFromClient(String xCorrelationId, UUID clientId, UUID userId);
    List<CompactUser> getClientUsers(String xCorrelationId, UUID clientId);
    PublicKey getClientKeyById(String xCorrelationId, UUID clientId, String keyId);
    void createKeys(String xCorrelationId, UUID clientId, List<KeySeed> keySeed);
    PublicKeys getClientKeys(String xCorrelationId, UUID clientId, List<UUID> userIds);
    void addClientPurpose(String xCorrelationId, UUID clientId, PurposeAdditionDetailsSeed purposeAdditionDetailsSeed);
    CompactClients getClients(String xCorrelationId, Integer offset, Integer limit, String q, List<UUID> userIds, ClientKind kind);
    Client getClient(String xCorrelationId, UUID clientId);
    CreatedResource addUsersToClient(String xCorrelationId, UUID clientId, InlineObject2 inlineObject2);

}
