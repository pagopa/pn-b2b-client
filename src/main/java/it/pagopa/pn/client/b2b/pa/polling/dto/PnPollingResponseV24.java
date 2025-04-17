package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class PnPollingResponseV24 extends PnPollingResponse {
    private FullSentNotificationV24 notification;
    private NewNotificationRequestStatusResponseV23 statusResponse;
    private TimelineElementV24 timelineElement;
    private NotificationStatusHistoryElement notificationStatusHistoryElement;
    private List<ProgressResponseElementV24> progressResponseElementList;
    private ProgressResponseElementV24 progressResponseElement;
}