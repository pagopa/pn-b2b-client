package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationsCountBySection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface INotificationClient extends IClient<Notification, UUID> {

    void deleteAll(List<UUID> ids);
    void delete(UUID id);
    boolean existsAll(List<Notification> expected);
    Set<Notification> missingIds(List<Notification> expected);
    void readAll(List<UUID> ids);
    void read(UUID id);
    void unreadAll(List<UUID> ids);
    void unread(UUID id);
    NotificationsCountBySection countBySection();

    Notification get(UUID id);

    List<Notification> getByBodyLike(String like);
    Optional<Notification> getByBody(String body);
    UUID generateId(EntityIdType entityIdType);
    List<Notification> getAll();

    List<Notification> getAll(int offset, int limit);

    Optional<Notification> get(Predicate<Notification> notificationCatcher);
}
