package it.pagopa.interop.event.queue;

import it.pagopa.interop.event.queue.purpose_template.PurposeTemplateEventsQueue;
import it.pagopa.interop.event.service.IM2MEventClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class M2MEventsQueue {

    private final List<IEventQueue<?>> queues = new ArrayList<>();

    public M2MEventsQueue(IM2MEventClient eventClient) {
        Objects.requireNonNull(eventClient, "eventClient cannot be null");
        queues.add(new PurposeTemplateEventsQueue(eventClient));
    }

    public <Event> Optional<Event> find(Event filter) {
        return queues.stream()
                .filter(queue -> queue.canHandle(filter))
                .findFirst()
                .flatMap(queue -> findOnQueue(queue, filter))
                .flatMap(event -> castExactlyAsFilter(event, filter));
    }

    public <Event> Optional<Event> peek() {
        for (IEventQueue<?> queue : queues) {
            Optional<?> event = queue.peek();
            if (event.isPresent()) {
                return castOptional(event);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private static <Event> Optional<Event> castOptional(Optional<?> event) {
        return (Optional<Event>) event;
    }

    private static <Event> Optional<Event> castExactlyAsFilter(Object event, Event filter) {
        if (event == null) {
            return Optional.empty();
        }
        if (filter == null) {
            return castOptional(Optional.of(event));
        }

        Class<?> filterClass = filter.getClass();
        if (!event.getClass().equals(filterClass)) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        Event typedEvent = (Event) event;
        return Optional.of(typedEvent);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Object> findOnQueue(IEventQueue<?> queue, Object filter) {
        return ((IEventQueue<Object>) queue).find(filter);
    }
}
