package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeysIssuerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnLegalPersonAuthClient {

    PublicKeyResponse getPublicKeysWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, List<String> xPagopaPnCxGroups, Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException;

    PublicKeyResponse newPublicKeyWithHttpInfo(PublicKeyRequest request) throws RestClientException;

    PublicKeyResponse rotatePublicKeyWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, PublicKeyRequest publicKeyRequest, List<String> xPagopaPnCxGroups) throws RestClientException;

    ResponseEntity<PublicKeysIssuerResponse> getIssuerStatusWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId) throws RestClientException;

    ResponseEntity<Void> deletePublicKeysWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, List<String> xPagopaPnCxGroups) throws RestClientException;

    ResponseEntity<Void> changeStatusPublicKeyWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, String status, List<String> xPagopaPnCxGroups) throws RestClientException;

}
