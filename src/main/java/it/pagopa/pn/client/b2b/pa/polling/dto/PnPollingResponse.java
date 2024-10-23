package it.pagopa.pn.client.b2b.pa.polling.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class PnPollingResponse {
    private Boolean result;
    //MATTEO anzicchè v24 però tutti quanti dovrebbero essere una nuova superclasse capostipite di v1, v20, v21, v23 e v24
//    private FullSentNotificationV24 notification;
//    private NewNotificationRequestStatusResponseV23 statusResponse;
//    private TimelineElementV24 timelineElement;
//    private NotificationStatusHistoryElement notificationStatusHistoryElement;
//    private List<ProgressResponseElementV24> progressResponseElementList;
//    private ProgressResponseElementV24 progressResponseElement;
}