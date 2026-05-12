package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.interop.notification.OptInNotificationConfig;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import it.pagopa.pn.interop.cucumber.utility.functionalint.Task;
import it.pagopa.pn.interop.cucumber.utility.functionalint.ThrowingConsumer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
public class NotificationStepsConfig {
    @Data
    @AllArgsConstructor
    @Builder
    static class GlobalNotificationConfig {
        private TenantNotificationConfigUpdateSeed tenantConfig;
        private UserNotificationConfigUpdateSeed userConfig;
    }

    enum ConfigStrategy {PER_ROLE, NO_CONFIG}

    private final NotificationClientImpl notificationClient;
    private final NotificationConfigClient notificationConfigClient;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final FeatureLifecycleManager notificationTestsManager;
    private final SharedStepsContext sharedStepsContext;
    private final ConfigFileReader configFileReader;

    // Il ruolo 'support' non permette alcuna configurazione delle notifiche
    private final List<String> excludedRoles = List.of("support");

    private static Map<String, GlobalNotificationConfig> rolesNotificationConfig = new HashMap<>();
    private static final GlobalNotificationConfig noNotificationConfig;
    private static final List<String> fullNotificationList = List.of(
            "eserviceStateChangedToConsumer",
            "agreementActivatedRejectedToConsumer",
            "agreementSuspendedUnsuspendedToConsumer",
            "purposeActivatedRejectedToConsumer",
            "purposeSuspendedUnsuspendedToConsumer",
            "purposeOverQuotaStateToConsumer",
            "purposeQuotaAdjustmentRequestToProducer",
            "agreementManagementToProducer",
            "agreementSuspendedUnsuspendedToProducer",
            "purposeStatusChangedToProducer",
            "clientAddedRemovedToProducer",
            "eserviceTemplateStatusChangedToInstantiator",
            "newEserviceTemplateVersionToInstantiator",
            "eserviceTemplateNameChangedToInstantiator",
            "templateStatusChangedToProducer",
            "delegationApprovedRejectedToDelegator",
            "eserviceNewVersionSubmittedToDelegator",
            "delegationSubmittedRevokedToDelegate",
            "eserviceNewVersionApprovedRejectedToDelegate",
            "certifiedVerifiedAttributeAssignedRevokedToAssignee",
            "clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers"
    );

