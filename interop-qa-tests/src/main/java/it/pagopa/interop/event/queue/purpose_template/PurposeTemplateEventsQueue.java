package it.pagopa.interop.event.queue.purpose_template;

import it.pagopa.interop.event.queue.AbstractEventQueue;
import it.pagopa.interop.event.service.IM2MEventClient;

public class PurposeTemplateEventsQueue extends AbstractEventQueue<PurposeTemplateM2MEvent> {

    public PurposeTemplateEventsQueue(IM2MEventClient eventsClient) {
        super(eventsClient, PurposeTemplateM2MEvent.class);
    }

}
