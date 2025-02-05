package it.pagopa.interop.tracing.service;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.GetTracingErrorsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.RecoverTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.ReplaceTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.SubmitTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.TracingState;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

public interface IInteropTracingClient extends SettableBearerToken {

    GetTracingErrorsResponse getTracingErrors(UUID tracingId, Integer offset, Integer limit) throws RestClientException;

    GetTracingsResponse getTracings(Integer offset, Integer limit, List<TracingState> states) throws RestClientException;

    RecoverTracingResponse recoverTracing(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    ReplaceTracingResponse replaceTracing(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    SubmitTracingResponse submitTracing(org.springframework.core.io.Resource _file, String date) throws RestClientException;

    void getHealthStatus() throws RestClientException;
}
