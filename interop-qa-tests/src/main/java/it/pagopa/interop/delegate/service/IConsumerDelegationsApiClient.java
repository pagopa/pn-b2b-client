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
    // 22/01/2025 L'effettiva utilità dei parametri di questi metodi è da chiarire. Da considerne la semplificazione.
    DelegationTenants getConsumerDelegatorsWithAgreements(String xCorrelationId, Integer offset, Integer limit, String q);
    DelegationTenants getConsumerDelegators(String xCorrelationId, Integer offset, Integer limit, String q, List<UUID> eserviceIds);
    CompactEServices getConsumerDelegatedEservices(String xCorrelationId, UUID delegatorId, Integer offset, Integer limit, String q);

    CreatedResource createConsumerDelegation(String xCorrelationId, DelegationSeed delegationSeed);
    void approveConsumerDelegation(String xCorrelationId, UUID delegationId);
    void rejectConsumerDelegation(String xCorrelationId, UUID delegationId, RejectDelegationPayload rejectDelegationPayload);
    void revokeConsumerDelegation(String xCorrelationId, String delegationId);
}
