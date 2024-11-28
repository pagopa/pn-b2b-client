package it.pagopa.pn.client.b2b.pa.interop.polling.dto;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.TracingState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PnPollingInterop {
    private String tracingId;
    private TracingState status;
}
