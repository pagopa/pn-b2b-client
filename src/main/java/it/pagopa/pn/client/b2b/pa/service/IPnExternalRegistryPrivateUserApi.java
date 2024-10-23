package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

public interface IPnExternalRegistryPrivateUserApi {

    PgUser getPgUsersPrivate(String xPagopaPnUid, String xPagopaPnCxId) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
