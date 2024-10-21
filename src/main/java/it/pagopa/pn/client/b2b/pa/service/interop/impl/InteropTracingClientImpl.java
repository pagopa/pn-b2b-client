package it.pagopa.pn.client.b2b.pa.service.interop.impl;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.ApiClient;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.api.TracingsApi;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.*;
import it.pagopa.pn.client.b2b.pa.service.interop.IInteropTracingClient;
import it.pagopa.pn.client.b2b.pa.service.interop.config.InteropClientConfigs;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InteropTracingClientImpl implements IInteropTracingClient {
    private final RestTemplate restTemplate;
    private final TracingsApi tracingsApi;
    private final InteropClientConfigs interopClientConfigs;

    public InteropTracingClientImpl(RestTemplate restTemplate, InteropClientConfigs interopClientConfigs) {
        this.restTemplate = restTemplate;
        this.interopClientConfigs = interopClientConfigs;
        this.tracingsApi = new TracingsApi(createApiClient(interopClientConfigs));
    }

    private ApiClient createApiClient(InteropClientConfigs interopClientConfigs) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(interopClientConfigs.getBasePath());
        apiClient.setBearerToken(interopClientConfigs.getBearerToken());
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
    public RecoverTracingResponse recoverTracing(String tracingId, Resource _file) throws RestClientException {
        return tracingsApi.recoverTracing(tracingId, _file);
    }

    @Override
    public ReplaceTracingResponse replaceTracing(String tracingId, Resource _file) throws RestClientException {
        return tracingsApi.replaceTracing(tracingId, _file);
    }

    @Override
    public SubmitTracingResponse submitTracing(Resource _file, LocalDate date) throws RestClientException {
        return tracingsApi.submitTracing(_file, date);
    }

    @Override
    public boolean setBearerToken(BearerTokenType bearerToken) {
        return false;
    }

    @Override
    public BearerTokenType getBearerTokenSetted() {
        return null;
    }
}
