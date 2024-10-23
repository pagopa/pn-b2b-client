package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NewNotificationRequestStatusResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV1 extends PnPollingResponse {
    //MATTEO
    private FullSentNotification notification;
    private NewNotificationRequestStatusResponse statusResponse;
    private TimelineElement timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
}