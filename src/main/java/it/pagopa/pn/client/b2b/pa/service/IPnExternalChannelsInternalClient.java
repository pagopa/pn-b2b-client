package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.CourtesyMessageProgressEvent;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesyMailRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.internal.externalchannels.v1.model.DigitalCourtesySmsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnExternalChannelsInternalClient {

    List<CourtesyMessageProgressEvent> getCourtesyShortMessageStatus(String requestIdx, String xPagopaExtchCxId) throws RestClientException;

    List<CourtesyMessageProgressEvent> getDigitalCourtesyMessageStatus(String requestIdx, String xPagopaExtchCxId) throws RestClientException;

    ResponseEntity<Void> sendCourtesyShortMessage(String requestIdx, String xPagopaExtchCxId, DigitalCourtesySmsRequest digitalCourtesySmsRequest) throws RestClientException;

    ResponseEntity<Void> sendDigitalCourtesyMessage(String requestIdx, String xPagopaExtchCxId, DigitalCourtesyMailRequest digitalCourtesyMailRequest) throws RestClientException;
}
