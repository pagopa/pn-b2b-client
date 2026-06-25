package it.pagopa.interop.tracing.service;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingErrorsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.RecoverTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.ReplaceTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.SubmitTracingResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.TracingState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

public interface IInteropTracingClient extends SettableBearerToken {

    GetTracingErrorsResponse getTracingErrors(UUID tracingId, Integer offset, Integer limit) throws RestClientException;

    GetTracingsResponse getTracings(Integer offset, Integer limit, List<TracingState> states) throws RestClientException;

    RecoverTracingResponse recoverTracing(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    ResponseEntity recoverTracingWithHttpInfo(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    ReplaceTracingResponse replaceTracing(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    ResponseEntity replaceTracingWithHttpInfo(UUID tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    SubmitTracingResponse submitTracing(org.springframework.core.io.Resource _file, String date) throws RestClientException;

    ResponseEntity submitTracingWithHttpInfo(org.springframework.core.io.Resource _file, String date) throws RestClientException;

    void getHealthStatus() throws RestClientException;

    ResponseEntity<Void> callTracingWithIllegalPercentEncodedCharInPath(String method, String subpath) throws RestClientException;

    IdentityService getIdentityService();
}
