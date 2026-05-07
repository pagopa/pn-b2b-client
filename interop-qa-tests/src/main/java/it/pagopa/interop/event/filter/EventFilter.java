package it.pagopa.interop.event.filter;

import it.pagopa.interop.event.domain.dto.M2MEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class EventFilter {

    private final List<Predicate<M2MEvent>> predicates = new ArrayList<>();

    private EventFilter() {}

    public static EventFilter builder(){
        return new EventFilter();
    }

    public EventPredicate build(){
        return predicates.stream()
            .reduce(Predicate::and)
            .map(EventPredicate::from)
            .orElse(event -> true);
    }

    public EventFilter like(M2MEvent filter) {
        predicates.add(filter::equals);
        return this;
    }
}
