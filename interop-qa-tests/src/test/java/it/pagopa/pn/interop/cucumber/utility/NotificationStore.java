package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationStore {
    @Value
    @AllArgsConstructor(staticName = "of")
    public static class NotificationUser {
        String role;
        String tenant;
    }

    private final ConfigFileReader configFileReader;
    private final IdentityService identityService;
    private final ClientTokenConfigurator clientTokenConfigurator;

    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean setupPerformed = false;
    private final Map<NotificationUser, Set<Notification>> notifications = new ConcurrentHashMap<>();

    public NotificationStore(
        ConfigFileReader configFileReader,
        @Qualifier("interopIdentityService") IdentityService identityService,
        ClientTokenConfigurator clientTokenConfigurator
    ) {
        this.configFileReader = configFileReader;
        this.identityService = identityService;
        this.clientTokenConfigurator = clientTokenConfigurator;
    }

    public void concurrentSafeInitializeOnce() {
        // Double-Checked Locking per il setup
        if (!setupPerformed) {
            lock.lock();
            try {
                if (!setupPerformed) {
                    log.info("{} safe initialization...", this.getClass().getSimpleName());
                    this.initializeNotifications();
                    setupPerformed = true;
                }
            } catch (Exception e) {
                setupPerformed = false;
                throw e;
            } finally {
                lock.unlock();
            }
            log.info("{} initialization done.", this.getClass().getSimpleName());
        } else {
            log.info("{} initialization already done. Skipping...",
                this.getClass().getSimpleName());
        }
    }

    private void initializeNotifications(NotificationUser user) {
        String tenantName = (user == null) ? null : user.getTenant();
        this.applyTaskForEveryUser(
            //List.of("support", "viewer", "reviewer"), // Questi ruoli non ricevono notifiche in-app
            List.of("api", "security", "api,security", "support", "reviewer", "viewer"), // Solo admin non escluso
            (role, tenant) -> {
                int offset = 0;
                List<Notification> currentNotif;
                do {
                    clientTokenConfigurator.setBearerToken(identityService.getToken(tenant.getName(), role));
                    int limit = 30;
                    currentNotif = clientTokenConfigurator.getNotificationClient().getAll(offset,
                        limit);
                    for (Notification notif : currentNotif) {
                        this.put(NotificationUser.of(role, tenant.getName()), notif);
                    }

                    offset+=limit;
                } while (!currentNotif.isEmpty());
            }, tenantName);
    }

    private void initializeNotifications() {
        initializeNotifications(null);
    }

    public void put(NotificationUser key, Notification value) {
        Set<Notification> notificationsSet = this.notifications.computeIfAbsent(key,
            k -> new HashSet<>());
        notificationsSet.add(value);
    }

    public Set<Notification> get(NotificationUser user) {
        // TODO Ora che i test sono automatici, le notifiche non possono essere recuperate solo 1 volta
        // Ma ogni volta che si esegue il Then e dunque il get delle notifiche, si deve aggiornare
        // Qui viene ri-inizializzato NotificationStore ma si deve trovare una soluzione meno impattante
        initializeNotifications(user);
        Set<Notification> notificationsSet = this.notifications.get(user);
        if (notificationsSet == null) {
            notificationsSet = new HashSet<>();
        }

        return notificationsSet;
    }

    public int count(NotificationUser key) {
        Set<Notification> notificationSet = this.notifications.get(key);
        return notificationSet == null  ? 0 : notificationSet.size();
    }

    public int count() {
        int total = 0;
        for(Entry<NotificationUser, Set<Notification>> entry : this.notifications.entrySet()) {
            total += entry.getValue() == null ? 0 : entry.getValue().size();
        }
        return total;
    }

    // TODO generalizzabile in utility separata
    private void applyTaskForEveryUser(
            List<String> excludedRoles,
            BiConsumer<String, Tenant> taskPerRole,
            String userRestriction
    ) {
        List<Tenant> tenantList = this.configFileReader.getTenantList();
        for (Tenant tenant : tenantList) {
            if (userRestriction != null) {
                if (!tenant.getName().equals(userRestriction)) continue;
            }
            Map<String, List<String>> rolesCopy = new HashMap<>(tenant.getUserRoles());
            Set<Entry<String, List<String>>> roles = rolesCopy.entrySet();
            for (Entry<String, List<String>> roleEntry : roles) {
                String role = roleEntry.getKey();
                List<String> users = roleEntry.getValue();
                for (int i = 0; i < users.size() && !excludedRoles.contains(role); i++) {
                    String token = this.identityService.getToken(tenant.getName(), role, i);
                    this.clientTokenConfigurator.setBearerToken(token);
                    taskPerRole.accept(role, tenant);
                }
            }
        }
    }

    private void applyTaskForEveryUser(
            List<String> excludedRoles,
            BiConsumer<String, Tenant> taskPerRole
    ) {
        applyTaskForEveryUser(excludedRoles, taskPerRole, null);
    }
}
