package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.FullSentNotificationV21;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NewNotificationRequestStatusResponseV21;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.TimelineElementV20;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PnPollingResponseV21 extends PnPollingResponse {
    private FullSentNotificationV21 notification;
    private NewNotificationRequestStatusResponseV21 statusResponse;
    private TimelineElementV20 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
}