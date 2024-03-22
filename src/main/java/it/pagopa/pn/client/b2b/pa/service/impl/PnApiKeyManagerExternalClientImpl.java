package it.pagopa.pn.client.b2b.pa.service.impl;


import it.pagopa.pn.client.b2b.pa.service.IPnApiKeyManagerClient;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.ApiClient;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.api.ApiKeysApi;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ApiKeysResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestApiKeyStatus;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.RequestNewApiKey;
import it.pagopa.pn.client.web.generated.openapi.clients.externalApiKeyManager.model.ResponseNewApiKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnApiKeyManagerExternalClientImpl implements IPnApiKeyManagerClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final ApiKeysApi apiKeysApi;
    private final String basePath;
    private final String bearerTokenCom1;
    private final String bearerTokenCom2;
    private final String bearerTokenSON;
    private final String bearerTokenROOT;
    private final String bearerTokenGA;


    private final String userAgent;
    private ApiKeyType apiKeySetted = SettableApiKey.ApiKeyType.MVP_1;

    public PnApiKeyManagerExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.webapi.external.base-url}") String basePath,
            @Value("${pn.external.bearer-token-pa-1}") String bearerTokenCom1,
            @Value("${pn.external.bearer-token-pa-2}") String bearerTokenCom2,
            @Value("${pn.external.bearer-token-pa-SON}") String bearerTokenSON,
            @Value("${pn.external.bearer-token-pa-ROOT}") String bearerTokenROOT,
            @Value("${pn.external.bearer-token-pa-GA}") String bearerTokenGA,
            @Value("${pn.webapi.external.user-agent}")String userAgent
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.basePath = basePath;

        this.bearerTokenCom1 = bearerTokenCom1;
        this.bearerTokenCom2 = bearerTokenCom2;
        this.bearerTokenSON = bearerTokenSON;
        this.bearerTokenROOT = bearerTokenROOT;
        this.bearerTokenGA = bearerTokenGA;

        this.userAgent = userAgent;
        this.apiKeysApi = new ApiKeysApi( newApiClient( restTemplate, basePath, bearerTokenCom1, userAgent) );
    }

    public void setApiKey(String bearerToken) {
        this.apiKeysApi.setApiClient(newApiClient( restTemplate, basePath, bearerToken, userAgent));
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


    //TODO: indagare l'utilizzo di questo metodo
    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        return switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_1) {
                    setApiKey(bearerTokenCom1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                yield true;
            }
            case MVP_2 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(bearerTokenCom2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                yield true;
            }
            case GA -> {
                if (this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(bearerTokenGA);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                yield true;
            }
            case SON -> {
                if (this.apiKeySetted != ApiKeyType.SON) {
                    setApiKey(bearerTokenSON);
                    this.apiKeySetted = ApiKeyType.SON;
                }
                yield true;
            }
            case ROOT -> {
                if (this.apiKeySetted != ApiKeyType.ROOT) {
                    setApiKey(bearerTokenROOT);
                    this.apiKeySetted = ApiKeyType.ROOT;
                }
                yield true;
            }
            default -> false;
        };
    }



    @Override
    public ApiKeyType getApiKeySetted() {return this.apiKeySetted; }
}
