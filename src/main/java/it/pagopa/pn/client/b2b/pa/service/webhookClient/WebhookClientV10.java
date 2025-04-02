package it.pagopa.pn.client.b2b.pa.service.webhookClient;

import it.pagopa.pn.client.b2b.pa.service.utils.InteropTokenSingleton;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2.EventsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.api_v2.StreamsApi;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
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
public class WebhookClientV10 extends AbstractWebhookClient {
    private final EventsApi eventsApi;
    private final StreamsApi streamsApi;

    public WebhookClientV10(RestTemplate restTemplate, InteropTokenSingleton interopTokenSingleton,
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

    public StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest) {
        refreshAndSetTokenInteropClient();
        return this.streamsApi.createEventStream(streamCreationRequest);
    }

    public void deleteEventStream(UUID streamId) {
        refreshAndSetTokenInteropClient();
        this.streamsApi.removeEventStream(streamId);
    }

    public StreamMetadataResponse retrieveEventStream(UUID streamId) {
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

    public ResponseEntity<List<ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId) {
        refreshAndSetTokenInteropClient();
        return this.eventsApi.consumeEventStreamWithHttpInfo(streamId, lastEventId);
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
