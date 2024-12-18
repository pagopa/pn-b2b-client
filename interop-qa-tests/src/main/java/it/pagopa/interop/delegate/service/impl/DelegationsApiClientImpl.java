package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class DelegationsApiClientImpl implements IDelegationsApiClient {
    private final ProducerDelegationsApi producerDelegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String bearerToken;

    public DelegationsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "bearerToken";
        this.producerDelegationsApi = new ProducerDelegationsApi(createApiClient(bearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CreatedResource createProducerDelegation(String xCorrelationId, DelegationSeed delegationSeed) {
        return producerDelegationsApi.createProducerDelegation(xCorrelationId, delegationSeed);
    }

    @Override
    public void approveDelegation(String xCorrelationId, UUID delegationId) {
        producerDelegationsApi.approveDelegation(xCorrelationId, delegationId);
    }

    @Override
    public void rejectDelegation(String xCorrelationId, UUID delegationId, RejectDelegationPayload rejectDelegationPayload) {
        producerDelegationsApi.rejectDelegation(xCorrelationId, delegationId, rejectDelegationPayload);
    }

    @Override
    public void revokeProducerDelegation(String xCorrelationId, String delegationId) {
        producerDelegationsApi.revokeProducerDelegation(xCorrelationId, delegationId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.producerDelegationsApi.setApiClient(createApiClient(bearerToken));
    }

}
