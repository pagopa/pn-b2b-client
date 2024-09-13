package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.virtualKey.pg.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnLegalPersonVirtualKeyServiceClient {

    void changeStatusVirtualKeys(String id, RequestVirtualKeyStatus requestVirtualKeyStatus) throws RestClientException;

    ResponseNewVirtualKey createVirtualKey(RequestNewVirtualKey requestNewVirtualKey) throws RestClientException;

    void deleteVirtualKey(String id) throws RestClientException;

    VirtualKeysResponse getVirtualKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);

}
