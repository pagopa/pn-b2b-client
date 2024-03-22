package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV20 extends PnPollingResponse {
    private FullSentNotificationV20 notification;
}
