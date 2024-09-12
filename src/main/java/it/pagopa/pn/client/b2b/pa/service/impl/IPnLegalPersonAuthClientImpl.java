package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.api.external.bff.apikey.manager.pg.PublicKeysApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.CxTypeAuthFleet;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeyRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeyResponse;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.apikey.manager.pg.PublicKeysIssuerResponse;
import it.pagopa.pn.client.b2b.pa.service.IPnLegalPersonAuthClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IPnLegalPersonAuthClientImpl implements IPnLegalPersonAuthClient {

    private PublicKeysApi publicKeysApi;

    public IPnLegalPersonAuthClientImpl(PublicKeysApi publicKeysApi) {
        this.publicKeysApi = publicKeysApi;
    }

    @Override
    public PublicKeyResponse getPublicKeysWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, List<String> xPagopaPnCxGroups, Integer limit, String lastKey, String createdAt, Boolean showPublicKey) throws RestClientException {
        return null;
    }

    @Override
    public PublicKeyResponse newPublicKeyWithHttpInfo(PublicKeyRequest request) throws RestClientException {
        return null;
    }

    @Override
    public PublicKeyResponse rotatePublicKeyWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, PublicKeyRequest publicKeyRequest, List<String> xPagopaPnCxGroups) throws RestClientException {
        return null;
    }

    @Override
    public ResponseEntity<PublicKeysIssuerResponse> getIssuerStatusWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId) throws RestClientException {
        return null;
    }

    @Override
    public ResponseEntity<Void> deletePublicKeysWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, List<String> xPagopaPnCxGroups) throws RestClientException {
        return null;
    }

    @Override
    public ResponseEntity<Void> changeStatusPublicKeyWithHttpInfo(String xPagopaPnUid, CxTypeAuthFleet xPagopaPnCxType, String xPagopaPnCxId, String xPagopaPnCxRole, String kid, String status, List<String> xPagopaPnCxGroups) throws RestClientException {
        return null;
    }
}
