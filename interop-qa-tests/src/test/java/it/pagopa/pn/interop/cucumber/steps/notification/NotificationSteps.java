package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationsCountBySection;
import it.pagopa.interop.notification.INotificationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import org.assertj.core.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {

    private enum NotificationOp { DELETE, READ, UNREAD, UNKNOWN }
    private enum Target { MULTIPLE, SINGLE }

    private final SharedStepsContext sharedStepsContext;
    private final INotificationClient notificationClient;
    private List<Notification> allocated = new LinkedList<>();
    private NotificationsCountBySection notificationsCountBySection;
    private int toAllocate = 0;

    public NotificationSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("inAppNotification", clientTokenConfigurator.getNotificationClient(), sharedStepsContext);
        notificationClient = clientTokenConfigurator.getNotificationClient();
        notificationClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
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

    @When("l'utente tenta di recuperare la lista di notifiche create")
    public void pollUntilAllocate() {
        Set<UUID> touchedIds = sharedStepsContext
                .getNotificationCommonContext()
                .getTouchedIds();

        PollingService.makePolling(
                client::getAll,
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
            Notification notification = notificationClient.getByBody(n.getBody())
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
        notificationsCountBySection = notificationClient.countBySection();
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
                () -> notificationClient.existsAll(allocated),   // true => tutte presenti
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

        if (entityIdType != null) {
            UUID generatedId = client.generateId(entityIdType);

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

    private NotificationOp parseOp(String op) {
        if (op == null) return NotificationOp.UNKNOWN;

        return switch (op.toLowerCase()) {
            case "eliminare", "delete" -> NotificationOp.DELETE;
            case "leggere", "read" -> NotificationOp.READ;
            case "unread" -> NotificationOp.UNREAD;
            default -> NotificationOp.UNKNOWN;
        };
    }

    private void onDelete(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> notificationClient.deleteAll(ids);
            case SINGLE -> notificationClient.delete(ids.get(0));
        }
    }

    private void onRead(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> notificationClient.readAll(ids);
            case SINGLE -> notificationClient.read(ids.get(0));
        }
    }

    private void onUnread(List<UUID> ids, Target target) {
        switch (target) {
            case MULTIPLE -> notificationClient.unreadAll(ids);
            case SINGLE -> notificationClient.unread(ids.get(0));
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

    @Override
    public void bindActual(SharedStepsContext context, List<Notification> actualEntities) {
        allocated = actualEntities;
    }

    @Override
    public List<Notification> bindExpected(SharedStepsContext context) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    protected boolean isEqual(Notification a, Notification b) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<Notification> bindUnexpected(SharedStepsContext context) {
        throw new RuntimeException("Not implemented yet");
    }
}
