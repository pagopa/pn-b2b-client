package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface IMandateReverseServiceClient {

    ResponseEntity<String> createReverseMandateWithHttpInfo(MandateDtoRequest mandateDtoRequest) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);


}
