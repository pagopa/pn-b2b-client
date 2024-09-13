package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnLegalPersonAuthClient {

    BffPublicKeysResponse getPublicKeysV1(Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException;

    BffPublicKeyResponse newPublicKeyV1(BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException;

    BffPublicKeyResponse rotatePublicKeyV1(String kid, BffPublicKeyRequest bffPublicKeyRequest) throws RestClientException;

    //ResponseEntity<PublicKeysIssuerResponse> getIssuerStatusWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId) throws RestClientException;

    void deletePublicKeyV1(String kid) throws RestClientException;

    void changeStatusPublicKeyV1(String kid, String status) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);

}
