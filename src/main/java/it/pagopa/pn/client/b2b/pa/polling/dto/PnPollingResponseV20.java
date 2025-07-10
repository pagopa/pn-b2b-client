package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV20;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationRequestStatusResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV20;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook_v20.model.ProgressResponseElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingResponseV20 extends PnPollingResponse {

    private FullSentNotificationV20 notification;
    private NewNotificationRequestStatusResponse statusResponse;
    private TimelineElementV20 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElement> progressResponseElementList;
    private ProgressResponseElement progressResponseElement;
}