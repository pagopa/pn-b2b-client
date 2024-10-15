package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.BffPublicKeysResponse;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnLegalPersonAuthClient {

    BffPublicKeysResponse getPublicKeysV1(Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException;

    BffPublicKeyResponse newPublicKeyV1(BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException;

    BffPublicKeyResponse rotatePublicKeyV1(String kid, BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException;

    void deletePublicKeyV1(String kid) throws RestClientException;

    void changeStatusPublicKeyV1(String kid, String status) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);

}
