package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PaSummary;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PgGroup;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalb2b.model.PgGroupStatus;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.util.List;

public interface IExternalPGServiceClient extends SettableBearerToken{

    List<PaSummary> listOnboardedPa(String paNameFilter, List<String> id) throws RestClientException;
    List<PgGroup> getPgUserGroupsPrivate(PgGroupStatus statusFilter) throws RestClientException;
}
