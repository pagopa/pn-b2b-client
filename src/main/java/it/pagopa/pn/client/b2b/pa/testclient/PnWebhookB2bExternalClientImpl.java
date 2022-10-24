package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.ApiClient;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class PnWebhookB2bExternalClientImpl implements IPnWebhookB2bClient {

    private final ApplicationContext ctx;
    private final RestTemplate restTemplate;
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    private final String devApiKey;
    private final String devBasePath;


    public PnWebhookB2bExternalClientImpl(
            ApplicationContext ctx,
            RestTemplate restTemplate,
            @Value("${pn.external.base-url}") String devBasePath,
            @Value("${pn.external.api-key}") String devApiKey
    ) {
        this.ctx = ctx;
        this.restTemplate = restTemplate;
        this.devApiKey = devApiKey;
        this.devBasePath = devBasePath;

        this.eventsApi = new EventsApi( newApiClient( restTemplate, devBasePath, devApiKey) );
        this.streamsApi = new StreamsApi( newApiClient( restTemplate, devBasePath, devApiKey) );
    }


    private static ApiClient newApiClient(RestTemplate restTemplate, String basePath, String apikey ) {
        ApiClient newApiClient = new ApiClient( restTemplate );
        newApiClient.setBasePath( basePath );
        newApiClient.addDefaultHeader("x-api-key", apikey );
        return newApiClient;
    }

    public StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest){
        return this.streamsApi.createEventStream(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId){
        this.streamsApi.deleteEventStream(streamId);
    }

    public StreamMetadataResponse getEventStream(UUID streamId){
        return this.streamsApi.getEventStream(streamId);
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


}
