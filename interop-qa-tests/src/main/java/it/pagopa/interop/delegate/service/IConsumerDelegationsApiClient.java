package it.pagopa.interop.delegate.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationTenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import java.util.List;
import java.util.UUID;

public interface IConsumerDelegationsApiClient extends SettableBearerToken {
    DelegationTenants getConsumerDelegatorsWithAgreements(Integer offset, Integer limit, String q);
    DelegationTenants getConsumerDelegators(Integer offset, Integer limit, String q, List<UUID> eserviceIds);
    CompactEServices getConsumerDelegatedEservices(UUID delegatorId, Integer offset, Integer limit, String q);

    CreatedResource createConsumerDelegation(DelegationSeed delegationSeed);
    void approveConsumerDelegation(UUID delegationId);
    void rejectConsumerDelegation(UUID delegationId, RejectDelegationPayload rejectDelegationPayload);
    void revokeConsumerDelegation(UUID delegationId);
}
