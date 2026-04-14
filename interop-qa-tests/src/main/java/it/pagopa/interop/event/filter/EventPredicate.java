package it.pagopa.interop.event.filter;

import it.pagopa.interop.event.domain.dto.M2MEvent;
import lombok.Value;

import java.util.function.Predicate;

@Value
public class EventPredicate implements Predicate<M2MEvent> {
    Predicate<M2MEvent> predicate;

    @Override
    public boolean test(M2MEvent m2MEvent) {
        return predicate.test(m2MEvent);
    }
}
