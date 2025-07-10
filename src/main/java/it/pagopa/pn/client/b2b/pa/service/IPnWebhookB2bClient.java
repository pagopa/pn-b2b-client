package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;


public interface IPnWebhookB2bClient extends SettableApiKey {
    it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamMetadataResponse createEventStream(it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamCreationRequest streamCreationRequest);

    void deleteEventStream(UUID streamId);

    it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamMetadataResponse retrieveEventStream(UUID streamId);

    List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamListElement> listEventStreams();

    it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamMetadataResponse updateEventStream(UUID streamId, it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.StreamCreationRequest streamCreationRequest);

    List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.ProgressResponseElement> consumeEventStream(UUID streamId, String lastEventId);

    ResponseEntity<List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.ProgressResponseElement>> consumeEventStreamHttp(UUID streamId, String lastEventId);

    //V23
    StreamMetadataResponseV23 createEventStreamV23(StreamCreationRequestV23 streamCreationRequest);

    void deleteEventStreamV23(UUID streamId);

    StreamMetadataResponseV23 retrieveEventStreamV23(UUID streamId);

    List<StreamListElement> listEventStreamsV23();

    StreamMetadataResponseV23 updateEventStreamV23(UUID streamId, StreamRequestV23 streamRequest);

    StreamMetadataResponseV23 disableEventStreamV23(UUID streamId);

    List<ProgressResponseElementV23> consumeEventStreamV23(UUID streamId, String lastEventId);

    ResponseEntity<List<ProgressResponseElementV23>> consumeEventStreamHttpV23(UUID streamId, String lastEventId);

    //V24
    StreamMetadataResponseV24 createEventStreamV24(StreamCreationRequestV24 streamCreationRequestV24) throws RestClientException;

    StreamMetadataResponseV24 disableEventStreamV24(UUID streamId) throws RestClientException;

    List<StreamListElement> listEventStreamsV24() throws RestClientException;

    void deleteEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 retrieveEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 updateEventStreamV24(UUID streamId, StreamRequestV24 streamRequestV24) throws RestClientException;

    List<ProgressResponseElementV24> consumeEventStreamV24(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttpV24(UUID streamId, String lastEventId) throws RestClientException;

    //V25
    StreamMetadataResponseV25 createEventStreamV25(StreamCreationRequestV25 streamCreationRequestV25) throws RestClientException;

    StreamMetadataResponseV25 disableEventStreamV25(UUID streamId) throws RestClientException;

    List<StreamListElement> listEventStreamsV25() throws RestClientException;

    void deleteEventStreamV25(UUID streamId) throws RestClientException;

    StreamMetadataResponseV25 retrieveEventStreamV25(UUID streamId) throws RestClientException;

    StreamMetadataResponseV25 updateEventStreamV25(UUID streamId, StreamRequestV25 streamRequestV25) throws RestClientException;

    List<ProgressResponseElementV25> consumeEventStreamV25(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV25>> consumeEventStreamHttpV25(UUID streamId, String lastEventId) throws RestClientException;

    //V26
    StreamMetadataResponseV26 createEventStreamV26(StreamCreationRequestV26 streamCreationRequestV26) throws RestClientException;

    StreamMetadataResponseV26 disableEventStreamV26(UUID streamId) throws RestClientException;

    List<StreamListElement> listEventStreamsV26() throws RestClientException;

    void deleteEventStreamV26(UUID streamId) throws RestClientException;

    StreamMetadataResponseV26 retrieveEventStreamV26(UUID streamId) throws RestClientException;

    StreamMetadataResponseV26 updateEventStreamV26(UUID streamId, StreamRequestV26 streamRequestV26) throws RestClientException;

    List<ProgressResponseElementV26> consumeEventStreamV26(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV26>> consumeEventStreamHttpV26(UUID streamId, String lastEventId) throws RestClientException;

    //V27
    StreamMetadataResponseV27 createEventStreamV27(StreamCreationRequestV27 streamCreationRequestV27) throws RestClientException;

    StreamMetadataResponseV27 disableEventStreamV27(UUID streamId) throws RestClientException;

    List<StreamListElement> listEventStreamsV27() throws RestClientException;

    void deleteEventStreamV27(UUID streamId) throws RestClientException;

    StreamMetadataResponseV27 retrieveEventStreamV27(UUID streamId) throws RestClientException;

    StreamMetadataResponseV27 updateEventStreamV27(UUID streamId, StreamRequestV27 streamRequestV27) throws RestClientException;

    List<ProgressResponseElementV27> consumeEventStreamV27(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV27>> consumeEventStreamHttpV27(UUID streamId, String lastEventId) throws RestClientException;

    //V28
    StreamMetadataResponseV28 createEventStreamV28(StreamCreationRequestV28 streamCreationRequestV28) throws RestClientException;

    StreamMetadataResponseV28 disableEventStreamV28(UUID streamId) throws RestClientException;

    List<StreamListElement> listEventStreamsV28() throws RestClientException;

    void deleteEventStreamV28(UUID streamId) throws RestClientException;

    StreamMetadataResponseV28 retrieveEventStreamV28(UUID streamId) throws RestClientException;

    StreamMetadataResponseV28 updateEventStreamV28(UUID streamId, StreamRequestV28 streamRequestV28) throws RestClientException;

    List<ProgressResponseElementV28> consumeEventStreamV28(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV28>> consumeEventStreamHttpV28(UUID streamId, String lastEventId) throws RestClientException;


}