package it.pagopa.interop.keys.service.Impl;

import it.pagopa.interop.common.client.AbstractClient;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.api.KeysApi;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Key;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.ProducerKey;
import it.pagopa.interop.keys.service.IKeysClient;
import it.pagopa.interop.utils.HttpCallExecutor;
import lombok.ToString;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@ToString
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class KeysClient extends AbstractClient implements IKeysClient {

    private final KeysApi keysApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public KeysClient(RestTemplate restTemplate,
                      InteropClientConfigs interopClientConfigs,
                      HttpCallExecutor httpCallExecutor) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getApiv3BaseUrl();
        super.httpCallExecutor = httpCallExecutor;

        this.keysApi = new KeysApi(createApiClient());
    }

    private ApiClient createApiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    public Key getJWKByKid(String kid) {
		        return performOperation(SimpleOperation.of(
                () -> keysApi.getJWKByKidWithHttpInfo(kid),
                response -> response.getBody()
        )).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero del JWK (response non 2xx o body nullo)"
        ));
    }

    public ProducerKey getProducerJWKByKid(String kid) {
        return performOperation(SimpleOperation.of(
                () -> keysApi.getProducerJWKByKidWithHttpInfo(kid),
                response -> response.getBody()
        )).orElseThrow(() -> new IllegalStateException(
                "Errore nel recupero del producer JWK (response non 2xx o body nullo)"
        ));
    }


}
