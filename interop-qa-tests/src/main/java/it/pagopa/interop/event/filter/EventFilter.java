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
        Predicate<M2MEvent> predicate =
                predicates.stream()
                    .reduce(Predicate::and)
                    .orElse(event -> true);

        return new EventPredicate(predicate);
    }

    public EventFilter like(M2MEvent filter) {
        predicates.add(filter::equals);
        return this;
    }
}
