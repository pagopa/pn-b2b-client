package it.pagopa.interop.authorization.service.impl;

import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Client;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject3;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthorizationClientImpl implements IAuthorizationClient {
    private final ClientsApi clientsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public AuthorizationClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.clientsApi = new ClientsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedResource createConsumerClient(ClientSeed clientSeed) {
        return clientsApi.createConsumerClient(clientSeed);
    }

    @Override
    public CreatedResource createApiClient(ClientSeed clientSeed) {
        return clientsApi.createApiClient(clientSeed);
    }

    @Override
    public void deleteClient(UUID clientId) {
        clientsApi.deleteClient(clientId);
    }

    @Override
    public void getEncodedClientKeyById(UUID clientId, String keyId) {
        clientsApi.getEncodedClientKeyById(clientId, keyId);
    }

    @Override
    public void deleteClientKeyById(UUID clientId, String keyId) {
        clientsApi.deleteClientKeyById(clientId, keyId);
    }

    @Override
    public void removeUserFromClient(UUID clientId, UUID userId) {
        clientsApi.removeUserFromClient(clientId, userId);
    }

    @Override
    public List<CompactUser> getClientUsers(UUID clientId) {
        return clientsApi.getClientUsers(clientId);
    }

    @Override
    public PublicKey getClientKeyById(UUID clientId, String keyId) {
        return clientsApi.getClientKeyById(clientId, keyId);
    }

    @Override
    public void createKeys(UUID clientId, List<KeySeed> keySeed) {
        keySeed.forEach(seed -> clientsApi.createKey(clientId, seed));
    }

    @Override
    public PublicKeys getClientKeys(UUID clientId, Integer offset, Integer limit,  List<UUID> userIds) {
        return clientsApi.getClientKeys(clientId, 0, 50, userIds);
    }

    @Override
    public void addClientPurpose(UUID clientId,
            PurposeAdditionDetailsSeed purposeAdditionDetailsSeed) {
        clientsApi.addClientPurpose(clientId, purposeAdditionDetailsSeed);
    }

    @Override
    public CompactClients getClients(Integer offset, Integer limit, String q, List<UUID> userIds,
            ClientKind kind) {
        return clientsApi.getClients(offset, limit, q, userIds, kind);
    }

    @Override
    public Client getClient(UUID clientId) {
        return clientsApi.getClient(clientId);
    }

    @Override
    public void removeClientPurpose(UUID clientId, UUID purposeId) {
        clientsApi.removeClientPurpose(clientId, purposeId);
    }

    @Override
    public CreatedResource addUsersToClient(UUID clientId, InlineObject3 inlineObject3) {
        return clientsApi.addUsersToClient(clientId, inlineObject3);
    }

    @Override
    public void editClientAdmin(UUID clientId, Object body) {
        // clientsApi.editClientAdmin(body);
    }

    @Override
    public void deleteClientAdmin(UUID clientId, UUID adminId) {
        // clientsApi.deleteClientAdmin(clientId, adminId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.clientsApi.setApiClient(createApiClient(bearerToken));
    }

}
