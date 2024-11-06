package it.pagopa.pn.client.b2b.pa.interop.service.impl;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.ApiClient;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.api.HealthApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.api.TracingsApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.*;
import it.pagopa.pn.client.b2b.pa.interop.config.InteropClientConfigs;
import it.pagopa.pn.client.b2b.pa.interop.IInteropTracingClient;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InteropTracingClientImpl implements IInteropTracingClient {
    private final RestTemplate restTemplate;
    private final TracingsApi tracingsApi;
    private final HealthApi healthApi;
    private final InteropClientConfigs interopClientConfigs;
    private BearerTokenType bearerTokenSetted;

    public InteropTracingClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.tracingsApi = new TracingsApi(createApiClient(interopClientConfigs.getBaseUrl(), interopClientConfigs.getBearerToken1()));
        this.healthApi = new HealthApi(createApiClient(interopClientConfigs.getBaseUrl(), interopClientConfigs.getBearerToken1()));
        this.bearerTokenSetted = BearerTokenType.TENANT_1;
    }

    private ApiClient createApiClient(String basePath, String bearerToken) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(basePath);
        apiClient.setBearerToken(bearerToken);
        return apiClient;
    }

    @Override
    public GetTracingErrorsResponse getTracingErrors(UUID tracingId, Integer offset, Integer limit) throws RestClientException {
        return tracingsApi.getTracingErrors(tracingId, offset, limit);
    }

    @Override
    public GetTracingsResponse getTracings(Integer offset, Integer limit, List<TracingState> states) throws RestClientException {
        return tracingsApi.getTracings(offset, limit, states);
    }

    @Override
    public RecoverTracingResponse recoverTracing(UUID tracingId, Resource _file) throws RestClientException {
        return tracingsApi.recoverTracing(tracingId, _file);
    }

    @Override
    public ReplaceTracingResponse replaceTracing(UUID tracingId, Resource _file) throws RestClientException {
        return tracingsApi.replaceTracing(tracingId, _file);
    }

    @Override
    public SubmitTracingResponse submitTracing(Resource _file, String date) throws RestClientException {
        return tracingsApi.submitTracing(_file, date);
    }

    @Override
    public void getHealthStatus() {
        healthApi.getStatus();
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        switch (bearerToken) {
            case TENANT_1 -> {
                this.tracingsApi.setApiClient(createApiClient(interopClientConfigs.getBaseUrl(), interopClientConfigs.getBearerToken1()));
                this.bearerTokenSetted = BearerTokenType.TENANT_1;
            }
            case TENANT_2 -> {
                this.tracingsApi.setApiClient(createApiClient(interopClientConfigs.getBaseUrl(), interopClientConfigs.getBearerToken2()));
                this.bearerTokenSetted = BearerTokenType.TENANT_2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + bearerToken);
        }
        return true;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }
}
