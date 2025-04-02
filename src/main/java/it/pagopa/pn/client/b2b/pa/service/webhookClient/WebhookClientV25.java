package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v25.EventsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.api_v25.StreamsApi;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v25.*;
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
public class WebhookClientV25 extends AbstractWebhookClient {
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    public WebhookClientV25(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
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

    public StreamMetadataResponseV25 createEventStream(StreamCreationRequestV25 streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.createEventStreamV25(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApi.removeEventStreamV25(streamId);
    }

    public StreamMetadataResponseV25 retrieveEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.retrieveEventStreamV25(streamId);
    }

    public List<StreamListElement> listEventStreams() {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.listEventStreamsV25();
    }

    public StreamMetadataResponseV25 updateEventStream(UUID streamId, StreamRequestV25 streamRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.updateEventStreamV25(streamId, streamRequest);
    }

    public StreamMetadataResponseV25 disableEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.disableEventStreamV25(streamId);
    }

    public List<ProgressResponseElementV25> consumeEventStream(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV25(streamId, lastEventId);
    }

    public ResponseEntity<List<ProgressResponseElementV25>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV25WithHttpInfo(streamId, lastEventId);
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
