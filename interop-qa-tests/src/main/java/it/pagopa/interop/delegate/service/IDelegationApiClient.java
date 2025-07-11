package it.pagopa.interop.delegate.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDelegations;
import it.pagopa.interop.generated.openapi.clients.bff.model.Delegation;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationState;

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface IDelegationApiClient extends SettableBearerToken {
    CompactDelegations getDelegation(Integer offset, Integer limit, List<DelegationState> states, List<UUID> delegatorIds, List<UUID> delegateIds, DelegationKind kind, List<UUID> eserviceIds);
    Delegation getDelegation(UUID delegationId);
    File getDelegationContract(UUID delegationId, UUID contractId);
}
