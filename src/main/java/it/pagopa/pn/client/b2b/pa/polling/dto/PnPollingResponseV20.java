package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NewNotificationRequestStatusResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v2.ProgressResponseElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingResponseV20 extends PnPollingResponse {
    //MATTEO
    private FullSentNotificationV20 notification;
    private NewNotificationRequestStatusResponse statusResponse;
    private TimelineElementV20 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElement> progressResponseElementList;
    private ProgressResponseElement progressResponseElement;
}