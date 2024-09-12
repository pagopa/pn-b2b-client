package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.mandateb2b.model.MandateDto;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IMandateServiceClient {

    List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException;
}
