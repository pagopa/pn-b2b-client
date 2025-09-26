package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IM2MDelegationClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegation;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ConsumerDelegations;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.ProducerDelegation;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class M2MDelegationClient implements IM2MDelegationClient {
    private final DelegationsApi delegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public M2MDelegationClient(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getM2mBaseUrl();
        this.delegationsApi = new DelegationsApi(createApiClient("dummyBearer"));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public ConsumerDelegation createConsumerDelegation(DelegationSeed seed) {
        return this.delegationsApi.createConsumerDelegation(seed);
    }

    @Override
    public ConsumerDelegations getConsumerDelegations(
        @Nullable List<UUID> delegatorIds,
        @Nullable List<UUID> delegateIds,
        @Nullable List<UUID> eserviceIds) {
        return this.delegationsApi.getConsumerDelegations(
            0,
            30,
            null,
            delegatorIds,
            delegateIds,
            eserviceIds
        );
    }

    @Override
    public ProducerDelegation getProducerDelegation(UUID delegationId) {
        return this.delegationsApi.getProducerDelegation(delegationId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.delegationsApi.setApiClient(createApiClient(bearerToken));
    }
}