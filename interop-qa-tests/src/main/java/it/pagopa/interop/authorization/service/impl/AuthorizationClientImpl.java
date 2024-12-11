package it.pagopa.interop.authorization.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.web.client.RestTemplate;

import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Client;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactClients;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject2;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionResource;

public class AuthorizationClientImpl implements IAuthorizationClient {
    private final ClientsApi clientsApi;
    private final PurposesApi purposesApi;
    private final RestTemplate restTemplate;
    private final InteropClientConfigs interopClientConfigs;
    private final String basePath;
    private final String bearerToken;
    private SettableBearerToken.BearerTokenType bearerTokenSetted;

    public AuthorizationClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "apiBearerToken";
        ApiClient apiClient = createApiClient(bearerToken);
        this.clientsApi = new ClientsApi(apiClient);
        this.purposesApi = new PurposesApi(apiClient);
        this.bearerTokenSetted = SettableBearerToken.BearerTokenType.CONSUMER;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedResource createConsumerClient(String xCorrelationId, ClientSeed clientSeed) {
        return clientsApi.createConsumerClient(xCorrelationId, clientSeed);
    }

    @Override
    public CreatedResource createApiClient(String xCorrelationId, ClientSeed clientSeed) {
        return clientsApi.createApiClient(xCorrelationId, clientSeed);
    }

    @Override
    public void deleteClient(String xCorrelationId, UUID clientId) {
        clientsApi.deleteClient(xCorrelationId, clientId);
    }

    @Override
    public void getEncodedClientKeyById(String xCorrelationId, UUID clientId, String keyId) {
        clientsApi.getEncodedClientKeyById(xCorrelationId, clientId, keyId);
    }

    @Override
    public void deleteClientKeyById(String xCorrelationId, UUID clientId, String keyId) {
        clientsApi.deleteClientKeyById(xCorrelationId, clientId, keyId);
    }

    @Override
    public void removeUserFromClient(String xCorrelationId, UUID clientId, UUID userId) {
        clientsApi.removeUserFromClient(xCorrelationId, clientId, userId);
    }

    @Override
    public List<CompactUser> getClientUsers(String xCorrelationId, UUID clientId) {
        return clientsApi.getClientUsers(xCorrelationId, clientId);
    }

    @Override
    public PublicKey getClientKeyById(String xCorrelationId, UUID clientId, String keyId) {
        return clientsApi.getClientKeyById(xCorrelationId, clientId, keyId);
    }

    @Override
    public void createKeys(String xCorrelationId, UUID clientId, List<KeySeed> keySeed) {
        clientsApi.createKeys(xCorrelationId, clientId, keySeed);
    }

    @Override
    public PublicKeys getClientKeys(String xCorrelationId, UUID clientId, List<UUID> userIds) {
        return clientsApi.getClientKeys(xCorrelationId, clientId, userIds);
    }

    @Override
    public void addClientPurpose(String xCorrelationId, UUID clientId,
            PurposeAdditionDetailsSeed purposeAdditionDetailsSeed) {
        clientsApi.addClientPurpose(xCorrelationId, clientId, purposeAdditionDetailsSeed);
    }

    @Override
    public PurposeVersionResource archivePurposeVersion(String xCorrelationId, UUID purposeId, UUID versionId) {
        return purposesApi.archivePurposeVersion(xCorrelationId, purposeId, versionId);
    }

    @Override
    public Purpose getPurpose(String xCorrelationId, UUID purposeId) {
        return purposesApi.getPurpose(xCorrelationId, purposeId);
    }

    @Override
    public CompactClients getClients(String xCorrelationId, Integer offset, Integer limit, String q, List<UUID> userIds,
            ClientKind kind) {
        return clientsApi.getClients(xCorrelationId, offset, limit, q, userIds, kind);
    }

    @Override
    public Client getClient(String xCorrelationId, UUID clientId) {
        return clientsApi.getClient(xCorrelationId, clientId);
    }

    @Override
    public void removeClientPurpose(String xCorrelationId, UUID clientId, UUID purposeId) {
        clientsApi.removeClientPurpose(xCorrelationId, clientId, purposeId);
    }

    @Override
    public CreatedResource addUsersToClient(String xCorrelationId, UUID clientId, InlineObject2 inlineObject2) {
        return clientsApi.addUsersToClient(xCorrelationId, clientId, inlineObject2);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.clientsApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public SettableBearerToken.BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }

}
