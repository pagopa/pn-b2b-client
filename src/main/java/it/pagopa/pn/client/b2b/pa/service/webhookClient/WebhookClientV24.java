package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.EventsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api.StreamsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;

@Component
@Slf4j
public class WebhookClientV24 extends AbstractWebhookClient {
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    public WebhookClientV24(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
                            @Value("${pn.external.base-url}") String devBasePath,
                            @Value("${pn.external.api-key}") String apiKeyMvp1,
                            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                            @Value("${pn.external.api-key-GA}") String apiKeyGa,
                            @Value("${pn.interop.enable}") String enableInterop) {
        super(restTemplate, interopTokenSingleton, devBasePath, apiKeyMvp1, apiKeyMvp2, apiKeyGa, enableInterop);
        this.eventsApi = new EventsApi(newApiClientV25(restTemplate, devBasePath, apiKeyMvp1, super.getBearerTokenInterop(), enableInterop));
        this.streamsApi = new StreamsApi(newApiClientV25(restTemplate, devBasePath, apiKeyMvp1, super.getBearerTokenInterop(), enableInterop));
    }

    @Override
    public void setApiKey(String apiKey) {
        this.eventsApi.setApiClient(newApiClientV25(super.getRestTemplate(), super.getDevBasePath(), apiKey, super.getBearerTokenInterop(), super.getEnableInterop()));
        this.streamsApi.setApiClient(newApiClientV25(super.getRestTemplate(), super.getDevBasePath(), apiKey, super.getBearerTokenInterop(), super.getEnableInterop()));
    }

    public StreamMetadataResponseV24 createEventStream(StreamCreationRequestV24 streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.createEventStreamV24(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApi.removeEventStreamV24(streamId);
    }

    public StreamMetadataResponseV24 retrieveEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.retrieveEventStreamV24(streamId);
    }

    public List<StreamListElement> listEventStreams() {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.listEventStreamsV24();
    }

    public StreamMetadataResponseV24 updateEventStream(UUID streamId, StreamRequestV24 streamRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.updateEventStreamV24(streamId, streamRequest);
    }

    public StreamMetadataResponseV24 disableEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.disableEventStreamV24(streamId);
    }

    public List<ProgressResponseElementV24> consumeEventStream(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV24(streamId, lastEventId);
    }

    public ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV24WithHttpInfo(streamId, lastEventId);
    }

    public void refreshAndSetTokenInteropClient() {
        if (ENEBLED_INTEROP.equalsIgnoreCase(super.getEnableInterop())) {
            String tokenInterop = super.getInteropTokenSingleton().getTokenInterop();
            if (!tokenInterop.equals(super.getBearerTokenInterop())) {
                log.info("webhookClient call interopTokenSingleton");
                super.setBearerTokenInterop(tokenInterop);
                this.eventsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + super.getBearerTokenInterop());
                this.streamsApi.getApiClient().addDefaultHeader("Authorization", "Bearer " + super.getBearerTokenInterop());
            }
        }
    }
}