    private static NotificationConfig configureNotificationWithOptInList(List<String> optInNotifications) {
        NotificationConfig config = new NotificationConfig();
        for (String notification : fullNotificationList) {
            String methodName = "set" + notification.substring(0, 1).toUpperCase() + notification.substring(1);
            try {
                Method notificationMethod = config.getClass().getMethod(methodName, Boolean.class);
                notificationMethod.invoke(config, optInNotifications.contains(notification));

            } catch (NoSuchMethodException e) {
                System.err.println("Metodo per impostare la notifica non trovato: " + methodName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    static {
        String filePath = "config/activatable-notifications.yaml";
        List<OptInNotificationConfig> notificationConfigList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Yaml yaml = new Yaml(new Constructor(OptInNotificationConfig.class));
            yaml.loadAll(reader).forEach(i -> notificationConfigList.add((OptInNotificationConfig) i));

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        for (OptInNotificationConfig notificationConfig : notificationConfigList) {
            GlobalNotificationConfig roleNotificationConfig = GlobalNotificationConfig.builder()
                    .userConfig(new UserNotificationConfigUpdateSeed()
                            .emailNotificationPreference(false)
                            .inAppNotificationPreference(true)
                            .emailDigestPreference(true)
                            .emailConfig(
                                    configureNotificationWithOptInList(notificationConfig.getEmailNotifications())
                            )
                            .inAppConfig(
                                    configureNotificationWithOptInList(notificationConfig.getInAppNotifications())
                            )
                    ).build();
            rolesNotificationConfig.put(notificationConfig.getRole(), roleNotificationConfig);
        }
        noNotificationConfig = GlobalNotificationConfig.builder()
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(false)
                        .emailDigestPreference(false)
                        .emailConfig(configureNotificationWithOptInList(List.of()))
                        .inAppConfig(configureNotificationWithOptInList(List.of()))
                ).build();
    }

    public NotificationStepsConfig(
        SharedStepsContext sharedStepsContext,
        ClientTokenConfigurator clientTokenConfigurator,
        ConfigFileReader configFileReader,
        @Qualifier("notificationFeatureLifecycleManager") FeatureLifecycleManager notificationTestsManager
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;

        // necessario ricorrere all'impl. concreta per usare il suo HttpCallExecutor
        this.notificationClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        this.notificationConfigClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        this.notificationTestsManager = notificationTestsManager;
        this.sharedStepsContext = sharedStepsContext;
        this.configFileReader = configFileReader;
    }

    @Before("@bff-notification and not @disable-notifications-hooks")
    public void switchOnInAppNotification() throws Exception {
        this.configNotificationTests(excludedRoles, this.notificationTestsManager::before, ConfigStrategy.PER_ROLE);
    }

    @After("@bff-notification and not @disable-notifications-hooks")
    public void switchOffInAppNotification() throws Exception {
        this.configNotificationTests(excludedRoles, this.notificationTestsManager::after, ConfigStrategy.NO_CONFIG);
    }

    // NOTE 13 01 2026 Potrebbero presentarsi problemi di race conditions (non andrebbero cancellate le notifiche se un altro test è in corso)
    @After("@bff-notification and not @disable-notifications-hooks")
    public void deleteAllNotifications() throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor notificationExecutor = this.notificationClient.getHttpCallExecutor();
        IHttpExecutor executor = this.sharedStepsContext.getHttpCallExecutor();
        applyTaskForEveryUser(excludedRoles, role -> {
            List<Notification> notifications = pollingService.makePolling(
                this.notificationClient::getAll,
                res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
                "Reperimento notifiche fallito");

            while(!notifications.isEmpty()) {
                List<UUID> notificationsIds = notifications.stream().map(Notification::getId).toList();

                /* TODO 12/01/2026 per bypassare nel breve termine una problematica di sviluppo sono
                  * utilizzati due executors distinti. Correggere usandone uno solo appena possibile. */
                pollingService.makePolling(
                    () -> executor.performCall(() -> this.notificationClient.deleteAll(notificationsIds)),
                    HttpStatus::is2xxSuccessful,
                    "Eliminazione notifiche fallita");
                notifications = pollingService.makePolling(
                    this.notificationClient::getAll,
                    res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
                    "Reperimento notifiche fallito");
            }
        });
    }

    private boolean isResponseUserNotificationConfigNotFound(IHttpExecutor configExecutor) {
        return configExecutor.getResponseStatus().value() == 404 &&
                configExecutor.getErrorMessage().contains("\"title\":\"User notification config not found\"");
    }

    private void configNotificationTests(List<String> excludedRoles,
        ThrowingConsumer<Task> hook, ConfigStrategy configStrategy
    ) throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        hook.accept(() -> applyTaskForEveryUser(excludedRoles, role -> {
            GlobalNotificationConfig config = configStrategy == ConfigStrategy.NO_CONFIG ? noNotificationConfig : rolesNotificationConfig.get(role);
            if (config.getUserConfig() != null) {
                pollingService.makePolling(
                    () -> {
                        this.notificationConfigClient.updateUserNotificationConfig(config.getUserConfig());
                        if (isResponseUserNotificationConfigNotFound(configExecutor)) {
                            log.warn("User notification config not found (ruolo: " + role + " di " + sharedStepsContext.getTenantType() + ") - Ignored");
                        }
                        return null;
                    },
                    res -> (
                            configExecutor.getResponseStatus().is2xxSuccessful() ||
                                    isResponseUserNotificationConfigNotFound(configExecutor)
                    ),
                    "Configurazione notifiche user fallita (ruolo: " + role + " di " + sharedStepsContext.getTenantType() + ")");
            }

            if (config.getTenantConfig() != null) {
                pollingService.makePolling(
                    () -> {
                        this.notificationConfigClient.updateTenantNotificationConfig(config.getTenantConfig());
                        return null;
                    },
                    res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                    "Configurazione notifiche tenant fallita");
            }
        }));
    }

    // TODO generalizzabile in utility separata
    private void applyTaskForEveryUser(List<String> excludedRoles, ThrowingConsumer<String> taskPerRole) throws Exception {
        List<Tenant> tenantList = this.configFileReader.getTenantList();
        for (Tenant tenant : tenantList) {
            Map<String, List<String>> rolesCopy = new HashMap<>(tenant.getUserRoles());
            Set<Entry<String, List<String>>> roles = rolesCopy.entrySet();
            for (Entry<String, List<String>> roleEntry : roles) {
                String role = roleEntry.getKey();
                List<String> users = roleEntry.getValue();
                for (int i = 0; i < users.size() && !excludedRoles.contains(role); i++) {
                    this.sharedStepsContext.setTenantType(tenant.getName());
                    String token = this.sharedStepsContext.getIdentityService()
                        .getToken(tenant.getName(), role, i);
                    this.clientTokenConfigurator.setBearerToken(token);
                    taskPerRole.accept(role);
                }
            }
        }
    }
}
