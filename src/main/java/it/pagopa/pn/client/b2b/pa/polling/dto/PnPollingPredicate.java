package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;


@Getter
@Setter
public class PnPollingPredicate {

    private Predicate<TimelineElement> timelineElementPredicateV1;
    private Predicate<TimelineElementV20> timelineElementPredicateV20;
    private Predicate<TimelineElementV20> timelineElementPredicateV21;
    private Predicate<TimelineElementV23> timelineElementPredicateV23;
    private Predicate<TimelineElementV25> timelineElementPredicateV25;
    private Predicate<TimelineElementV26> timelineElementPredicateV26;
    private Predicate<TimelineElementV27> timelineElementPredicateV27;
    private Predicate<TimelineElementV28> timelineElementPredicateV28;

    private Predicate<NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV1;
    private Predicate<NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV20;
    private Predicate<NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV21;
    private Predicate<NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV23;
    private Predicate<NotificationStatusHistoryElementV26> notificationStatusHistoryElementPredicateV26;
    private Predicate<NotificationStatusHistoryElementV26> notificationStatusHistoryElementPredicateV28;// todo t v29
}