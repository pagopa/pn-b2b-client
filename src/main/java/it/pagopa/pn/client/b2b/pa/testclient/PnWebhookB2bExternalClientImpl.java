package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.pa.impl.PnPaB2bExternalClientImpl;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.ApiClient;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PnWebhookB2bExternalClientImpl implements IPnWebhookB2bClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private ApiKeyType apiKeySetted = ApiKeyType.MVP_1;
    private final String devBasePath;
    private String bearerTokenInterop;

    private final String interopBaseUrl;

    private final String tokenOauth2Path;

    private final String clientAssertion;

    private final String interopClientId;


    private final String enableInterop;

    public PnWebhookB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.external.base-url}") String devBasePath,
            @Value("${pn.external.api-key}") String apiKeyMvp1,
            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
            @Value("${pn.external.api-key-GA}") String apiKeyGa,
            @Value("${pn.interop.base-url}") String interopBaseUrl,
            @Value("${pn.interop.token-oauth2.path}") String tokenOauth2Path,
            @Value("${pn.interop.token-oauth2.client-assertion}") String clientAssertion,
            @Value("${interop.clientId}") String interopClientId,

            @Value("${pn.interop.enable}") String enableInterop
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;

        this.enableInterop = enableInterop;
        this.interopBaseUrl = interopBaseUrl;
        this.tokenOauth2Path = tokenOauth2Path;
        this.clientAssertion = clientAssertion;
        this.interopClientId = interopClientId;

        if ("true".equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = getBearerToken();
        }
        this.devBasePath = devBasePath;

        this.eventsApi = new EventsApi( newApiClient( restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
        this.streamsApi = new StreamsApi( newApiClient( restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop,enableInterop) );
    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        if ("true".equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    public String getBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("client_assertion", clientAssertion);
        map.add("client_id", interopClientId);
        map.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<PnPaB2bExternalClientImpl.InteropResponse> response = this.restTemplate.postForEntity( interopBaseUrl + tokenOauth2Path, request , PnPaB2bExternalClientImpl.InteropResponse.class );

        return (response.getStatusCode().is2xxSuccessful() ? response.getBody().getAccessToken() : null);

    }

    public StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest){
        return this.streamsApi.createEventStream(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId){
        this.streamsApi.removeEventStream(streamId);
    }

    public StreamMetadataResponse getEventStream(UUID streamId){
        return this.streamsApi.retrieveEventStream(streamId);
    }

    public List<StreamListElement> listEventStreams(){
        return this.streamsApi.listEventStreams();
    }

    public StreamMetadataResponse updateEventStream(UUID streamId, StreamCreationRequest streamCreationRequest){
        return this.streamsApi.updateEventStream(streamId,streamCreationRequest);
    }

    public List<ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId){
        return this.eventsApi.consumeEventStream(streamId,lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        return this.eventsApi.consumeEventStreamWithHttpInfo(streamId,lastEventId);
    }


    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch(apiKey){
            case MVP_1:
                if(this.apiKeySetted != ApiKeyType.MVP_1){
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                beenSet = true;
                break;
            case MVP_2:
                if(this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                beenSet = true;
                break;
            case GA:
                if(this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                beenSet = true;
                break;
        }
        return beenSet;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeySetted;
    }

    public void setApiKey(String apiKey){
        this.eventsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop,enableInterop));
        this.streamsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop,enableInterop));
    }
}
