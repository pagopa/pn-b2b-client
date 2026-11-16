package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PaperCostToInvalidate;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PaSummary;
import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.selfcare.privateapi.PgGroup;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IPnExternalRegistryPrivateUserApi {

    PgUser getPgUsersPrivate(String xPagopaPnUid, String xPagopaPnCxId) throws RestClientException;

    void invalidatePaperCost(String iun, PaperCostToInvalidate paperCostToInvalidate) throws RestClientException;

    /**
     * GET /ext-registry/pa/v1/activated-on-pn
     */
    List<PaSummary> listOnboardedPa(String paNameFilter, List<String> ids) throws RestClientException;

    /**
     * GET /ext-registry/pg/v1/user-groups
     */
    List<PgGroup> getPgUserGroups(String xPagopaPnCxId, String xPagopaPnUid) throws RestClientException;

    void setBearerToken(SettableBearerToken.BearerTokenType bearerToken);
}
