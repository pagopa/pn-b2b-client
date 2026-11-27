package it.pagopa.interop.producer_keychains.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.ProducerKeychainsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerKeychainAddEService;
import it.pagopa.interop.producer_keychains.IM2MProducerKeychainsClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Primary
public class M2MProducerKeychainsClient extends AbstractClient implements IM2MProducerKeychainsClient {

    private final ProducerKeychainsApi producerKeychainsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MProducerKeychainsClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.producerKeychainsApi = new ProducerKeychainsApi(createApiClient("dummyBearer"));
        super.httpCallExecutor = httpCallExecutor;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.producerKeychainsApi.setApiClient(createApiClient(bearerToken));
    }

    @Override
    public void linkEservice(UUID producerKeychainId, ProducerKeychainAddEService seed) {
        httpCallExecutor.performCall(() -> producerKeychainsApi.addProducerKeychainEService(producerKeychainId, seed));
    }
}
