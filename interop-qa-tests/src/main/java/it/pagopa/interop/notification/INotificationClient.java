package it.pagopa.interop.notification;

import it.pagopa.interop.common.client.IClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface INotificationClient extends IClient<Notification, UUID> {
    Optional<Notification> get(Predicate<Notification> notificationCatcher);
}
