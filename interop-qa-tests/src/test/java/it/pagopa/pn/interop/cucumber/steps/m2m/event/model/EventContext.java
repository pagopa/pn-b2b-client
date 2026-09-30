package it.pagopa.pn.interop.cucumber.steps.m2m.event.model;

import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.enums.InteropEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EventContext {
    private final Map<InteropEvent, M2MEvent> lastEvents = new HashMap<>();

    public M2MEvent getLastEventMatched(InteropEvent interopEvent) {
        return lastEvents.get(interopEvent);
    }

    public void setLastEventMatched(InteropEvent interopEvent, M2MEvent lastEvent) {
        lastEvents.put(interopEvent, lastEvent);
    }
}
