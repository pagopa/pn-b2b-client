package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IProducerDelegationsApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ProducerDelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DelegationSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegationPayload;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProducerDelegationsApiClientImpl implements IProducerDelegationsApiClient {
    private final ProducerDelegationsApi producerDelegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;

    public ProducerDelegationsApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.producerDelegationsApi = new ProducerDelegationsApi(createApiClient("dummyBearer"));
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
        producerDelegationsApi.approveProducerDelegation(xCorrelationId, delegationId);
    }

    @Override
    public void rejectDelegation(String xCorrelationId, UUID delegationId, RejectDelegationPayload rejectDelegationPayload) {
        producerDelegationsApi.rejectProducerDelegation(xCorrelationId, delegationId, rejectDelegationPayload);
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
