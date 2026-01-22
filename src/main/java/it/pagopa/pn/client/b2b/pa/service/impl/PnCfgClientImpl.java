package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnCfgClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.api.CfgApi;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.DocumentTypesConfigurations;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.UserConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnCfgClientImpl implements IPnCfgClient {

    private final CfgApi cfgApi;

    public PnCfgClientImpl(RestTemplate restTemplate,
                           @Value("${pn.safeStorage.base-url}") String safeStorageBaseUrl,
                           @Value("${pn.safeStorage.apikey}") String apiKeySafeStorage) {
        cfgApi = new CfgApi(newApiClient(restTemplate, safeStorageBaseUrl, apiKeySafeStorage));
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apiKey) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apiKey);
        return newApiClient;
    }

    @Override
    public UserConfiguration getCurrentClientConfig(String clientId) throws RestClientException {
        return cfgApi.getCurrentClientConfig(clientId);
    }

    @Override
    public DocumentTypesConfigurations getDocumentsConfigs() throws RestClientException {
        return cfgApi.getDocumentsConfigs();
    }
}
