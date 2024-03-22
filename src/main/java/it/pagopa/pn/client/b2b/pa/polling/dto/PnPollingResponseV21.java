package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV21;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV21 extends PnPollingResponse {
    private FullSentNotificationV21 notification;
}
