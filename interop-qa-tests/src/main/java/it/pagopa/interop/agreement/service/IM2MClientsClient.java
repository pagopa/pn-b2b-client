package it.pagopa.interop.agreement.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Client;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;

import java.util.List;
import java.util.UUID;

public interface IM2MClientsClient extends SettableBearerToken, SettableHttpCallExecutor {
    Client getClient(UUID clientId);
    Purposes getClientPurposes(UUID clientId);
    Purposes getClientPurposes(UUID clientId, int offset, int limit);
    Purposes getClientPurposes(UUID clientId, Integer offset, Integer limit, List<UUID> eserviceIds, List<PurposeVersionState> states);

}
