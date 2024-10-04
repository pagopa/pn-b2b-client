package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

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

    //Versione 2_2
    StreamMetadataResponseV23 createEventStreamV23(StreamCreationRequestV23 streamCreationRequest);

    void deleteEventStreamV23(UUID streamId);

    StreamMetadataResponseV23 getEventStreamV23(UUID streamId);

    List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV23();

    StreamMetadataResponseV23 updateEventStreamV23(UUID streamId, StreamRequestV23 streamRequest);

    StreamMetadataResponseV23 disableEventStreamV23(UUID streamId);

    List<ProgressResponseElementV23> consumeEventStreamV23(UUID streamId, String lastEventId);

    ResponseEntity<List<ProgressResponseElementV23>> consumeEventStreamHttpV23(UUID streamId, String lastEventId);

    //Versione 2.4

    StreamMetadataResponseV24 createEventStreamV24(StreamCreationRequestV24 streamCreationRequestV24) throws RestClientException;

    StreamMetadataResponseV24 disableEventStreamV24(UUID streamId) throws RestClientException;

    List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV24() throws RestClientException;

    void removeEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 retrieveEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 updateEventStreamV24(UUID streamId, StreamRequestV24 streamRequestV24) throws RestClientException;

    List<ProgressResponseElementV24> consumeEventStreamV24(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttpV24(UUID streamId, String lastEventId) throws RestClientException;
}