package it.pagopa.interop.authorization.service.impl;

import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProducerClientImpl implements IProducerClient {
    private final EservicesApi eservicesApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public ProducerClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.eservicesApi = new EservicesApi(createApiClient("dummyBearer"));
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
