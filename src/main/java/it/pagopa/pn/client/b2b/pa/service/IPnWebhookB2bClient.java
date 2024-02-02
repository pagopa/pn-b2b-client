package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamMetadataResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface IPnWebhookB2bClient extends SettableApiKey {

    StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest);

    void deleteEventStream(UUID streamId);

    StreamMetadataResponse getEventStream(UUID streamId);

    List<StreamListElement> listEventStreams();

    StreamMetadataResponse updateEventStream(UUID streamId, StreamCreationRequest streamCreationRequest);

    List<ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId);

    ResponseEntity<List<ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId);
}
