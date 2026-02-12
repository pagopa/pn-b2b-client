package it.pagopa.interop.agreement.service.m2m.v2;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purposes;

import java.util.UUID;

public interface IM2MClientsClient extends SettableBearerToken {
    Purposes getClientPurposes(UUID clientId);
    Purposes getClientPurposes(UUID clientId, int offset, int limit);
}