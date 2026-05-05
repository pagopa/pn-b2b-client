package it.pagopa.interop.event.filter;

import it.pagopa.interop.event.domain.dto.M2MEvent;

import java.util.List;
import java.util.function.Predicate;

@FunctionalInterface
public interface EventPredicate extends Predicate<M2MEvent> {
    static EventPredicate from(Predicate<M2MEvent> predicate) {
        return predicate::test;
    }

    static EventPredicate andAll(List<EventPredicate> predicates) {
        return event -> predicates.stream().allMatch(p -> p.test(event));
    }
}
