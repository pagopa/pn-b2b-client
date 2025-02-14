package it.pagopa.interop.tracing.service;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.GetTracingsResponse;
import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.tracing.model.TracingState;

import java.util.List;

public class TracingRetriever {
    private final IInteropTracingClient interopTracingClient;

    public TracingRetriever(IInteropTracingClient interopTracingClient) {
        this.interopTracingClient = interopTracingClient;
    }

    public GetTracingsResponse retrieve(int offset, List<TracingState> states) {
        return interopTracingClient.getTracings(offset, 50, states);
    }
}
