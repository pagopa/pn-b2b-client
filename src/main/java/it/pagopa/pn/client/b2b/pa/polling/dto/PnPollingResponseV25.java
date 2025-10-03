package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV25;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingResponseV25 extends PnPollingResponse {

    private FullSentNotificationV25 notification;
    private NewNotificationRequestStatusResponseV25 statusResponse;
    private TimelineElementV25 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElementV25> progressResponseElementList;
    private ProgressResponseElementV25 progressResponseElement;
}