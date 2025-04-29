package it.pagopa.interop.authorization.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;

import java.util.List;
import java.util.UUID;

public interface IAuthorizationClient extends SettableBearerToken {

    CreatedResource createConsumerClient(ClientSeed clientSeed);
    CreatedResource createApiClient(ClientSeed clientSeed);
    void deleteClient(UUID clientId);
    void getEncodedClientKeyById(UUID clientId, String keyId);
    void deleteClientKeyById(UUID clientId, String keyId);
    void removeUserFromClient(UUID clientId, UUID userId);
    List<CompactUser> getClientUsers(UUID clientId);
    PublicKey getClientKeyById(UUID clientId, String keyId);
    void createKeys(UUID clientId, List<KeySeed> keySeed);
    PublicKeys getClientKeys(UUID clientId, Integer offset, Integer limit, List<UUID> userIds);
    void addClientPurpose(UUID clientId, PurposeAdditionDetailsSeed purposeAdditionDetailsSeed);
    CompactClients getClients(Integer offset, Integer limit, String q, List<UUID> userIds, ClientKind kind);
    Client getClient(UUID clientId);
    void removeClientPurpose(UUID clientId, UUID purposeId);
    CreatedResource addUsersToClient(UUID clientId, InlineObject3 inlineObject3);

}
