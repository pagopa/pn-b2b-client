package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.conf.springconfig.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class DelegationApiClientImpl implements IDelegationApiClient {
    private final DelegationsApi delegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final String bearerToken;

    public DelegationApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.bearerToken = "bearerToken";
        this.delegationsApi = new DelegationsApi(createApiClient(bearerToken));
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CompactDelegations getDelegation(String xCorrelationId, Integer offset, Integer limit, List<DelegationState> states, List<UUID> delegatorIds, List<UUID> delegateIds, DelegationKind kind, List<UUID> eserviceIds) {
        return delegationsApi.getDelegations(xCorrelationId, offset, limit, states, delegatorIds, delegateIds, kind, eserviceIds);
    }

    @Override
    public Delegation getDelegation(String xCorrelationId, String delegationId) {
        return delegationsApi.getDelegation(xCorrelationId, delegationId);
    }

    @Override
    public File getDelegationContract(String xCorrelationId, UUID delegationId, UUID contractId) {
        return delegationsApi.getDelegationContract(xCorrelationId, delegationId, contractId);
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.delegationsApi.setApiClient(createApiClient(bearerToken));
    }

}
