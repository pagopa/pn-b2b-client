package it.pagopa.interop.event.queue;

import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateEventsQueue;
import it.pagopa.interop.event.service.IM2MEventClient;
import lombok.experimental.Delegate;

import java.util.Objects;

public class M2MEventsQueue {

    @Delegate
    private final PurposeTemplateEventsQueue purposeTemplateEventsQueue;

    public M2MEventsQueue(IM2MEventClient eventClient) {
        Objects.requireNonNull(eventClient, "eventClient cannot be null");
        purposeTemplateEventsQueue = new PurposeTemplateEventsQueue(eventClient);
    }

}
