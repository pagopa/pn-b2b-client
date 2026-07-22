package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.interop.notification.OptInNotificationConfig;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import it.pagopa.pn.interop.cucumber.utility.functionalint.Task;
import it.pagopa.pn.interop.cucumber.utility.functionalint.ThrowingConsumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
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

    private final NotificationConfigClient notificationConfigClient;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final FeatureLifecycleManager notificationTestsManager;
    private final SharedStepsContext sharedStepsContext;
    private final ConfigFileReader configFileReader;

    // I ruoli "support", "reviewer", "viewer" non permettono la configurazione delle notifiche
    private final List<String> excludedRoles = List.of("support", "reviewer", "viewer");

    private static Map<String, List<String>> activatableNotificationsByRole = new HashMap<>();

    private static final List<String> fullNotificationList = List.of(
            "eserviceStateChangedToConsumer",
            "agreementActivatedRejectedToConsumer",
            "agreementSuspendedUnsuspendedToConsumer",
            "purposeActivatedRejectedToConsumer",
            "purposeSuspendedUnsuspendedToConsumer",
            "purposeOverQuotaStateToConsumer",
            "purposeQuotaAdjustmentRequestToProducer",
            "eserviceStateChangedToProducer",
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

    /**
     * Restituisce una configurazione per le notifiche (in-app o e-mail) attivandole o disattivandole di default in
     * base al valore di defaultState.
     *
     * <p>
     * Il metodo deve conoscere, tramite activatableNotificationsByRole, le notifiche che è ammesso attivare per
     * ciascun ruolo. Se ci sono campi non ammessi vanno specificati nella request ma devono per forza essere a false
     * a prescindere dal valore che vorremmo. I campi che fanno eccezione rispetto al valore di default si possono
     * elencare in exceptionNotificationFields.
     *
     * @param defaultState true o false per attivare o disattivare il singolo campo relativo a un tipo di notifica
     * @param activatableNotificationsByRole l'elenco dei campi attivabili che deve rispettare il ruolo da configurare
     * @param exceptionNotificationFields l'elenco dei campi che devono avere il valore opposto a quello di default
     * @return un oggetto {@link NotificationConfig} che può configurare sia le notifiche in-app che e-mail
     */
    private static NotificationConfig configureNotificationWithOptInLists(
            boolean defaultState,
            List<String> activatableNotificationsByRole,
            List<String> exceptionNotificationFields
    ) {
        NotificationConfig config = new NotificationConfig();
        for (String notification : fullNotificationList) {
            String methodName = "set" + notification.substring(0, 1).toUpperCase() + notification.substring(1);
            try {
                Method notificationMethod = config.getClass().getMethod(methodName, Boolean.class);
                boolean state = defaultState;
                if (activatableNotificationsByRole.contains(notification)) {
                    if (exceptionNotificationFields.contains(notification)) {
                        state = !defaultState;
                    }
                } else {
                    state = false;
                }
                notificationMethod.invoke(config, state);

            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                        "Not found method to set the notification: " + methodName
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    private static NotificationConfig configureNotificationWithOptInLists(
            boolean defaultState,
            List<String> activatableNotificationsByRole
    ) {
        return configureNotificationWithOptInLists(defaultState, activatableNotificationsByRole, List.of());
    }

    private static NotificationConfig configureNotificationWithOptInLists() {
        // Per disattivare tutte le notifiche basta avere il default a false
        // Le liste delle notifiche ammesse e delle eccezioni possono essere vuote
        return configureNotificationWithOptInLists(false, List.of(), List.of());
    }

    /**
     * Restituisce una configurazione globale per le notifiche (in-app ed e-mail) attivandole o disattivandole
     * di default in base al valore di defaultState.
     *
     * <p>
     * Il metodo deve conoscere, tramite activatableNotificationsByRole, le notifiche che è ammesso attivare per
     * ciascun ruolo. Se ci sono campi non ammessi vanno specificati nella request ma devono per forza essere a false
     * a prescindere dal valore che vorremmo. I campi che fanno eccezione rispetto al valore di default si possono
     * elencare in exceptionNotificationFields.
     *
     * @param defaultState true o false per attivare o disattivare il singolo campo relativo a un tipo di notifica
     * @param notificationType con i valori 'in-app' o 'e-mail' specifica il tipo di notifiche da attivare
     * @param activatableNotificationsByRole l'elenco dei campi attivabili che deve rispettare il ruolo da configurare
     * @param exceptionNotificationFields l'elenco dei campi che devono avere il valore opposto a quello di default
     * @return un oggetto {@link GlobalNotificationConfig} che può configurare le notifiche di un utente
     */
    private static GlobalNotificationConfig configureGlobalNotificationWithOptInLists(
            boolean defaultState,
            String notificationType,
            List<String> activatableNotificationsByRole,
            List<String> exceptionNotificationFields
    ) {
        NotificationConfig inAppConfig = configureNotificationWithOptInLists(
                defaultState, activatableNotificationsByRole, exceptionNotificationFields
        );
        NotificationConfig emailConfig = configureNotificationWithOptInLists();

        if ("e-mail".equals(notificationType)) {
            emailConfig = inAppConfig;
            inAppConfig = configureNotificationWithOptInLists();
        }
        return GlobalNotificationConfig.builder()
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(false)
                        .emailConfig(emailConfig)
                        .inAppConfig(inAppConfig)
                ).build();
    }

    private static GlobalNotificationConfig configureGlobalNotificationWithOptInLists(
            boolean defaultState,
            String notificationType,
            List<String> activatableNotificationsByRole
    ) {
        return configureGlobalNotificationWithOptInLists(
                defaultState, notificationType, activatableNotificationsByRole, List.of()
        );
    }

    /**
     * Restituisce una configurazione globale con tutte le notifiche spente sia in-app che e-mail.
     *
     * <p>
     * Questo overload chiama il metodo generale senza passare alcuna lista opt-in, la conseguenza logica è che tutti
     * i campi relativi alle notifiche verranno disabilitati.
     */
    private static GlobalNotificationConfig configureGlobalNotificationWithOptInLists() {
        return configureGlobalNotificationWithOptInLists(false, "in-app", List.of(), List.of());
    }

    static {
        String filePath = "config/activatable-notifications.yaml";
        List<OptInNotificationConfig> notificationConfigList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Yaml yaml = new Yaml(new Constructor(OptInNotificationConfig.class));
            yaml.loadAll(reader).forEach(i -> notificationConfigList.add((OptInNotificationConfig) i));

        } catch (IOException exception) {
            log.error("Something went wrong reading " + filePath);
            exception.printStackTrace();
        }

        for (OptInNotificationConfig notificationConfig : notificationConfigList) {
            activatableNotificationsByRole.put(
                    notificationConfig.getRole(), notificationConfig.getNotifications()
            );
        }
    }

    public NotificationStepsConfig(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator,
            ConfigFileReader configFileReader,
            @Qualifier("notificationFeatureLifecycleManager") FeatureLifecycleManager notificationTestsManager
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;

        // Necessario ricorrere all'implementazione concreta per usare il suo HttpCallExecutor
//        this.notificationClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        this.notificationConfigClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        this.notificationTestsManager = notificationTestsManager;
        this.sharedStepsContext = sharedStepsContext;
        this.configFileReader = configFileReader;
    }

    @Before("@bff-notification and not @disable-notifications-hooks")
    public void switchOnInAppNotification() throws Exception {
        // In relazione agli utenti con certi ruoli e su certi enti su cui si devono controllare le notifiche,
        // vengono attivate solo quelle necessarie:
        configureNotificationForUser(UserRole.ADMIN, "PA1", "attiva", "in-app");
        configureNotificationForUser(UserRole.SECURITY, "PA1", "attiva", "in-app");
        configureNotificationForUser(UserRole.ADMIN, "PA2", "attiva", "in-app");
        configureNotificationForUser(UserRole.ADMIN, "GSP", "attiva", "in-app");
    }

// Spegnere ed eliminare le notifiche da pochi o tutti gli utenti può determinare un disturbo di altri test in QA
//    @After("@bff-notification and not @disable-notifications-hooks")
//    public void switchOffInAppNotification() throws Exception {
//        this.configNotificationTests(excludedRoles, this.notificationTestsManager::after, ConfigStrategy.NO_CONFIG);
//    }
//
//    // NOTE 13 01 2026 Potrebbero presentarsi problemi di race conditions (non andrebbero cancellate le notifiche se un altro test è in corso)
//    @After("@bff-notification and not @disable-notifications-hooks")
//    public void deleteAllNotifications() throws Exception {
//        PollingService pollingService = this.sharedStepsContext.getPollingService();
//        IHttpExecutor notificationExecutor = this.notificationClient.getHttpCallExecutor();
//        IHttpExecutor executor = this.sharedStepsContext.getHttpCallExecutor();
//        applyTaskForEveryUser(excludedRoles, role -> {
//            List<Notification> notifications = pollingService.makePolling(
//                this.notificationClient::getAll,
//                res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
//                "Reperimento notifiche fallito");
//
//            while(!notifications.isEmpty()) {
//                List<UUID> notificationsIds = notifications.stream().map(Notification::getId).toList();
//
//                /* TODO 12/01/2026 per bypassare nel breve termine una problematica di sviluppo sono
//                  * utilizzati due executors distinti. Correggere usandone uno solo appena possibile. */
//                pollingService.makePolling(
//                    () -> executor.performCall(() -> this.notificationClient.deleteAll(notificationsIds)),
//                    HttpStatus::is2xxSuccessful,
//                    "Eliminazione notifiche fallita");
//                notifications = pollingService.makePolling(
//                    this.notificationClient::getAll,
//                    res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
//                    "Reperimento notifiche fallito");
//            }
//        });
//    }

    private boolean isResponseUserNotificationConfigNotFound(IHttpExecutor configExecutor) {
        return configExecutor.getResponseStatus().value() == 404 &&
                configExecutor.getErrorMessage().contains("\"title\":\"User notification config not found\"");
    }

    private void configNotificationTests(
            List<String> excludedRoles,
            ThrowingConsumer<Task> hook,
            ConfigStrategy configStrategy
    ) throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        hook.accept(() -> applyTaskForEveryUser(excludedRoles, role -> {
            GlobalNotificationConfig config = configStrategy == ConfigStrategy.NO_CONFIG ?
                    configureGlobalNotificationWithOptInLists()
                    : configureGlobalNotificationWithOptInLists(
                            true,
                            "in-app",
                            activatableNotificationsByRole.get(role)
                    );

            if (config.getUserConfig() != null) {
                pollingService.makePolling(
                    () -> {
                        this.notificationConfigClient.updateUserNotificationConfig(config.getUserConfig());
                        if (isResponseUserNotificationConfigNotFound(configExecutor)) {
                            log.warn("User notification config not found (role: " + role + " of " + sharedStepsContext.getTenantType() + ") - Ignored");
                        }
                        return null;
                    },
                    res -> (
                            configExecutor.getResponseStatus().is2xxSuccessful() ||
                                    isResponseUserNotificationConfigNotFound(configExecutor)
                    ),
                    "User notification config failed (role: " + role + " of " + sharedStepsContext.getTenantType() + ")");
            }

            if (config.getTenantConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateTenantNotificationConfig(config.getTenantConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Tenant notification config failed");
            }
        }));
    }

    private void configureNotificationsToUser(List<String> excludedRoles, ConfigStrategy configStrategy) throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        applyTaskForEveryUser(excludedRoles, role -> {
            GlobalNotificationConfig config = switch (configStrategy) {
                case NO_CONFIG -> configureGlobalNotificationWithOptInLists();
                default -> configureGlobalNotificationWithOptInLists(
                        false,
                        "in-app",
                        activatableNotificationsByRole.get(role)
                );
            };
            if (config.getUserConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateUserNotificationConfig(
                                    config.getUserConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "User notification config failed");
            }
            if (config.getTenantConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateTenantNotificationConfig(config.getTenantConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Tenant notification config failed");
            }
        });
    }

    // TODO generalizzabile in utility separata
    private void applyTaskForEveryUser(List<String> excludedRoles, ThrowingConsumer<String> taskPerRole) throws Exception {
        List<Tenant> tenantList = this.configFileReader.getTenantList();
        for (Tenant tenant : tenantList) {
            // FIXME scorciatoia temporanea per interrogare solo PA1 e PA2 e GSP
            if (!tenant.getName().equals("PA1") && !tenant.getName().equals("PA2") && !tenant.getName().equals("GSP")) continue;
            Map<String, List<String>> rolesCopy = new HashMap<>(tenant.getUserRoles());
            Set<Entry<String, List<String>>> roles = rolesCopy.entrySet();
            for (Entry<String, List<String>> roleEntry : roles) {
                //TODO: da eliminare, patch per la gestio
                String role = roleEntry.getKey();
                List<String> users = roleEntry.getValue();
                if (!role.equals("admin")) continue;
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

    @Given("{userRole} di {string} {turnOnOrOff} le notifiche {emailOrInApp} eccetto:")
    public void configureNotificationForUser(
            UserRole role,
            String tenantName,
            String inclusionOperation,
            String notificationType,
            DataTable exceptionsTable)
    {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();

        this.sharedStepsContext.setTenantType(tenantName);
        String token = this.sharedStepsContext.getIdentityService().getToken(tenantName, role.getValue(), 0);
        this.clientTokenConfigurator.setBearerToken(token);

        final AtomicReference<UserNotificationConfigUpdateSeed> seed =
                new AtomicReference<>(configureGlobalNotificationWithOptInLists().getUserConfig());

        if (inclusionOperation.equals("attiva")) {
            if (exceptionsTable.height() == 0) {
                // Attiva tutte le notifiche attivabili per un ruolo senza eccezioni
                seed.set(configureGlobalNotificationWithOptInLists(
                        true,
                        notificationType,
                        activatableNotificationsByRole.get(role.getValue())
                ).getUserConfig());
            } else {
                // Attiva tutte le notifiche attivabili per un ruolo eccetto la lista in tabella
                List<String> exceptionFields = exceptionsTable.asList(String.class);
                seed.set(configureGlobalNotificationWithOptInLists(
                        true,
                        notificationType,
                        activatableNotificationsByRole.get(role.getValue()),
                        exceptionFields
                ).getUserConfig());
            }
        }

        pollingService.makePolling(
                () -> {
                    this.notificationConfigClient.updateUserNotificationConfig(seed.get());
                    return null;
                },
                res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                "User notification config failed");
    }

    @Given("{userRole} di {string} {turnOnOrOff} le notifiche {emailOrInApp}")
    public void configureNotificationForUser(
            UserRole role,
            String tenant,
            String inclusionOperation,
            String notificationType)
    {
        configureNotificationForUser(role, tenant, inclusionOperation, notificationType, DataTable.emptyDataTable());
    }
}
