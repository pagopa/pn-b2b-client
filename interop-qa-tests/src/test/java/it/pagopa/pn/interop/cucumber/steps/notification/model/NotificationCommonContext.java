package it.pagopa.pn.interop.cucumber.steps.notification.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class NotificationCommonContext {
    private final Set<UUID> touchedIds = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UserNotificationConfig> originalUserNotificationConfigs = new ConcurrentHashMap<>();
}
