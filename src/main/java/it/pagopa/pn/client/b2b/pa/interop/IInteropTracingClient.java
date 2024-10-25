package it.pagopa.pn.client.b2b.pa.interop;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.*;
import it.pagopa.pn.client.b2b.pa.service.utils.SettableBearerToken;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IInteropTracingClient extends SettableBearerToken {

    GetTracingErrorsResponse getTracingErrors(UUID tracingId, Integer offset, Integer limit) throws RestClientException;

    GetTracingsResponse getTracings(Integer offset, Integer limit, List<TracingState> states) throws RestClientException;

    RecoverTracingResponse recoverTracing(String tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    ReplaceTracingResponse replaceTracing(String tracingId, org.springframework.core.io.Resource _file) throws RestClientException;

    SubmitTracingResponse submitTracing(org.springframework.core.io.Resource _file, LocalDate date) throws RestClientException;

    void getHealthStatus() throws RestClientException;
}
