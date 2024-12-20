package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDtoRequest;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IMandateReverseServiceClient extends SettableBearerToken{

    String createReverseMandate(MandateDtoRequest mandateDtoRequest) throws RestClientException;
}
