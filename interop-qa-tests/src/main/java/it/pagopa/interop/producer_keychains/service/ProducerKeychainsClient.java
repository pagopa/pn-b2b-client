package it.pagopa.interop.producer_keychains.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerKeychainApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import it.pagopa.interop.producer_keychains.IProducerKeychainsClient;
import it.pagopa.interop.utils.HttpCallExecutor;

import java.util.UUID;

import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProducerKeychainsClient extends AbstractClient implements IProducerKeychainsClient {

    private final ProducerKeychainApi producerKeychainApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public ProducerKeychainsClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        super.httpCallExecutor = httpCallExecutor;

        this.producerKeychainApi = new ProducerKeychainApi(createProducerKeychainApiClient("dummyBearer"));
    }

    private ApiClient createProducerKeychainApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    public CreatedResource createProducerKeychain(ProducerKeychainSeed producerKeychainSeed) {
        return performOperation(SimpleOperation.of(() -> producerKeychainApi.createProducerKeychain(producerKeychainSeed), res -> res)).orElseThrow(() -> new IllegalStateException("Errore nella creazione del producer keychain (response non 2xx o body nullo)"));
    }

    public ProducerKeychain getProducerKeychain(UUID producerKeychainId) {
        return performOperation(SimpleOperation.of(() -> producerKeychainApi.getProducerKeychain(producerKeychainId), res -> res)).orElseThrow(() -> new IllegalStateException("Errore nel recupero del producer keychain (response non 2xx o body nullo)"));
    }

    public void createProducerKeychainKey(UUID producerKeychainId, KeySeed keySeed) {
        performOperation(() -> producerKeychainApi.createProducerKeyWithHttpInfo(producerKeychainId, keySeed));
    }

    public void setBearerToken(String bearerToken) {
        this.producerKeychainApi.setApiClient(createProducerKeychainApiClient(bearerToken));
    }
}