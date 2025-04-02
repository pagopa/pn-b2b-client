package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v27.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v27.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v27.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton.ENEBLED_INTEROP;

@Slf4j
@Component
public class WebhookClientV27 extends AbstractWebhookClient {
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    @Autowired
    public WebhookClientV27(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
                            @Value("${pn.external.base-url}") String devBasePath,
                            @Value("${pn.external.api-key}") String apiKeyMvp1,
                            @Value("${pn.external.api-key-2}") String apiKeyMvp2,
                            @Value("${pn.external.api-key-GA}") String apiKeyGa,
                            @Value("${pn.interop.enable}") String enableInterop) {
        super(restTemplate, interopTokenSingleton, devBasePath, apiKeyMvp1, apiKeyMvp2, apiKeyGa, enableInterop);
        this.eventsApi = new EventsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, super.getBearerTokenInterop(), enableInterop));
        this.streamsApi = new StreamsApi(newApiClient(restTemplate, devBasePath, apiKeyMvp1, super.getBearerTokenInterop(), enableInterop));
    }

    @Override
    public void setApiKey(String apiKey) {
        this.eventsApi.setApiClient(newApiClient(super.getRestTemplate(), super.getDevBasePath(), apiKey, super.getBearerTokenInterop(), super.getEnableInterop()));
        this.streamsApi.setApiClient(newApiClient(super.getRestTemplate(), super.getDevBasePath(), apiKey, super.getBearerTokenInterop(), super.getEnableInterop()));
    }

    public StreamMetadataResponseV27 createEventStream(StreamCreationRequestV27 streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.createEventStreamV27(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApi.removeEventStreamV27(streamId);
    }

    public StreamMetadataResponseV27 retrieveEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.retrieveEventStreamV27(streamId);
    }

    public List<StreamListElement> listEventStreams() {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.listEventStreamsV27();
    }

    public StreamMetadataResponseV27 updateEventStream(UUID streamId, StreamRequestV27 streamRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.updateEventStreamV27(streamId, streamRequest);
    }

    public StreamMetadataResponseV27 disableEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.disableEventStreamV27(streamId);
    }

    public List<ProgressResponseElementV27> consumeEventStream(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV27(streamId, lastEventId);
    }

    public ResponseEntity<List<ProgressResponseElementV27>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamV27WithHttpInfo(streamId, lastEventId);
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
