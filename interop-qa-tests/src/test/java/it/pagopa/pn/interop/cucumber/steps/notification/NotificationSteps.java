package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

public class NotificationSteps {

    private enum NotificationOp { DELETE, READ, UNREAD, UNKNOWN, UPDATE }
    private enum Target { MULTIPLE, SINGLE, USER, TENANT }

    private final SharedStepsContext sharedStepsContext;
    private final NotificationClientImpl apiClient;
    private final NotificationConfigClient configClient;

    private List<Notification> allocated = new LinkedList<>();
    private int toAllocate = 0;
    private NotificationsCountBySection notificationsCountBySection;

    private TenantNotificationConfig actualTenantNotificationConfig;
    private UserNotificationConfig actualUserNotificationConfig;
    private TenantNotificationConfig expectedTenantNotificationConfig;
    private UserNotificationConfig expectedUserNotificationConfig;

    public NotificationSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        apiClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        apiClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        configClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        configClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());

        this.sharedStepsContext = sharedStepsContext;
    }

    @When("{string} ha già generato {int} notifiche")
    public void createNotifications(int n){
        //TODO: creare le notifiche
        this.toAllocate = n;
    }

    @When("l'utente tenta di {word} le notifiche recuperate")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate")
    public void crudNotifications(String op) {
        handleOperation(op, null, Target.MULTIPLE);
    }

    @When("l'utente tenta di {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    public void crudNotifications(String op, EntityIdType entityIdType) {
        handleOperation(op, entityIdType, Target.MULTIPLE);
    }

    @When("l'utente tenta di {word} la notifica recuperata")
    @When("l'utente tenta di marcare come {word} la notifica recuperata")
    public void crudNotification(String op) {
        handleOperation(op, null, Target.SINGLE);
    }

    @When("l'utente tenta di {word} la notifica recuperata specificando un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} la notifica recuperata specificando un id {entityIdType}")
    public void crudNotification(String op, EntityIdType entityIdType) {
        handleOperation(op, entityIdType, Target.SINGLE);
    }

    @When("si tenta di {word} la configurazione delle notifiche per {word}")
    public void crudConfiguration(String rawOp, String t){
        Target target = Target.valueOf(t.toUpperCase());
        handleConfigOperation(rawOp, null, target);
    }

    @When("si tenta di {word} la configurazione delle notifiche per {word} specificando un valore {entityIdType}")
    public void crudConfiguration(String rawOp, String t, EntityIdType entityIdType) {
        Target target = Target.valueOf(t.toUpperCase());
        handleConfigOperation(rawOp, entityIdType, target);
    }

    @When("la configurazione delle notifiche per {word} {word} restituita")
    public void checkReadConfiguration(String target, String assertion){
        Target configTarget = Target.valueOf(target.toUpperCase());

        boolean exist = parseExistenceToken(assertion);
        var config = configTarget == Target.USER ? actualUserNotificationConfig : actualTenantNotificationConfig;

        if(exist) Assertions.assertThat(config).as("La configurazione deve essere presente").isNotNull();
        else Assertions.assertThat(actualTenantNotificationConfig).as("La configurazione deve essere presente").isNotNull();
    }

    @When("modifica {word} applicata")
    public void checkUpdateConfiguration(String assertion){
        boolean equals = parseExistenceToken(assertion);
        var actual = actualTenantNotificationConfig != null ? actualTenantNotificationConfig : actualUserNotificationConfig;
        var expected = expectedTenantNotificationConfig != null ? expectedTenantNotificationConfig : actualUserNotificationConfig;

        if(equals) Assertions.assertThat(actual).as("Actual ed expected devono coincidere").isEqualTo(expected);
        else Assertions.assertThat(actual).as("Actual ed expected non devono coincidere").isNotEqualTo(expected);
    }

    @When("l'utente tenta di recuperare la lista di notifiche create")
    public void pollUntilAllocate() {
        Set<UUID> touchedIds = sharedStepsContext
                .getNotificationCommonContext()
                .getTouchedIds();

        PollingService.makePolling(
                apiClient::getAll,
                all -> {
                    // 1) filtro + ordinamento
                    List<Notification> candidates = all.stream()
                            .filter(Objects::nonNull)
                            .sorted(Comparator.comparing(Notification::getCreatedAt, Comparator.nullsLast(String::compareTo)).reversed())
                            .filter(n -> !touchedIds.contains(n.getId()))
                            .toList();

                    // 2) provo ad allocare atomicamente M notifiche
                    List<Notification> acquired = new ArrayList<>(toAllocate);
                    for (Notification n : candidates) {
                        UUID id = n.getId();

                        // add è atomico: solo uno scenario riesce a "prendere" l'id
                        if (touchedIds.add(id)) {
                            acquired.add(n);
                            if (acquired.size() == toAllocate) {
                                break;
                            }
                        }
                    }

                    // 3) se non sono riuscito a prenderne abbastanza, rollback
                    if (acquired.size() < toAllocate) {
                        acquired.forEach(n -> touchedIds.remove(n.getId()));
                        return false; // continua polling
                    }

                    // 4) successo: assegno allo scenario
                    this.allocated = acquired;
                    return true;
                },
                "Attesa di " + toAllocate + " notifiche fresh non ancora toccate",
                30,
                1000
        );


    }

    @When("l'utente tenta di recuperare lo stato aggiornato delle notifiche")
    public void refreshAllocated() {
        List<Notification> refreshed = new LinkedList<>();

        allocated.forEach(n -> {
            Notification notification = apiClient.getByBody(n.getBody())
                    .orElseThrow(() -> new IllegalStateException(
                            "Notifica non trovata per body: " + n.getBody()
                    ));
            refreshed.add(notification);
        });

        this.allocated = refreshed;
    }

    @When("le notifiche create sono state eliminate")
    public void checkSuccessDelete(){
        checkDelete("DELETED");
    }

    @When("nessuna notifica è stata eliminata")
    public void checkFailDelete(){
        checkDelete("PRESENT");
    }

    @When("le notifiche recuperate sono nello stato {word}")
    @When("la notifica recuperate è nello stato {word}")
    public void checkRead(String readState) {
        boolean read = "read".equalsIgnoreCase(readState);

        if (read) {
            Assertions.assertThat(allocated).as("Le notifiche devono essere read").map(Notification::getReadAt).isNotNull();
        } else {
            Assertions.assertThat(allocated).as("Le notifiche devono essere unread").map(Notification::getReadAt).isNull();
        }
    }

    @When("l'utente tenta di recuperare il count delle notifiche")
    public void getNotificationCount() {
        notificationsCountBySection = apiClient.countBySection();
    }

    @When("count delle notifiche {word} restituito")
    public void checkNotificationCount(String assertion) {
        boolean exists = parseExistenceToken(assertion);

        if (exists) {
            Assertions.assertThat(notificationsCountBySection)
                    .as("Il count delle notifiche deve essere presente")
                    .isNotNull();
        } else {
            Assertions.assertThat(notificationsCountBySection)
                    .as("Il count delle notifiche non deve essere presente")
                    .isNull();
        }
    }

    private void checkDelete(String expectation) {

        switch (expectation) {
            case "DELETED","PRESENT" -> {}
            default -> throw new IllegalArgumentException("Token non riconosciuto: " + expectation);
        }

        PollingService.makePolling(
                () -> apiClient.existsAll(allocated),   // true => tutte presenti
                allPresent -> expectation.equals("PRESENT") == allPresent,
                expectation.equals("PRESENT")
                        ? String.format("Le notifiche con %s risultano eliminate ma dovevano essere presenti", allocated)
                        : String.format("Le notifiche con %s non sono state eliminate", allocated),
                30,
                1000
        );
    }

    private void handleOperation(String rawOp, EntityIdType entityIdType, Target target) {
        NotificationOp op = parseOp(rawOp);

        List<UUID> ids = allocated.stream()
                .map(Notification::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        if (entityIdType != null && target != Target.USER && target != Target.TENANT) {
            UUID generatedId = apiClient.generateId(entityIdType);

            if (target == Target.SINGLE) {
                ids.clear();
                ids.add(generatedId);
            } else if (target == Target.MULTIPLE) {
                ids.add(generatedId);
            }
        }

        switch (op) {
            case DELETE -> onDelete(ids, target);
            case READ -> onRead(ids, target);
            case UNREAD -> onUnread(ids, target);
            default -> onUnknown(rawOp);
        }
    }

    private void handleConfigOperation(String rawOp, EntityIdType entityIdType, Target target){
        NotificationOp op = parseOp(rawOp);
        boolean isUser = target == Target.USER;

        UserNotificationConfigUpdateSeed userSeed = new UserNotificationConfigUpdateSeed();
        TenantNotificationConfigUpdateSeed tenantSeed = new  TenantNotificationConfigUpdateSeed();

        if (entityIdType != null) {
            if(entityIdType.equals(EntityIdType.NON_EXISTENT_ID)) {
                userSeed.setEmailConfig(null);
                tenantSeed.setEnabled(null);
            }
            else if(entityIdType.equals(EntityIdType.INVALID_ID)) {
                userSeed = null;
                tenantSeed = null;
            }
            else
                throw new IllegalArgumentException("EntityIdType non gestito: " + entityIdType);
        }
        else {
            if(actualUserNotificationConfig != null) userSeed.setEmailNotificationPreference(!actualUserNotificationConfig.getEmailNotificationPreference());
            if(actualTenantNotificationConfig != null) tenantSeed.setEnabled(!actualTenantNotificationConfig.getEnabled());
        }

        switch (op) {
            case READ -> onConfigRead(target);
            case UPDATE -> onConfigUpdate(isUser ? userSeed : tenantSeed, target);
            default -> onUnknown(rawOp);
        }
    }

    private NotificationOp parseOp(String op) {
        if (op == null) return NotificationOp.UNKNOWN;

        return switch (op.toLowerCase()) {
            case "eliminare", "delete" -> NotificationOp.DELETE;
            case "leggere", "read", "recuperare" -> NotificationOp.READ;
            case "unread" -> NotificationOp.UNREAD;
            case "modificare" -> NotificationOp.UPDATE;
            default -> NotificationOp.UNKNOWN;
        };
    }

    private void onDelete(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> apiClient.deleteAll(ids);
            case SINGLE -> apiClient.delete(ids.get(0));
            default -> throw new IllegalArgumentException("Unrecognized target: " + target);
        }
    }

    private void onRead(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> apiClient.readAll(ids);
            case SINGLE -> apiClient.read(ids.get(0));
            case USER -> actualUserNotificationConfig = configClient.getUserConfig();
            case TENANT ->  actualTenantNotificationConfig = configClient.getTenantConfig();
            default -> throw new IllegalArgumentException("Unrecognized target: " + target);
        }
    }

    private void onConfigRead(Target target){
        switch (target) {
            case USER -> actualUserNotificationConfig = configClient.getUserConfig();
            case TENANT -> actualTenantNotificationConfig = configClient.getTenantConfig();
            default -> throw new IllegalArgumentException("Unrecognized target: " + target);
        }
    }

    private void onConfigUpdate(Object seed, Target target) {
        switch (target) {
            case USER -> {
                UserNotificationConfigUpdateSeed userSeed = (UserNotificationConfigUpdateSeed) seed;
                expectedUserNotificationConfig = configClient.getUserConfig();

                configClient.updateUserNotificationConfig(userSeed);
                expectedUserNotificationConfig.setEmailNotificationPreference(userSeed.getEmailNotificationPreference());
            }
            case TENANT -> {
                TenantNotificationConfigUpdateSeed tenantSeed = (TenantNotificationConfigUpdateSeed) seed;
                expectedTenantNotificationConfig = configClient.getTenantConfig();

                configClient.updateTenantNotificationConfig(tenantSeed);
                expectedTenantNotificationConfig.setEnabled(tenantSeed.getEnabled());
            }
            default -> throw new IllegalArgumentException("Unrecognized target: " + target);
        }
    }

    private void onUnread(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> apiClient.unreadAll(ids);
            case SINGLE -> apiClient.unread(ids.get(0));
            default -> throw new IllegalArgumentException("Unrecognized target: " + target);
        }
    }

    private void onUnknown(String rawOp) {
        throw new IllegalArgumentException("Operazione non riconosciuta: " + rawOp);
    }

    private boolean parseExistenceToken(String assertion) {
        if (!"viene".equals(assertion) && !"non".equals(assertion)) {
            throw new IllegalArgumentException("Token non riconosciuto: " + assertion);
        }
        return "viene".equals(assertion);
    }
}
