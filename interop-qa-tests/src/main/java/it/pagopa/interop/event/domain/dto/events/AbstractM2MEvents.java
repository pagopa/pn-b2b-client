package it.pagopa.interop.event.domain.dto.events;

import it.pagopa.interop.event.domain.dto.M2MEvent;
import it.pagopa.interop.event.filter.EventPredicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AbstractM2MEvents<Event extends M2MEvent> implements M2MEvents {
    protected List<Event> events = new ArrayList<>();

    public M2MEvent getLastEvent() {
        return isEmpty(events) ? null : events.get(events.size() - 1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setEvents(List<? extends M2MEvent> events) {
        this.events.clear();
        this.events.addAll((Collection<? extends Event>) events);
    }

    public M2MEvent filter(EventPredicate filter) {
        if(filter == null) throw new IllegalArgumentException("filter cannot be null");

        return events.stream()
                .filter(filter)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public void addEvents(List<? extends M2MEvent> events) {
        this.events.addAll((Collection<? extends Event>) events);
    }

    @Override
    public void addEvents(M2MEvents events) {
        if(events == null) return;
        addEvents(events.getEvents());
    }
}
