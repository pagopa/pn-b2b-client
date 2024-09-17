package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffNewVirtualKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffVirtualKeyStatusRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffVirtualKeysResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnLegalPersonVirtualKeyServiceClient {

    void changeStatusVirtualKeys(String id, BffVirtualKeyStatusRequest requestVirtualKeyStatus) throws RestClientException;

    BffNewVirtualKeyResponse createVirtualKey(BffNewVirtualKeyRequest requestNewVirtualKey) throws RestClientException;

    void deleteVirtualKey(String id) throws RestClientException;

    BffVirtualKeysResponse getVirtualKeys(Integer limit, String lastKey, String lastUpdate, Boolean showVirtualKey) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);

}
