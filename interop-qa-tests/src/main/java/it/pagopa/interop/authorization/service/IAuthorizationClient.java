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
    CreatedResource addUsersToClient(UUID clientId, InlineObject4 inlineObject);

    // QA-7236 TODO 07/05/2025: da adeguare una volta che sarà stata rilasciata l'API in oggetto
    //  nel nome, nel parametro passato e nel risultato restituito
    void editClientAdmin(UUID clientId, Object body);

    // QA-7236 TODO 08/05/2025: da adeguare una volta che sarà stata rilasciata l'API in oggetto
    void deleteClientAdmin(UUID clientId, UUID adminId);
}
