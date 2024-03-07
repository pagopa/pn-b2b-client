package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV23;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingTemplate;
import java.util.function.Predicate;


//@Service(PnPollingStrategy.TIMELINE_SLOW)
public class PnPollingServiceTimelineSlowOldVersion extends PnPollingTemplate<FullSentNotificationV23> {

    @Override
    public FullSentNotificationV23 waitForEvent(String iun, String checkValue) {
        return null;
    }

    @Override
    protected Predicate<FullSentNotificationV23> checkCondition(String iun, String state) {
        //Example use v1 for check
        return null;
    }

    @Override
    protected FullSentNotificationV23 getPollingResponse(String iun, String state) {
        return null;
    }
}