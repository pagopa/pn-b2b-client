package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.ApiClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;

@Slf4j
@Getter
public abstract class AbstractWebhookClient implements SettableApiKey {

    private final RestTemplate restTemplate;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String devBasePath;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;

    @Setter
    private String bearerTokenInterop;

    private ApiKeyType apiKeySet;

    public AbstractWebhookClient(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
                                 @Value("${pn.external.base-url}") String devBasePath,
                                 @Value("${pn.external.api-key}") String apiKeyMvp1,
                                 @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                                 @Value("${pn.external.api-key-GA}") String apiKeyGa,
                                 @Value("${pn.interop.enable}") String enableInterop) {
        this.restTemplate = restTemplate;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.enableInterop = enableInterop;
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();
        }
        this.interopTokenSingleton = interopTokenSingleton;
        this.devBasePath = devBasePath;
        this.apiKeySet = ApiKeyType.MVP_1;
    }

    public static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    //TODO: sarebbe carino modificare il POM in modo che il package della V25 segua l'impostazione delle altre versioni,
    // evitando questo duplicato inutile (oltretutto condiviso da V24 e V25, bruttissimo)
    public static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient newApiClientV25(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeySet != ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeySet = ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeySet != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySet = ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeySet != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySet = ApiKeyType.GA;
                }
                beenSet = true;
            }
        }
        return beenSet;
    }

    public ApiKeyType getApiKeySetted() {
        return this.apiKeySet;
    }

    public abstract void setApiKey(String apiKey);
}
