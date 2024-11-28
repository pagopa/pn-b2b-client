package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.ApiClient;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamCreationRequestV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamRequestV23;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnWebhookB2bExternalClientImpl implements IPnWebhookB2bClient {
    private final RestTemplate restTemplate;
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;
    private final it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2_3.EventsApi eventsApiV23;
    private final it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2_3.StreamsApi streamsApiV23;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.EventsApi eventsApiV25;
    private final it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.StreamsApi streamsApiV25;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private ApiKeyType apiKeySetted;
    private final String devBasePath;
    private String bearerTokenInterop;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;


    public PnWebhookB2bExternalClientImpl(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
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
        this.eventsApi = new EventsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.streamsApi = new StreamsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.eventsApiV23 = new it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2_3.EventsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.streamsApiV23 = new it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2_3.StreamsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop,enableInterop));
        this.eventsApiV25 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.EventsApi(newApiClientV25(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.streamsApiV25 = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.StreamsApi(newApiClientV25(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.apiKeySetted = ApiKeyType.MVP_1;
    }

    //@Scheduled(cron = "* * * * * ?")
    public void refreshAndSetTokenInteropClient() {
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            String tokenInterop = interopTokenSingleton.getTokenInterop();
            if (!tokenInterop.equals(this.bearerTokenInterop)) {
                log.info("webhookClient call interopTokenSingleton");
                this.bearerTokenInterop = tokenInterop;
                this.eventsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
                this.streamsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
                this.eventsApiV23.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
                this.streamsApiV23.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            }
        }
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    private static it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient newApiClientV25(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient newApiClient = new it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (ENEBLED_INTEROP.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    public StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.createEventStream(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApi.removeEventStream(streamId);
    }

    public StreamMetadataResponse getEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.retrieveEventStream(streamId);
    }

    public List<StreamListElement> listEventStreams() {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.listEventStreams();
    }

    public StreamMetadataResponse updateEventStream(UUID streamId, StreamCreationRequest streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.updateEventStream(streamId, streamCreationRequest);
    }

    public List<ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStream(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamWithHttpInfo(streamId, lastEventId);
    }

    //V23
    public StreamMetadataResponseV23 createEventStreamV23(StreamCreationRequestV23 streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV23.createEventStreamV23(streamCreationRequest);
    }

    public void deleteEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApiV23.removeEventStreamV23(streamId);
    }

    public StreamMetadataResponseV23 getEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV23.retrieveEventStreamV23(streamId);
    }

    public List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> listEventStreamsV23() {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV23.listEventStreamsV23();
    }

    public StreamMetadataResponseV23 updateEventStreamV23(UUID streamId, StreamRequestV23 streamRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV23.updateEventStreamV23(streamId, streamRequest);
    }

    public StreamMetadataResponseV23 disableEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV23.disableEventStreamV23(streamId);
    }

    public List<ProgressResponseElementV23> consumeEventStreamV23(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV23.consumeEventStreamV23(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV23>> consumeEventStreamHttpV23(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV23.consumeEventStreamV23WithHttpInfo(streamId, lastEventId);
    }

    //V24
    @Override
    public StreamMetadataResponseV24 createEventStreamV24(StreamCreationRequestV24 streamCreationRequestV24) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.createEventStreamV24(streamCreationRequestV24);
    }

    @Override
    public StreamMetadataResponseV24 disableEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.disableEventStreamV24(streamId);
    }

    @Override
    public List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV24() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.listEventStreamsV24();
    }

    @Override
    public void deleteEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.streamsApiV25.removeEventStreamV24(streamId);
    }

    @Override
    public StreamMetadataResponseV24 retrieveEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.retrieveEventStreamV24(streamId);
    }

    @Override
    public StreamMetadataResponseV24 updateEventStreamV24(UUID streamId, StreamRequestV24 streamRequestV24) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.updateEventStreamV24(streamId, streamRequestV24);
    }

    @Override
    public List<ProgressResponseElementV24> consumeEventStreamV24(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV25.consumeEventStreamV24(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttpV24(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV25.consumeEventStreamV24WithHttpInfo(streamId, lastEventId);
    }

    //V25
    @Override
    public StreamMetadataResponseV25 createEventStreamV25(StreamCreationRequestV25 streamCreationRequestV25) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.createEventStreamV25(streamCreationRequestV25);
    }

    @Override
    public StreamMetadataResponseV25 disableEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.disableEventStreamV25(streamId);
    }

    @Override
    public List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV25() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.listEventStreamsV25();
    }

    @Override
    public void deleteEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        this.streamsApiV25.removeEventStreamV25(streamId);
    }

    @Override
    public StreamMetadataResponseV25 retrieveEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.retrieveEventStreamV25(streamId);
    }

    @Override
    public StreamMetadataResponseV25 updateEventStreamV25(UUID streamId, StreamRequestV25 streamRequestV25) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.streamsApiV25.updateEventStreamV25(streamId, streamRequestV25);
    }

    @Override
    public List<ProgressResponseElementV25> consumeEventStreamV25(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV25.consumeEventStreamV25(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV25>> consumeEventStreamHttpV25(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return this.eventsApiV25.consumeEventStreamV25WithHttpInfo(streamId, lastEventId);
    }

    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeySetted = ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeySetted != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeySetted = ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeySetted != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeySetted = ApiKeyType.GA;
                }
                beenSet = true;
            }
        }
        return beenSet;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeySetted;
    }

    public void setApiKey(String apiKey) {
        this.eventsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.streamsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.eventsApiV23.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.streamsApiV23.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.eventsApiV25.setApiClient(newApiClientV25(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        this.streamsApiV25.setApiClient(newApiClientV25(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
    }
}