package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableApiKey;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamCreationRequest;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamListElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.StreamMetadataResponse;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamCreationRequestV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamMetadataResponseV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamRequestV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamCreationRequestV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamMetadataResponseV23;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamRequestV23;
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

    //V23
    StreamMetadataResponseV23 createEventStreamV23(StreamCreationRequestV23 streamCreationRequest);

    void deleteEventStreamV23(UUID streamId);

    StreamMetadataResponseV23 getEventStreamV23(UUID streamId);

    List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.StreamListElement> listEventStreamsV23();

    StreamMetadataResponseV23 updateEventStreamV23(UUID streamId, StreamRequestV23 streamRequest);

    StreamMetadataResponseV23 disableEventStreamV23(UUID streamId);

    List<ProgressResponseElementV23> consumeEventStreamV23(UUID streamId, String lastEventId);

    ResponseEntity<List<ProgressResponseElementV23>> consumeEventStreamHttpV23(UUID streamId, String lastEventId);

    //V24
    StreamMetadataResponseV24 createEventStreamV24(StreamCreationRequestV24 streamCreationRequestV24) throws RestClientException;

    StreamMetadataResponseV24 disableEventStreamV24(UUID streamId) throws RestClientException;

    List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV24() throws RestClientException;

    void deleteEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 retrieveEventStreamV24(UUID streamId) throws RestClientException;

    StreamMetadataResponseV24 updateEventStreamV24(UUID streamId, StreamRequestV24 streamRequestV24) throws RestClientException;

    List<ProgressResponseElementV24> consumeEventStreamV24(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV24>> consumeEventStreamHttpV24(UUID streamId, String lastEventId) throws RestClientException;

    //V25
    StreamMetadataResponseV25 createEventStreamV25(StreamCreationRequestV25 streamCreationRequestV25) throws RestClientException;

    StreamMetadataResponseV25 disableEventStreamV25(UUID streamId) throws RestClientException;

    List<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.StreamListElement> listEventStreamsV25() throws RestClientException;

    void deleteEventStreamV25(UUID streamId) throws RestClientException;

    StreamMetadataResponseV25 retrieveEventStreamV25(UUID streamId) throws RestClientException;

    StreamMetadataResponseV25 updateEventStreamV25(UUID streamId, StreamRequestV25 streamRequestV25) throws RestClientException;

    List<ProgressResponseElementV25> consumeEventStreamV25(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV25>> consumeEventStreamHttpV25(UUID streamId, String lastEventId) throws RestClientException;

    //V26
    StreamMetadataResponseV26 createEventStreamV26(StreamCreationRequestV26 streamCreationRequestV26) throws RestClientException;

    StreamMetadataResponseV26 disableEventStreamV26(UUID streamId) throws RestClientException;

    List<it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.StreamListElement> listEventStreamsV26() throws RestClientException;

    void deleteEventStreamV26(UUID streamId) throws RestClientException;

    StreamMetadataResponseV26 retrieveEventStreamV26(UUID streamId) throws RestClientException;

    StreamMetadataResponseV26 updateEventStreamV26(UUID streamId, StreamRequestV26 streamRequestV26) throws RestClientException;

    List<ProgressResponseElementV26> consumeEventStreamV26(UUID streamId, String lastEventId) throws RestClientException;

    ResponseEntity<List<ProgressResponseElementV26>> consumeEventStreamHttpV26(UUID streamId, String lastEventId) throws RestClientException;


}