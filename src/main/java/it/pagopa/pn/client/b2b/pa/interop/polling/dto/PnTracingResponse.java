package it.pagopa.pn.client.b2b.pa.interop.polling.dto;

import it.pagopa.interop.client.b2b.generated.openapi.clients.interop.model.GetTracingsResponse;
import it.pagopa.pn.client.b2b.pa.polling.dto.PnPollingResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PnTracingResponse extends PnPollingResponse {
    private GetTracingsResponse getTracingsResponse;
}
