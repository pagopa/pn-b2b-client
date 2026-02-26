package it.pagopa.interop.notification.cache;

import it.pagopa.interop.cache.Cache;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import java.util.UUID;

public interface NotificationCache extends Cache<UUID, Notification> {
    void put(Notification notification);

    void putAll(Iterable<Notification> notifications);
}
