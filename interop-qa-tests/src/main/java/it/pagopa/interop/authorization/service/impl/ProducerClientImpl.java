package it.pagopa.interop.authorization.service.impl;

import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class ProducerClientImpl implements IProducerClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final InteropClientConfigs interopClientConfigs;
    private final String basePath;
    private final String bearerToken;

    public ProducerClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.basePath = interopClientConfigs.getBaseUrl();
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
    public ProducerEServiceDescriptor getProducerEServiceDescriptor(String xCorrelationId, UUID eserviceId, UUID descriptorId) {
        return eservicesApi.getProducerEServiceDescriptor(xCorrelationId, eserviceId, descriptorId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.eservicesApi.setApiClient(createApiClient(bearerToken));
    }

}