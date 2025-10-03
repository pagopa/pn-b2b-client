package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElement;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV1 extends PnPollingResponse {

    private FullSentNotification notification;
    private NewNotificationRequestStatusResponse statusResponse;
    private TimelineElement timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
}