package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
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
    private List<ProgressResponseElementV29> progressResponseElementList;
    private ProgressResponseElementV29 progressResponseElement;
}