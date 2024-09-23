package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import it.pagopa.pn.client.web.generated.openapi.clients.externalMandate.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import java.util.List;


public interface IPnWebMandateClient extends SettableBearerToken {
     void acceptMandate(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException;
     ResponseEntity<Void> acceptMandateWithHttpInfo(String mandateId, AcceptRequestDto acceptRequestDto) throws RestClientException;
     MandateCountsDto countMandatesByDelegate(String status) throws RestClientException;
     MandateDto createMandate(MandateDto mandateDto) throws RestClientException;
     void updateMandate(String xPagopaPnCxId, CxTypeAuthFleet xPagopaPnCxType, String mandateId, List<String> xPagopaPnCxGroups, String xPagopaPnCxRole, UpdateRequestDto updateRequestDto) throws RestClientException;
     List<MandateDto> listMandatesByDelegate1(String status) throws RestClientException;
     List<MandateDto> listMandatesByDelegator1() throws RestClientException;
     void rejectMandate(String mandateId) throws RestClientException;
     void revokeMandate(String mandateId) throws RestClientException;
     List<MandateDto> searchMandatesByDelegate(String taxId, List<String> groups) throws RestClientException;
     List<MandateDto> searchMandatesByDelegateStatusFilter(String taxId,List<String> status, List<String> groups);
}