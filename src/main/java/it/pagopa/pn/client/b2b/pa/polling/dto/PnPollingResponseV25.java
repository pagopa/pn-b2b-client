package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PnPollingResponseV25 extends PnPollingResponse {
    private FullSentNotificationV25 notification;
    private NewNotificationRequestStatusResponseV23 statusResponse;
    private TimelineElementV25 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElementV25> progressResponseElementList;
    private ProgressResponseElementV25 progressResponseElement;
}
