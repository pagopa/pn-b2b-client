package it.pagopa.interop.service.impl;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.service.IAuthorizationClientCreate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class AuthorizationClientCreateImpl implements IAuthorizationClientCreate {
    private final ClientsApi clientsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String bearerToken;
    private BearerTokenType bearerTokenSetted;

    public AuthorizationClientCreateImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.basePath = "basePath";
        this.bearerToken = "apiBearerToekn";
        this.clientsApi = new ClientsApi(createApiClient(bearerToken));
        this.bearerTokenSetted = BearerTokenType.CONSUMER;
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
    public void setBearerToken(String bearerToken) {
        this.clientsApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return bearerTokenSetted;
    }
}
