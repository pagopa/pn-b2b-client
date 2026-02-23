package it.pagopa.interop.agreement.service.impl;

import it.pagopa.interop.agreement.service.IM2MClientsClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@EqualsAndHashCode
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MClientsClientImpl implements IM2MClientsClient {
    private final ClientsApi clientsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MClientsClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.clientsApi = new ClientsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);

        apiClient.setBearerToken(bearerToken);

        return apiClient;
    }

    @Override
    public Purposes getClientPurposes(UUID clientId) {
        return this.getClientPurposes(clientId, 0, 30);
    }

    @Override
    public Purposes getClientPurposes(UUID clientId, int offset, int limit) {
        return clientsApi.getClientPurposes(clientId, offset, limit, null, null);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.clientsApi.setApiClient(createApiClient(bearerToken));
    }
}