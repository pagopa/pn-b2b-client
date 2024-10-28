package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.bff.recipientmandate.model.BffMandate;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IBffMandateServiceApi extends SettableBearerToken {

    List<BffMandate> getMandatesByDelegatorV1() throws RestClientException;


}
