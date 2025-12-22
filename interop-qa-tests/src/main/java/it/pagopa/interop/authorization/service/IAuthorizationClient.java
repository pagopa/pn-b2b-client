package it.pagopa.interop.authorization.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.Client;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface IAuthorizationClient extends SettableBearerToken {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Users {
        private List<UUID> userIds = new ArrayList<>();

        public Users addUserId(UUID userIdsItem) {
            this.userIds.add(userIdsItem);
            return this;
        }
    }


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
    CreatedResource addUsersToClient(UUID clientId, Users users);
    Client editClientAdmin(UUID clientId, ClientAdminConfig adminConfig);
    void deleteClientAdmin(UUID clientId, UUID adminId);
}
