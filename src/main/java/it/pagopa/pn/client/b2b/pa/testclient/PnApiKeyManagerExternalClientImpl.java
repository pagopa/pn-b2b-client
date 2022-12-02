package it.pagopa.pn.client.b2b.pa.testclient;


import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.api.ApiKeysApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ApiKeysResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestApiKeyStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestNewApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ResponseNewApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class PnApiKeyManagerExternalClientImpl implements IPnApiKeyManagerClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final ApiKeysApi apiKeysApi;
    private final String basePath;
    private final String bearerToken;
    private final String userAgent;
    private SettableApiKey.ApiKeyType apiKeySetted = SettableApiKey.ApiKeyType.MVP_1;

    public PnApiKeyManagerExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.external.bearer-token-pa-1}") String bearerToken,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.bearerToken = bearerToken;
        this.userAgent = userAgent;
        this.apiKeysApi = new ApiKeysApi( newApiClient( restTemplate, basePath, bearerToken,userAgent) );
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String bearerToken,String userAgent ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("user-agent",userAgent);
        newApiClient.setBearerToken(bearerToken);
        return newApiClient;
    }


    public void changeStatusApiKey(String id, RequestApiKeyStatus requestApiKeyStatus) throws RestClientException {
        apiKeysApi.changeStatusApiKey(id, requestApiKeyStatus);
    }


    public void deleteApiKeys(String id) throws RestClientException {
        apiKeysApi.deleteApiKeys(id);
    }


    public ApiKeysResponse getApiKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException {
        return apiKeysApi.getApiKeys(limit, lastKey, lastUpdate, showVirtualKey);
    }


    public ResponseNewApiKey newApiKey(RequestNewApiKey requestNewApiKey) throws RestClientException {
        return apiKeysApi.newApiKey(requestNewApiKey);
    }



}
