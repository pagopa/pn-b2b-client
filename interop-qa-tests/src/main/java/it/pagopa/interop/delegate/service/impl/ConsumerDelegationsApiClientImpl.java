package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IConsumerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ConsumerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactEServices;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationTenants;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConsumerDelegationsApiClientImpl implements IConsumerDelegationsApiClient {
    private final ConsumerDelegationsApi consumerDelegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public ConsumerDelegationsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.consumerDelegationsApi = new ConsumerDelegationsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedResource createConsumerDelegation(String xCorrelationId,
        DelegationSeed delegationSeed) throws RestClientException {
        return consumerDelegationsApi.createConsumerDelegation(xCorrelationId, delegationSeed);
    }

    @Override
    public CompactEServices getConsumerDelegatedEservices(String xCorrelationId, UUID delegatorId,
        Integer offset, Integer limit, String q) throws RestClientException {
        return consumerDelegationsApi.getConsumerDelegatedEservices(xCorrelationId, delegatorId,
            offset,
            limit, q);
    }

    @Override
    public DelegationTenants getConsumerDelegators(String xCorrelationId, Integer offset,
        Integer limit, String q, List<UUID> eserviceIds) throws RestClientException {
        return consumerDelegationsApi.getConsumerDelegators(xCorrelationId, offset, limit, q,
            eserviceIds);
    }

    @Override
    public DelegationTenants getConsumerDelegatorsWithAgreements(String xCorrelationId,
        Integer offset, Integer limit, String q) throws RestClientException {
        return consumerDelegationsApi.getConsumerDelegatorsWithAgreements(xCorrelationId, offset,
            limit, q);
    }

    @Override
    public void rejectConsumerDelegation(String xCorrelationId, UUID delegationId,
        RejectDelegationPayload rejectDelegationPayload) throws RestClientException {
        consumerDelegationsApi.rejectConsumerDelegation(xCorrelationId, delegationId,
            rejectDelegationPayload);
    }

    @Override
    public void revokeConsumerDelegation(String xCorrelationId, String delegationId)
        throws RestClientException {
        consumerDelegationsApi.revokeConsumerDelegation(xCorrelationId, delegationId);
    }

    @Override
    public void approveConsumerDelegation(String xCorrelationId, UUID delegationId)
        throws RestClientException {
        consumerDelegationsApi.approveConsumerDelegation(xCorrelationId, delegationId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.consumerDelegationsApi.setApiClient(createApiClient(bearerToken));
    }
}
