package it.pagopa.interop.notification;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;

public interface INotificationConfigClient extends SettableBearerToken {
    TenantNotificationConfig getTenantConfig();
    UserNotificationConfig getUserConfig();
    void updateTenantNotificationConfig(TenantNotificationConfigUpdateSeed seed);
    void updateUserNotificationConfig(UserNotificationConfigUpdateSeed seed);
}
