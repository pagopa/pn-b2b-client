package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.service.IPnWebhookB2bClient;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.ApiClient;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
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

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.INTEROP_ENABLED;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class PnWebhookB2bExternalClientImpl implements IPnWebhookB2bClient {
    private final RestTemplate restTemplate;
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;
    private final String apiKeyMvp1;
    private final String apiKeyMvp2;
    private final String apiKeyGa;
    private final String apiKeySon;
    private ApiKeyType apiKeyInUse;
    private final String devBasePath;
    private String bearerTokenInterop;
    private final String enableInterop;
    private final InteropTokenSingleton interopTokenSingleton;


    public PnWebhookB2bExternalClientImpl(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
                                          @Value("${pn.external.base-url}") String devBasePath,
                                          @Value("${pn.external.api-key}") String apiKeyMvp1,
                                          @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                                          @Value("${pn.external.api-key-GA}") String apiKeyGa,
                                          @Value("${pn.external.api-key-SON}") String apiKeySon,
                                          @Value("${pn.interop.enable}") String enableInterop) {
        this.restTemplate = restTemplate;
        this.apiKeyMvp1 = apiKeyMvp1;
        this.apiKeyMvp2 = apiKeyMvp2;
        this.apiKeyGa = apiKeyGa;
        this.apiKeySon = apiKeySon;
        this.enableInterop = enableInterop;
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            this.bearerTokenInterop = interopTokenSingleton.getTokenInterop();
        }
        this.interopTokenSingleton = interopTokenSingleton;
        this.devBasePath = devBasePath;
        eventsApi = new EventsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        streamsApi = new StreamsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, bearerTokenInterop, enableInterop));
        this.apiKeyInUse = ApiKeyType.MVP_1;
    }

    //@Scheduled(cron = "* * * * * ?")
    public void refreshAndSetTokenInteropClient() {
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            String tokenInterop = interopTokenSingleton.getTokenInterop();
            if (!tokenInterop.equals(this.bearerTokenInterop)) {
                log.info("webhookClient call interopTokenSingleton");
                this.bearerTokenInterop = tokenInterop;
                eventsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
                streamsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + bearerTokenInterop);
            }
        }
    }

    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey, String bearerToken, String enableInterop) {
        ApiClient newApiClient = new ApiClient(restTemplate);
        newApiClient.setBasePath(basePath);
        newApiClient.addDefaultHeader("x-api-key", apikey);
        if (INTEROP_ENABLED.equalsIgnoreCase(enableInterop)) {
            newApiClient.addDefaultHeader("Authorization", "Bearer " + bearerToken);
        }
        return newApiClient;
    }

    public StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStream(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStream(streamId);
    }

    public StreamMetadataResponse retrieveEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStream(streamId);
    }

    public List<StreamListElement> listEventStreams() {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreams();
    }

    public StreamMetadataResponse updateEventStream(UUID streamId, StreamCreationRequest streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStream(streamId, streamCreationRequest);
    }

    public List<ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStream(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamWithHttpInfo(streamId, lastEventId);
    }

    //V23
    public StreamMetadataResponseV23 createEventStreamV23(StreamCreationRequestV23 streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV23(streamCreationRequest);
    }

    public void deleteEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV23(streamId);
    }

    public StreamMetadataResponseV23 retrieveEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV23(streamId);
    }

    public List<StreamListElement> listEventStreamsV23() {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV23();
    }

    public StreamMetadataResponseV23 updateEventStreamV23(UUID streamId, StreamRequestV23 streamRequest) {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV23(streamId, streamRequest);
    }

    public StreamMetadataResponseV23 disableEventStreamV23(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV23(streamId);
    }

    public List<ProgressResponseElementV23> consumeEventStreamV23(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV23(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV23>> consumeEventStreamHttpV23(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV23WithHttpInfo(streamId, lastEventId);
    }

    //V24
    @Override
    public StreamMetadataResponseV24 createEventStreamV24(StreamCreationRequestV24 streamCreationRequestV24) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV24(streamCreationRequestV24);
    }

    @Override
    public StreamMetadataResponseV24 disableEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV24(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV24() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV24();
    }

    @Override
    public void deleteEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV24(streamId);
    }

    @Override
    public StreamMetadataResponseV24 retrieveEventStreamV24(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV24(streamId);
    }

    @Override
    public StreamMetadataResponseV24 updateEventStreamV24(UUID streamId, StreamRequestV24 streamRequestV24) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV24(streamId, streamRequestV24);
    }

    @Override
    public List<ProgressResponseElementV24> consumeEventStreamV24(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV24(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttpV24(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV24WithHttpInfo(streamId, lastEventId);
    }

    //V25
    @Override
    public StreamMetadataResponseV25 createEventStreamV25(StreamCreationRequestV25 streamCreationRequestV25) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV25(streamCreationRequestV25);
    }

    @Override
    public StreamMetadataResponseV25 disableEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV25(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV25() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV25();
    }

    @Override
    public void deleteEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV25(streamId);
    }

    @Override
    public StreamMetadataResponseV25 retrieveEventStreamV25(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV25(streamId);
    }

    @Override
    public StreamMetadataResponseV25 updateEventStreamV25(UUID streamId, StreamRequestV25 streamRequestV25) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV25(streamId, streamRequestV25);
    }

    @Override
    public List<ProgressResponseElementV25> consumeEventStreamV25(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV25(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV25>> consumeEventStreamHttpV25(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV25WithHttpInfo(streamId, lastEventId);
    }

    //V26
    @Override
    public StreamMetadataResponseV26 createEventStreamV26(StreamCreationRequestV26 streamCreationRequestV26) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV26(streamCreationRequestV26);
    }

    @Override
    public StreamMetadataResponseV26 disableEventStreamV26(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV26(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV26() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV26();
    }

    @Override
    public void deleteEventStreamV26(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV26(streamId);
    }

    @Override
    public StreamMetadataResponseV26 retrieveEventStreamV26(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV26(streamId);
    }

    @Override
    public StreamMetadataResponseV26 updateEventStreamV26(UUID streamId, StreamRequestV26 streamRequestV26) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV26(streamId, streamRequestV26);
    }

    @Override
    public List<ProgressResponseElementV26> consumeEventStreamV26(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV26(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV26>> consumeEventStreamHttpV26(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV26WithHttpInfo(streamId, lastEventId);
    }

    //V27
    @Override
    public StreamMetadataResponseV27 createEventStreamV27(StreamCreationRequestV27 streamCreationRequestV27) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV27(streamCreationRequestV27);
    }

    @Override
    public StreamMetadataResponseV27 disableEventStreamV27(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV27(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV27() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV27();
    }

    @Override
    public void deleteEventStreamV27(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV27(streamId);
    }

    @Override
    public StreamMetadataResponseV27 retrieveEventStreamV27(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV27(streamId);
    }

    @Override
    public StreamMetadataResponseV27 updateEventStreamV27(UUID streamId, StreamRequestV27 streamRequestV27) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV27(streamId, streamRequestV27);
    }

    @Override
    public List<ProgressResponseElementV27> consumeEventStreamV27(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV27(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV27>> consumeEventStreamHttpV27(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV27WithHttpInfo(streamId, lastEventId);
    }

    //V28
    @Override
    public StreamMetadataResponseV28 createEventStreamV28(StreamCreationRequestV28 streamCreationRequestV28) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV28(streamCreationRequestV28);
    }

    @Override
    public StreamMetadataResponseV28 disableEventStreamV28(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV28(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV28() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV28();
    }

    @Override
    public void deleteEventStreamV28(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV28(streamId);
    }

    @Override
    public StreamMetadataResponseV28 retrieveEventStreamV28(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV28(streamId);
    }

    @Override
    public StreamMetadataResponseV28 updateEventStreamV28(UUID streamId, StreamRequestV28 streamRequestV28) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV28(streamId, streamRequestV28);
    }

    @Override
    public List<ProgressResponseElementV28> consumeEventStreamV28(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV28(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV28>> consumeEventStreamHttpV28(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV28WithHttpInfo(streamId, lastEventId);
    }

    //V29
    @Override
    public StreamMetadataResponseV29 createEventStreamV29(StreamCreationRequestV29 streamCreationRequestV29) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.createEventStreamV29(streamCreationRequestV29);
    }

    @Override
    public StreamMetadataResponseV29 disableEventStreamV29(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.disableEventStreamV29(streamId);
    }

    @Override
    public List<StreamListElement> listEventStreamsV29() throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.listEventStreamsV29();
    }

    @Override
    public void deleteEventStreamV29(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        streamsApi.removeEventStreamV29(streamId);
    }

    @Override
    public StreamMetadataResponseV29 retrieveEventStreamV29(UUID streamId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.retrieveEventStreamV29(streamId);

    }

    @Override
    public StreamMetadataResponseV29 updateEventStreamV29(UUID streamId, StreamRequestV29 streamRequestV29) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return streamsApi.updateEventStreamV29(streamId, streamRequestV29);
    }

    @Override
    public List<ProgressResponseElementV29> consumeEventStreamV29(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV29(streamId, lastEventId);
    }

    @Override
    public ResponseEntity<List<ProgressResponseElementV29>> consumeEventStreamHttpV29(UUID streamId, String lastEventId) throws RestClientException {
        refreshAndSetTokenInteropClient();
        return eventsApi.consumeEventStreamV29WithHttpInfo(streamId, lastEventId);
    }


    @Override
    public boolean setApiKeys(ApiKeyType apiKey) {
        boolean beenSet = false;
        switch (apiKey) {
            case MVP_1 -> {
                if (this.apiKeyInUse != ApiKeyType.MVP_1) {
                    setApiKey(apiKeyMvp1);
                    this.apiKeyInUse = ApiKeyType.MVP_1;
                }
                beenSet = true;
            }
            case MVP_2 -> {
                if (this.apiKeyInUse != ApiKeyType.MVP_2) {
                    setApiKey(apiKeyMvp2);
                    this.apiKeyInUse = ApiKeyType.MVP_2;
                }
                beenSet = true;
            }
            case GA -> {
                if (this.apiKeyInUse != ApiKeyType.GA) {
                    setApiKey(apiKeyGa);
                    this.apiKeyInUse = ApiKeyType.GA;
                }
                beenSet = true;
            }
            case SON -> {
                if (this.apiKeyInUse != ApiKeyType.SON) {
                    setApiKey(apiKeySon);
                    this.apiKeyInUse = ApiKeyType.SON;
                }
                beenSet = true;
            }
        }
        return beenSet;
    }

    @Override
    public ApiKeyType getApiKeySetted() {
        return this.apiKeyInUse;
    }

    public void setApiKey(String apiKey) {
        eventsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
        streamsApi.setApiClient(newApiClient(restTemplate, devBasePath, apiKey, bearerTokenInterop, enableInterop));
    }
}