package it.pagopa.interop.service.impl;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.service.IProducerClient;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class ProducerClientImpl implements IProducerClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String bearerToken;

    public ProducerClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.basePath = "basePath";
        this.bearerToken = "apiBearerToken";
        this.eservicesApi = new EservicesApi(createApiClient(bearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void getProducerEServiceDescriptor(String xCorrelationId, UUID eserviceId, UUID descriptorId) {
        eservicesApi.getProducerEServiceDescriptor(xCorrelationId, eserviceId, descriptorId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }
}
