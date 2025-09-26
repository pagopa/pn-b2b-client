package it.pagopa.interop.delegate.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegations;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegation;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public interface IM2MDelegationClient extends SettableBearerToken {
    ConsumerDelegation createConsumerDelegation(DelegationSeed seed);

    ConsumerDelegations getConsumerDelegations(
        @Nullable List<UUID> delegatorIds,
        @Nullable List<UUID> delegateIds,
        @Nullable List<UUID> eserviceIds);

    ProducerDelegation getProducerDelegation(UUID delegationId);
    ConsumerDelegation getConsumerDelegation(UUID delegationId);
}