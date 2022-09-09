package it.pagopa.pn.client.b2b.pa.impl;

import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.StreamMetadataResponse;

import java.util.List;
import java.util.UUID;

public interface IPnWebhookB2bClient {


    StreamMetadataResponse createEventStream(StreamCreationRequest streamCreationRequest);

    void deleteEventStream(UUID streamId);

    StreamMetadataResponse getEventStream(UUID streamId);

    List<StreamListElement> listEventStreams();

    StreamMetadataResponse updateEventStream(UUID streamId, StreamCreationRequest streamCreationRequest);

    List<ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId);
}
