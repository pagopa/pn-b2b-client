package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.privatepaperchannel.model.InformalPrepareResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IPnPaperChannelClient {

    ResponseEntity<InformalPrepareResponse> sendInformalPrepareRequest(InformalPrepareRequest informalPrepareRequest, String xClientId) throws RestClientException;
}
