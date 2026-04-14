package it.pagopa.interop.event.queue;

import java.util.Optional;

public interface IEventQueue<Event> {
    Optional<Event> find(Event filter);
}
