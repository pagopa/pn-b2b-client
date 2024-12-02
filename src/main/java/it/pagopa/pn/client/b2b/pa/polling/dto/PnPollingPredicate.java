package it.pagopa.pn.client.b2b.pa.polling.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;


@Getter
@Setter
public class PnPollingPredicate {
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.TimelineElement> timelineElementPredicateV1;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.TimelineElementV20> timelineElementPredicateV20;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.TimelineElementV20> timelineElementPredicateV21;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV23> timelineElementPredicateV23;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV25> timelineElementPredicateV25;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementV26> timelineElementPredicateV26;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v1.NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV1;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v2.NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV20;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model_v21.NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV21;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElement> notificationStatusHistoryElementPredicateV23;
    private Predicate<it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationStatusHistoryElementV26> notificationStatusHistoryElementPredicateV26;
}