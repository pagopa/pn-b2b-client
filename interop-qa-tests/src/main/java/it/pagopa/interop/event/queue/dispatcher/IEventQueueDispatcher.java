package it.pagopa.interop.event.queue.dispatcher;

import it.pagopa.interop.event.queue.IEventQueue;

public interface IEventQueueDispatcher {
    <EventFilter> boolean canHandle(EventFilter eventFilter);
     IEventQueue getQueue();
}
