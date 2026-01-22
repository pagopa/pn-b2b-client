package it.pagopa.interop.delegate.service.impl;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.conf.InteropClientConfigs;
import it.pagopa.interop.delegate.service.IDelegationApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.DelegationsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Retryable(
        retryFor = { HttpServerErrorException.class },
        backoff = @Backoff(delay = 2000)
)
public class DelegationApiClientImpl implements IDelegationApiClient {
    private final DelegationsApi delegationsApi;
    private final RestTemplate restTemplate;
    private final String basePath;
    private final PollingService pollingService;

    public DelegationApiClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs, PollingService pollingService) {
        this.restTemplate = restTemplate;
        this.basePath = interopClientConfigs.getBaseUrl();
        this.delegationsApi = new DelegationsApi(createApiClient("dummyBearer"));
        this.pollingService = pollingService;
    }

    private ApiClient createApiClient(String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public CompactDelegations getDelegation(Integer offset, Integer limit, List<DelegationState> states, List<UUID> delegatorIds, List<UUID> delegateIds, DelegationKind kind, List<UUID> eserviceIds) {
        return delegationsApi.getDelegations(offset, limit, states, delegatorIds, delegateIds, kind, eserviceIds);
    }

    @Override
    public Delegation getDelegation(UUID delegationId) {
        return delegationsApi.getDelegation(delegationId);
    }

    @Override
    public File getDelegationContract(UUID delegationId, UUID contractId) {
        try {
            return delegationsApi.getDelegationContract(delegationId, contractId).getFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void waitForState(UUID delegationId, DelegationState state) {
        pollingService.makePolling(
            () -> delegationsApi.getDelegationWithHttpInfo(delegationId),
            res ->  res.getStatusCode().is2xxSuccessful() || res.getBody().getState().equals(state),
            "La delega non è nello stato atteso '%s'".formatted(state)
        );
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.delegationsApi.setApiClient(createApiClient(bearerToken));
    }

}
