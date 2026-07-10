package it.pagopa.pn.interop.cucumber.steps.notification;

import static it.pagopa.common.util.StringUtils.resolveDynamicValues;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.common.enums.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationsCountBySection;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.AgreementCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.authorization.ClientCreateStep;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.notification.model.DeepLinkType;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import it.pagopa.pn.interop.cucumber.utility.NotificationStore;
import it.pagopa.pn.interop.cucumber.utility.NotificationStore.NotificationUser;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {

    @Override
    public void bindActual(SharedStepsContext context, List<Notification> actualEntities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Notification> bindExpected(SharedStepsContext context) {
        throw new UnsupportedOperationException();
    }

    private enum NotificationOp {DELETE, READ, UNREAD, UPDATE, UNKNOWN}

    private enum NotificationsTarget {MULTIPLE, SINGLE}

    private final AgreementCommonSteps agreementCommonSteps;
    private final ClientCreateStep clientCreateStep;

    private final SharedStepsContext sharedStepsContext;
    private final NotificationClientImpl apiClient;

    private List<Notification> allocated;
    private int toAllocate = 0;
    private NotificationsCountBySection notificationsCountBySection;

    private final NotificationStore notificationStore;

    public NotificationSteps(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator,
            @Qualifier("notificationFeatureLifecycleManager") FeatureLifecycleManager notificationTestsManager,
            NotificationStore notificationStore,
            AgreementCommonSteps agreementCommonSteps,
            ClientCreateStep clientCreateStep
    ) {
        super("inAppNotification", clientTokenConfigurator.getNotificationClient(), sharedStepsContext);
        this.apiClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        this.apiClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.agreementCommonSteps = agreementCommonSteps;
        this.clientCreateStep = clientCreateStep;
        this.notificationStore = notificationStore;
        //this.notificationStore.concurrentSafeInitializeOnce();
        this.sharedStepsContext = sharedStepsContext;
    }

    @When("l'utente tenta di recuperare la lista di notifiche create")
    public void pollUntilAllocate() {
        Set<UUID> touchedIds = sharedStepsContext
                .getNotificationCommonContext()
                .getTouchedIds();

        PollingService.makePolling(
                apiClient::getAll,
                all -> tryAcquireFreshNotifications(all, touchedIds),
                "Attesa di " + toAllocate + " notifiche fresh non ancora toccate",
                30,
                1000
        );
    }

    private boolean tryAcquireFreshNotifications(List<Notification> all, Set<UUID> touchedIds) {
        // 1) filtro + ordinamento (più recenti prima)
        List<Notification> candidates = all.stream()
                .filter(Objects::nonNull)
                .filter(n -> n.getReadAt() == null || n.getReadAt().isEmpty())
                .sorted(Comparator.comparing(
                        Notification::getCreatedAt,
                        Comparator.nullsLast(String::compareTo)
                ).reversed())
                .filter(n -> !touchedIds.contains(n.getId()))
                .toList();

        // 2) acquisizione "atomica" di toAllocate notifiche tramite touchedIds
        List<Notification> acquired = new ArrayList<>(toAllocate);
        for (Notification n : candidates) {
            if (acquired.size() == toAllocate) break;

            UUID id = n.getId();
            if (touchedIds.add(id)) {
                acquired.add(n);
            }
        }

        // 3) se non bastano, rollback e continua polling
        if (acquired.size() < toAllocate) {
            acquired.forEach(n -> touchedIds.remove(n.getId()));
            return false;
        }

        // 4) successo
        this.allocated = acquired;
        return true;
    }

    @When("l'utente tenta di recuperare lo stato aggiornato delle notifiche")
    public void refreshAllocated() {
        List<Notification> refreshed = allocated.stream()
                .map(n -> apiClient.getByBody(n.getBody())
                        .orElseThrow(() -> new IllegalStateException(
                                "Notifica non trovata per body: " + n.getBody()
                        )))
                .toList();

        this.allocated = new LinkedList<>(refreshed);
    }

    @When("{string} ha già generato {int} notifiche")
    public void createNotifications(String tenant, int n) {
        this.toAllocate = n;
        String consumer = tenant.equals("PA1") ? "PA2" : "PA1";

        for (int i = 0; i <= this.toAllocate; i++) {
            agreementCommonSteps.tenantHasAlreadyCreatedEServiceWithStatusAndApproval(tenant, "PUBLISHED", "MANUAL");
            agreementCommonSteps.tenantAlreadyHasFruitionRequestWithState(consumer, "PENDING");
        }

        clientCreateStep.setRole("admin", tenant);
    }

    @When("l'utente tenta di recuperare la lista di notifiche")
    public void getAllNotifications() {
        try {
            allocated = apiClient.getAll();
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("l'utente tenta di {word} le notifiche recuperate")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate")
    public void crudNotifications(String op) {
        handleNotificationOperation(op, null, NotificationsTarget.MULTIPLE);
    }

    @When("l'utente tenta di {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    public void crudNotifications(String op, EntityIdType entityIdType) {
        handleNotificationOperation(op, entityIdType, NotificationsTarget.MULTIPLE);
    }

    @When("l'utente tenta di {word} la notifica recuperata")
    @When("l'utente tenta di marcare come {word} la notifica recuperata")
    public void crudNotification(String op) {
        handleNotificationOperation(op, null, NotificationsTarget.SINGLE);
    }

    @When("l'utente tenta di {word} la notifica recuperata specificando un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} la notifica recuperata specificando un id {entityIdType}")
    public void crudNotification(String op, EntityIdType entityIdType) {
        handleNotificationOperation(op, entityIdType, NotificationsTarget.SINGLE);
    }

    private void handleNotificationOperation(String rawOp, EntityIdType entityIdType, NotificationsTarget target) {
        NotificationOp op = parseOp(rawOp);

        List<UUID> ids = allocated.stream()
                .map(Notification::getId)
                .collect(Collectors.toCollection(ArrayList::new));

        if (entityIdType != null) {
            UUID generatedId = apiClient.generateId(entityIdType);
            if (target == NotificationsTarget.SINGLE) {
                ids.clear();
                ids.add(generatedId);
            } else {
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

    private void onDelete(List<UUID> ids, NotificationsTarget target) {
        switch (target) {
            case MULTIPLE -> apiClient.deleteAll(ids);
            case SINGLE -> apiClient.delete(firstIdOrThrow(ids, "DELETE SINGLE"));
        }
    }

    private void onRead(List<UUID> ids, NotificationsTarget target) {
        switch (target) {
            case MULTIPLE -> apiClient.readAll(ids);
            case SINGLE -> apiClient.read(firstIdOrThrow(ids, "READ SINGLE"));
        }
    }

    private void onUnread(List<UUID> ids, NotificationsTarget target) {
        switch (target) {
            case MULTIPLE -> apiClient.unreadAll(ids);
            case SINGLE -> apiClient.unread(firstIdOrThrow(ids, "UNREAD SINGLE"));
        }
    }

    private UUID firstIdOrThrow(List<UUID> ids, String context) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalStateException("Lista ID vuota in operazione: " + context);
        }
        return ids.get(0);
    }

    @When("lista di notifiche {word} restituita")
    public void checkNotifications(String assertion) {
        boolean exists = parseExistenceToken(assertion);

        if (exists) {
            assertThat(allocated)
                    .as("Il count delle notifiche deve essere presente")
                    .isNotNull();
        } else {
            assertThat(allocated)
                    .as("Il count delle notifiche non deve essere presente")
                    .isNull();
        }
    }

    @When("le notifiche create sono state eliminate")
    public void checkSuccessDelete() {
        checkDelete("DELETED");
    }

    @When("nessuna notifica è stata eliminata")
    public void checkFailDelete() {
        checkDelete("PRESENT");
    }

    @When("le notifiche recuperate sono nello stato {string}")
    @When("la notifica recuperate è nello stato {string}")
    public void checkRead(String readState) {
        this.refreshAllocated();
        boolean read = "read".equalsIgnoreCase(readState);

        if (read) {
            assertThat(allocated)
                    .as("Le notifiche devono essere read")
                    .extracting(Notification::getReadAt)
                    .allMatch(Objects::nonNull);
        } else {
            assertThat(allocated)
                    .as("Le notifiche devono essere unread")
                    .extracting(Notification::getReadAt)
                    .allMatch(Objects::isNull);
        }
    }

    @When("l'utente tenta di recuperare il count delle notifiche")
    public void getNotificationCount() {
        try {
            notificationsCountBySection = apiClient.countBySection();
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
        }
    }

    @When("count delle notifiche {word} restituito")
    public void checkNotificationCount(String assertion) {
        boolean exists = parseExistenceToken(assertion);

        if (exists) {
            assertThat(notificationsCountBySection)
                    .as("Il count delle notifiche deve essere presente")
                    .isNotNull();
        } else {
            assertThat(notificationsCountBySection)
                    .as("Il count delle notifiche non deve essere presente")
                    .isNull();
        }
    }

    private void checkDelete(String expectation) {
        switch (expectation) {
            case "DELETED", "PRESENT" -> { /* ok */ }
            default -> throw new IllegalArgumentException("Token non riconosciuto: " + expectation);
        }

        PollingService.makePolling(
                () -> apiClient.existsAll(allocated),   // true => tutte presenti
                allPresent -> expectation.equals("PRESENT") == allPresent,
                expectation.equals("PRESENT")
                        ? String.format("Le notifiche %s risultano eliminate ma dovevano essere presenti", allocated)
                        : String.format("Le notifiche %s non sono state eliminate", allocated),
                30,
                1000
        );
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

    private void onUnknown(String rawOp) {
        throw new IllegalArgumentException("Operazione non riconosciuta: " + rawOp);
    }

    private boolean parseExistenceToken(String assertion) {
        if (!"viene".equals(assertion) && !"non".equals(assertion)) {
            throw new IllegalArgumentException("Token non riconosciuto: " + assertion);
        }
        return "viene".equals(assertion);
    }

    /* 14 01 2026: versione iniziale di quando si stava tentando di effettuare dei check puntuali caso-per-caso*/
    /*@Then("per l'utente {string} di {string} è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern {string} e {string}")
    public void checkInAppNotificationBody(String role, String tenant, String bodyRegex, String deepLinkRegex){
        clientTokenConfigurator.setBearerToken(
            getContext().getIdentityService().getToken(tenant, role));

        PollingService pollingService = getContext().getPollingService();

        List<Notification> notifications = pollingService.makePolling(
            this.notificationClient::getAll,
            list -> !IterableUtils.isEmpty(list),
            "Non è stata restituita alcuna notifica");

        assertThat(notifications)
            .as("Verifica che almeno una notifica soddisfi i pattern di body e deepLink")
            .anySatisfy(notif -> {
                assertThat(notif.getBody()).matches(bodyRegex);
                assertThat(notif.getDeepLink()).matches(deepLinkRegex);
            });

        clientTokenConfigurator.setBearerToken(this.getContext().getUserToken());
    }*/

    /* 14 01 2026: seconda versione in cui si tenta di innescare tutti le notifiche con una Nrt e visionare quindi le notifiche a posteriori */
   /* @Then("per l'utente {string} di {string} è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern {string} e {string}")
    public void checkInAppNotificationBody(String role, String tenant, String bodyRegex, String deepLinkRegex){
        Set<Notification> notifications = notificationStore.get(NotificationUser.of(role, tenant));

        // FIXME per prove locali, rimuovere
        List<Notification> notificationStream = notifications.stream()
            .filter(a -> a.getBody().contains("stata rimossa dal client")).toList();
        notificationStream.forEach(notification -> System.out.println(notification.getBody()));

        assertThat(notifications)
            .as("Verifica che almeno una notifica soddisfi i pattern di body e deepLink")
            .anySatisfy(notif -> {
                assertThat(notif.getBody()).matches(bodyRegex);
                assertThat(notif.getDeepLink()).matches(deepLinkRegex);
            });
    }*/

//    /* 08 05 2026: terza versione */
//    @Then("l'utente {string} di {string} ha ricevuto la notifica in-app contenente il link {deepLink}")
//    public void checkInAppNotificationBody(String role, String tenant, DeepLinkType deepLinkType, String message){
//        // TODO usare polling
//        try { Thread.sleep(4000); } catch (InterruptedException e) { throw new RuntimeException(e); }
//
//        Set<Notification> notifications = notificationStore.get(NotificationUser.of(role, tenant));
//        message = message.replace("\n", " ");
//
//        String deepLink = resolveLabelsWithSharedContext(deepLinkType.getValue());
//        String finalMessage = resolveLabelsWithSharedContext(message);
//
//        assertThat(notifications)
//                .as("Verifica che almeno una notifica soddisfi i pattern di body e deepLink")
//                .anySatisfy(notif -> {
//                    //assertThat(notif.getBody()).isEqualTo(finalMessage);
//                    //assertThat(notif.getDeepLink()).isEqualTo(deepLink);
//                    assertThat(notif.getBody()).matches(finalMessage);
//                    assertThat(notif.getDeepLink()).matches(deepLink);
//                });
//    }

    /* 08 05 2026: terza versione */
    @Then("{userRole} di {string} ha ricevuto la notifica in-app contenente il link {deepLink}")
    public void checkInAppNotificationBody(UserRole role, String tenant, DeepLinkType deepLinkType, String message) {
        message = message.replace("\n", " ");
        String deepLink = resolveDynamicValues(deepLinkType.getValue(), sharedStepsContext);
        String finalMessage = resolveDynamicValues(message, sharedStepsContext);

        final int MAX_TRIES = 3;
        final int MAX_LIMIT = 50;
        final AtomicInteger tryCount = new AtomicInteger(0);
        final AtomicInteger limit = new AtomicInteger(5);
        final AtomicBoolean foundBody = new AtomicBoolean(false);

        PollingService.makePolling(
                () -> (notificationStore.getLastNotifications(
                        limit.accumulateAndGet(2, (x, y) -> Math.min(x * y, MAX_LIMIT)),
                        NotificationUser.of(role.getValue(), tenant))
                ),
                all -> {
                    try {
                        assertThat(all)
                                .as("Check in-app body message and deep link")
                                .anySatisfy(notif -> {
                                    assertThat(notif.getBody().trim()).isEqualTo(finalMessage);
                                    log.info("Found notification: \"" + finalMessage + "\"");
                                    foundBody.set(true);
                                    if (!deepLink.isEmpty()) {
                                        log.info("Checking deep link...");
                                        assertThat(notif.getDeepLink()).isEqualTo(deepLink);
                                        log.info("Found deep link: " + deepLink);
                                    }
                                });
                        return true;
                    } catch (AssertionError e) {
                        if (tryCount.incrementAndGet() == MAX_TRIES) {
                            if (!foundBody.get()) {
                                if (all.isEmpty()) {
                                    log.warn("No notification received at all before failing");
                                } else {
                                    log.warn("Last retrieved notifications before failing:");
                                    for (int i = all.size() - 1; i >= 0; i--) {
                                        log.warn(all.get(i).getBody());
                                    }
                                }
                            }
                        }
                        return false;
                    }
                },
                "Not Found expected notification: \"" + finalMessage + "\" with DeepLink " + deepLink,
                MAX_TRIES,
                3000
        );
    }

    @Then("{userRole} di {string} ha ricevuto la notifica in-app")
    public void checkInAppNotificationBody(UserRole role, String tenant, String message) {
        checkInAppNotificationBody(role, tenant, DeepLinkType.NO_DEEP_LINK, message);
    }

    @Then("{userRole} di {string} non ha ricevuto la notifica in-app")
    public void checkNoInAppNotificationBody(UserRole role, String tenant, String message) {
        try {
            checkInAppNotificationBody(role, tenant, DeepLinkType.NO_DEEP_LINK, message);
            Assertions.fail("Found not expected notification");
        } catch (Exception e) {
            Assertions.assertTrue(
                    e.getMessage().contains("Not Found expected notification"),
                    "In-app notification failed, but not for the reason: Not Found expected notification." +
                    " Actual reason: " + e.getMessage()
            );
        }
    }
}
