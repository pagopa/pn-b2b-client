package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponseV25;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV28;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV28;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model.ProgressResponseElementV29;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PnPollingResponseV29 extends PnPollingResponse {

    private FullSentNotificationV28 notification;
    private NewNotificationRequestStatusResponseV25 statusResponse;
    private TimelineElementV28 timelineElement;
    private NotificationStatusHistoryElementV28 notificationStatusHistoryElement;
    //todo t v29 verificare
    private List<ProgressResponseElementV29> progressResponseElementList;
    private ProgressResponseElementV29 progressResponseElement;
}