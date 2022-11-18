package it.pagopa.pn.client.b2b.pa.testclient;

import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.AcceptRequestDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateCountsDto;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.MandateDto;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnWebMandateClient extends SettableBearerToken{

     void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException;

     MandateCountsDto countMandatesByDelegate(String status) throws RestClientException;

     MandateDto createMandate(MandateDto mandateDto) throws RestClientException;

     List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException;

     List<MandateDto> listMandatesByDelegator1() throws RestClientException;

     void rejectMandate(String mandateId) throws RestClientException;

     void revokeMandate(String mandateId) throws RestClientException;

}
