package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV1 extends PnPollingResponse {
    private FullSentNotification notification;
}
