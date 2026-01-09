package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface INotificationClient extends IClient<Notification, UUID> {
    Optional<Notification> get(Predicate<Notification> notificationCatcher);
    void deleteAll(List<UUID> ids);
    boolean existsAll(List<Notification> expected);
    Set<UUID> missingIds(List<Notification> expected);
    void readAll(List<UUID> ids);
    Notification getByIdNoCache(UUID id);
}
