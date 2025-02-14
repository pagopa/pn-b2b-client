package it.pagopa.interop.tracing.service.impl;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.ApiClient;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.api.HealthApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.api.TracingsApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingErrorsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.RecoverTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.ReplaceTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.SubmitTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.TracingState;
import it.pagopa.interop.tracing.config.TracingClientConfigs;
import it.pagopa.interop.tracing.service.IInteropTracingClient;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

public abstract class AbstractInteropTracingClient implements IInteropTracingClient {
    private final RestTemplate restTemplate;
    protected final TracingsApi tracingsApi;
    protected final HealthApi healthApi;
    private final TracingClientConfigs tracingClientConfigs;
    protected BearerTokenType bearerTokenSetted;

    public AbstractInteropTracingClient(RestTemplate restTemplate, TracingClientConfigs tracingClientConfigs, String bearerToken) {
        this.restTemplate = restTemplate;
        this.tracingClientConfigs = tracingClientConfigs;
        this.tracingsApi = new TracingsApi(createApiClient(tracingClientConfigs.getBaseUrl(), bearerToken));
        this.healthApi = new HealthApi(createApiClient(tracingClientConfigs.getBaseUrl(), bearerToken));
    }

    protected ApiClient createApiClient(String basePath, String bearerToken) {
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

    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public TracingClientConfigs getTracingClientConfigs() {
        return tracingClientConfigs;
    }
}
