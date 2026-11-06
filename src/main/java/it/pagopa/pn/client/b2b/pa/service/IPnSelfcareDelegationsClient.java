package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.AllowedProductId;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.selfcaredelegationstest.model.DelegationResponse;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnSelfcareDelegationsClient {

    DelegationResponse createDelegation(DelegationRequest delegationRequest) throws RestClientException;

    void deleteDelegation(String delegationId) throws RestClientException;

    List<DelegationResponse> getDelegations(String institutionId, String brokerId) throws RestClientException;

    AllowedProductId getProductId();
}
