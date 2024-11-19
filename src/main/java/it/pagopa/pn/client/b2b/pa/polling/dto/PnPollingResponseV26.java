package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
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
    private List<ProgressResponseElementV25> progressResponseElementListV25;
    private ProgressResponseElementV25 progressResponseElementV25;
}