package it.pagopa.interop.notification.cache;

import it.pagopa.interop.cache.CacheConcurrentMapImpl;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import java.util.UUID;

public class NotificationCacheImpl extends CacheConcurrentMapImpl<UUID, Notification> implements NotificationCache {
    public NotificationCacheImpl() {
        super();
    }

    @Override
    public void put(Notification notification) {
        this.put(notification.getId(), notification);
    }

    @Override
    public void putAll(Iterable<Notification> notifications) {
        for (Notification notification : notifications) {
            this.put(notification.getId(), notification);
        }
    }
}
