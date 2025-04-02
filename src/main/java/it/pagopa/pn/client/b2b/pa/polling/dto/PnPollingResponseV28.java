package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV27;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v28.ProgressResponseElementV28;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PnPollingResponseV28 extends PnPollingResponse {
    private FullSentNotificationV27 notification;
    private NewNotificationRequestStatusResponseV25 statusResponse;
    //    private TimelineElementV27 timelineElement;//TODO V28
    private NotificationStatusHistoryElementV26 notificationStatusHistoryElement;
    private List<ProgressResponseElementV28> progressResponseElementList;
    private ProgressResponseElementV28 progressResponseElement;
}