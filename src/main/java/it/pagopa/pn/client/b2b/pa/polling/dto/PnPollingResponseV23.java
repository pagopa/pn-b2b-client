package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2_3.ProgressResponseElementV23;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class PnPollingResponseV23 extends PnPollingResponse {
    private FullSentNotificationV23 notification;
    private NewNotificationRequestStatusResponseV23 statusResponse;
    private TimelineElementV23 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElementV23> progressResponseElementListV23;
    private ProgressResponseElementV23 progressResponseElementV23;
}