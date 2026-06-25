package it.pagopa.interop.event.domain.dto.events;

import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.filter.EventPredicate;

import java.util.List;

public interface M2MEvents  {
    M2MEvent getLastEvent();
    List<? extends M2MEvent> getEvents();
    void setEvents(List<? extends M2MEvent> events);
    M2MEvent filter(EventPredicate filter);
    void addEvents(List<? extends M2MEvent> events);
    void addEvents(M2MEvents events);
}
