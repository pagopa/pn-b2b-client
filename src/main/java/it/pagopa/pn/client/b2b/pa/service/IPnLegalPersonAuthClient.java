package it.pagopa.pn.client.b2b.pa.service;

public interface IPnLegalPersonAuthClient {

    PublicKeyResponse getPublicKeys(Integer limit, String lastKey, String createdAt, Boolean showPublicKey);

    PublicKeyResponse newPublicKey(PublicKeyRequest request);
}
