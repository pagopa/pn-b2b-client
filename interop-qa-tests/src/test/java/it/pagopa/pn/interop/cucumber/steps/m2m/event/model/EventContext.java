package it.pagopa.pn.interop.cucumber.steps.m2m.event.model;

import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateM2MEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventContext {
    private PurposeTemplateM2MEvent lastPurposeTemplateEventMatched;
}
