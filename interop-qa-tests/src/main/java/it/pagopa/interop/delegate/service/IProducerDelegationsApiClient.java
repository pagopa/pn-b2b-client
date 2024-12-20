package it.pagopa.interop.delegate.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;

import java.util.UUID;

public interface IProducerDelegationsApiClient extends SettableBearerToken {
    CreatedResource createProducerDelegation(String xCorrelationId, DelegationSeed delegationSeed);
    void approveDelegation(String xCorrelationId, UUID delegationId);
    void rejectDelegation(String xCorrelationId, UUID delegationId, RejectDelegationPayload rejectDelegationPayload);
    void revokeProducerDelegation(String xCorrelationId, String delegationId);
}
