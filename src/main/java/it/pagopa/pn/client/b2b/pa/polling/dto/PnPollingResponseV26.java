package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import it.pagopa.pn.client.b2b.webhook.generated.openapi.clients.externalb2bwebhook.model_v26.ProgressResponseElementV26;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingResponseV26 extends PnPollingResponse {
    private FullSentNotificationV26 notification;
    private NewNotificationRequestStatusResponseV24 statusResponse;
    private TimelineElementV26 timelineElement;
    private NotificationStatusHistoryElementV26 notificationStatusHistoryElement;
    private List<ProgressResponseElementV26> progressResponseElementListV26;
    private ProgressResponseElementV26 progressResponseElementV26;
}