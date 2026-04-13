package it.pagopa.interop.event.queue;

import it.pagopa.interop.event.service.IM2MEventClient;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class AbstractEventQueue<Event> implements IEventQueue<Event> {
    protected final IM2MEventClient eventsClient;
    private final Class<Event> eventClass;

    private static final String GET_ID_METHOD_NAME = "getId";
    private static final Map<Class<?>, Method> GET_ID_METHOD_CACHE = new ConcurrentHashMap<>();

    protected AbstractEventQueue(IM2MEventClient eventsClient, Class<Event> eventClass) {
        this.eventsClient = Objects.requireNonNull(eventsClient, "eventsClient cannot be null");
        this.eventClass = Objects.requireNonNull(eventClass, "eventClass cannot be null");
    }

    @Override
    public final Optional<Event> find(Event filter) {
        return findTyped(filter);
    }

    @Override
    public final Optional<Event> peek() {
        return peekTyped();
    }

    protected Optional<Event> findTyped(Event filter) {
        return fetchEvents(filter).stream()
                .filter(matches(filter))
                .findFirst();
                //.map(this::trackAndReturn);
    }

    protected Optional<Event> peekTyped() {
        return fetchEvents(null).stream()
                .findFirst();
                //.map(this::trackAndReturn);
    }

    protected List<Event> fetchEvents(Event filter) {
        return eventsClient.getEvents(filter);
    }

    protected Predicate<Event> matches(Event filter) {
        if (filter == null) {
            return event -> true;
        }

        Map<String, Object> filterProps = readableProps(filter);
        if (filterProps.isEmpty()) {
            return event -> true;
        }

        return event -> {
            Map<String, Object> eventProps = readableProps(event);
            for (Map.Entry<String, Object> expected : filterProps.entrySet()) {
                if (!eventProps.containsKey(expected.getKey())) {
                    return false;
                }
                if (!Objects.equals(eventProps.get(expected.getKey()), expected.getValue())) {
                    return false;
                }
            }
            return true;
        };
    }

//    private Event trackAndReturn(Event event) {
//        UUID eventId = extractId(event);
//        if (eventId != null) {
//            lastEventId = eventId;
//        }
//        return event;
//    }

    private static Map<String, Object> readableProps(Object source) {
        if (source == null) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> values = new LinkedHashMap<>();
            for (var descriptor : Introspector.getBeanInfo(source.getClass(), Object.class).getPropertyDescriptors()) {
                if (descriptor.getReadMethod() == null) {
                    continue;
                }

                Object value = descriptor.getReadMethod().invoke(source);
                if (value != null) {
                    values.put(descriptor.getName(), value);
                }
            }
            return values;
        } catch (IntrospectionException | ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot introspect filter/event bean", ex);
        }
    }

    private static UUID extractId(Object source) {
        if (source == null) {
            return null;
        }

        Method getIdMethod = GET_ID_METHOD_CACHE.computeIfAbsent(source.getClass(), clazz -> {
            try {
                Method method = clazz.getMethod(GET_ID_METHOD_NAME);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Class does not expose getId(): " + clazz.getName(), ex);
            }
        });

        try {
            Object value = getIdMethod.invoke(source);
            if (value == null) {
                return null;
            }
            if (value instanceof UUID uuid) {
                return uuid;
            }
            throw new IllegalStateException("getId() must return UUID for class: " + source.getClass().getName());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot invoke getId() for class: " + source.getClass().getName(), ex);
        }
    }

    @Override
    public boolean canHandle(Object filter) {
        return filter == null || eventClass.isInstance(filter) ;
    }
}
