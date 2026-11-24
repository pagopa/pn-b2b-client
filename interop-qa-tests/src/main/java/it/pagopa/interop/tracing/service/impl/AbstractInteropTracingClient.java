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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public abstract class AbstractInteropTracingClient implements IInteropTracingClient {
    private final RestTemplate restTemplate;
    protected final TracingsApi tracingsApi;
    protected final HealthApi healthApi;
    private final TracingClientConfigs tracingClientConfigs;
    protected BearerTokenType bearerTokenSetted;

    protected AbstractInteropTracingClient(RestTemplate restTemplate, TracingClientConfigs tracingClientConfigs) {
        this.restTemplate = restTemplate;
        this.tracingClientConfigs = tracingClientConfigs;
        this.tracingsApi = new TracingsApi(createApiClient(tracingClientConfigs.getBaseUrl(), "dummyBearer"));
        this.healthApi = new HealthApi(createApiClient(tracingClientConfigs.getBaseUrl(), "dummyBearer"));
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
    public RecoverTracingResponse recoverTracing(UUID tracingId, Resource file) throws RestClientException {
        return tracingsApi.recoverTracing(tracingId, file);
    }

    @Override
    public ResponseEntity<RecoverTracingResponse> recoverTracingWithHttpInfo(UUID tracingId, Resource file) throws RestClientException {
        return tracingsApi.recoverTracingWithHttpInfo(tracingId, file);
    }

    @Override
    public ReplaceTracingResponse replaceTracing(UUID tracingId, Resource file) throws RestClientException {
        return tracingsApi.replaceTracing(tracingId, file);
    }

    @Override
    public ResponseEntity<ReplaceTracingResponse> replaceTracingWithHttpInfo(UUID tracingId, Resource file) throws RestClientException {
        return tracingsApi.replaceTracingWithHttpInfo(tracingId, file);
    }

    @Override
    public SubmitTracingResponse submitTracing(Resource file, String date) throws RestClientException {
        return tracingsApi.submitTracing(file, date);
    }

    @Override
    public ResponseEntity<SubmitTracingResponse> submitTracingWithHttpInfo(Resource file, String date) throws RestClientException {
        return tracingsApi.submitTracingWithHttpInfo(file, date);
    }

    @Override
    public void getHealthStatus() {
        healthApi.getStatus();
    }

    @Override
    public ResponseEntity<Void> callTracingWithIllegalPercentEncodedCharInPath(String method, String subpath) throws RestClientException {
        final RestTemplate restTemplate = new RestTemplate();
        String url = switch (method) {
            case "GET" -> switch (subpath) {
                case "endpoint" -> "/tracings/invalid%c0";
                case "id"       -> "/tracings/123%c0/errors";
                default         -> throw new IllegalArgumentException("Subpath name not supported: " + subpath);
            };
            case "POST" -> switch (subpath) {
                case "endpoint" -> "/tracings/sub%c0mit";
                case "id"       -> "/tracings/123%c0/recover";
                default         -> throw new IllegalArgumentException("Subpath name not supported: " + subpath);
            };
            default -> throw new IllegalArgumentException("Method not supported: " + method);
        };
        URI uri = URI.create(tracingsApi.getApiClient().getBasePath() + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getTracingClientConfigs().getBearerToken1());
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "OpenAPI-Generator/1.0.0/java");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // The expected behavior for this call will raise an exception due to 400 error
        return restTemplate.exchange(uri, HttpMethod.valueOf(method), entity, Void.class);
    }

    public BearerTokenType getBearerTokenSetted() {
        return this.bearerTokenSetted;
    }

    public TracingClientConfigs getTracingClientConfigs() {
        return tracingClientConfigs;
    }
}
