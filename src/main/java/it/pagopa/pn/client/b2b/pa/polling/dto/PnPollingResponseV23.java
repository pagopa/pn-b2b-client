package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV23 extends PnPollingResponse {
    private FullSentNotificationV23 notification;
    private NewNotificationRequestStatusResponseV23 statusResponse;
    private TimelineElementV23 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
}