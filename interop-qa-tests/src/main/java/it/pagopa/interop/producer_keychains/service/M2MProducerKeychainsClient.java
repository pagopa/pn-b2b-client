package it.pagopa.interop.producer_keychains.service;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.KeysApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.ProducerKeychainsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.LinkUser;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.producer_keychains.IM2MProducerKeychainsClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class M2MProducerKeychainsClient extends AbstractClient implements IM2MProducerKeychainsClient {

    private final ProducerKeychainsApi producerKeychainsApi;
    private final KeysApi keysApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MProducerKeychainsClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getApiv3BaseUrl();
        super.httpCallExecutor = httpCallExecutor;

        this.producerKeychainsApi = new ProducerKeychainsApi(createProducerKeychainsApiClient());
        this.keysApi = new KeysApi(createKeysApiClient());
    }

    private ApiClient createProducerKeychainsApiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    private ApiClient createKeysApiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    public ProducerKey createProducerKeychainKey(UUID keychainId, KeySeed keySeed) {
        return performOperation(() -> producerKeychainsApi.createProducerKeychainKeyWithHttpInfo(keychainId, keySeed)).orElseThrow(() -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)"));
    }

    public void deleteProducerKeychainKeyByKid(UUID keychainId, String kid) {
        performOperation(() -> producerKeychainsApi.deleteProducerKeychainKeyByIdWithHttpInfo(keychainId, kid)).orElseThrow(() -> new IllegalStateException("Errore nella cancellazione della chiave del producer keychain (response non 2xx)"));
    }

    public void createProducerKeychainUserAssociation(UUID producerKeychainId, LinkUser linkUser) {
        performOperation(() -> producerKeychainsApi.addProducerKeychainUserWithHttpInfo(producerKeychainId, linkUser)).orElseThrow(() -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)"));
    }

    public ProducerKey getProducerKey(String kid) {
        return performOperation(
                () -> keysApi.getProducerJWKByKidWithHttpInfo(kid)
        ).orElseThrow(
                () -> new IllegalStateException("Errore nella creazione della chiave del producer keychain (response non 2xx o body nullo)")
        );
    }
}
