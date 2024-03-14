package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV23 extends PnPollingResponse {
    private FullSentNotificationV23 notification;
}
