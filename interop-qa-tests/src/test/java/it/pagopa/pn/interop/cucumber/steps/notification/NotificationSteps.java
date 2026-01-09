package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.notification.INotificationClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import org.assertj.core.api.Assertions;

import java.util.*;

public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {

    private enum NotificationOp { DELETE, READ, UNREAD, UNKNOWN }
    private enum Target { MULTIPLE, SINGLE }

    private final SharedStepsContext sharedStepsContext;
    private final INotificationClient notificationClient;
    private List<Notification> allocated = new LinkedList<>();
    private int toAllocate = 0;

    public NotificationSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext1) {
        super("inAppNotification", clientTokenConfigurator.getNotificationClient(), sharedStepsContext);
        notificationClient = clientTokenConfigurator.getNotificationClient();
        this.sharedStepsContext = sharedStepsContext1;
    }

    @When("{string} ha già generato {int} notifiche")
    public void createNotifications(int n){
        //TODO: creare le notifiche
        this.toAllocate = n;
    }

    @When("l'utente tenta di {word} le notifiche recuperate")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate")
    public void operateWithNotifications(String op) {
        handleOperation(op, null, Target.MULTIPLE);
    }

    @When("l'utente tenta di {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} le notifiche recuperate specificando almeno un id {entityIdType}")
    public void operateWithNotifications(String op, EntityIdType entityIdType) {
        handleOperation(op, entityIdType, Target.MULTIPLE);
    }

    @When("l'utente tenta di {word} la notifica recuperata specificando un id {entityIdType}")
    @When("l'utente tenta di marcare come {word} la notifica recuperata specificando un id {entityIdType}")
    public void operateWithNotification(String op, EntityIdType entityIdType) {
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
    public void refreshAllocated(){
        List<Notification> refreshed = new LinkedList<>();

        allocated.forEach(n -> refreshed.add(notificationClient.getByIdNoCache(n.getId())));
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
        // TODO
    }

    @When("count delle notifiche {word} restituito")
    public void checkNotificationCount(String assertion) {
        boolean exist = parseExistenceToken(assertion);
        // TODO: usare 'exist'
    }

    private void checkDelete(String expectation) {

        switch (expectation) {
            case "DELETED","PRESENT" -> {}
            default -> throw new IllegalArgumentException("Token non riconosciuto: " + expectation);
        };

        var ids = allocated.stream().map(Notification::getId).toList();

        PollingService.makePolling(
                () -> notificationClient.existsAll(allocated),   // true => tutte presenti
                allPresent -> expectation.equals("PRESENT") == allPresent,
                expectation.equals("PRESENT")
                        ? String.format("Le notifiche con id %s risultano eliminate ma dovevano essere presenti", ids)
                        : String.format("Le notifiche con id %s non sono state eliminate", ids),
                30,
                1000
        );
    }

    private void handleOperation(String rawOp, EntityIdType entityIdType, Target target) {
        NotificationOp op = parseOp(rawOp);
        List<UUID> ids = new ArrayList<>(allocated.stream().map(Notification::getId).toList());

        if(entityIdType != null)
            ids.add(client.generateId(entityIdType));

        switch (op) {
            case DELETE -> onDelete(target);
            case READ -> onRead(target);
            case UNREAD -> onUnread(ids);
            case UNKNOWN -> onUnknown(rawOp);
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

    private void onDelete(Target target) {
        switch (target) {
            case MULTIPLE -> notificationClient.deleteAll(actualEntities.stream().map(Notification::getId).toList());
        }
    }

    private void onRead(Target target) {
        switch (target) {
            case MULTIPLE -> notificationClient.readAll(actualEntities.stream().map(Notification::getId).toList());
        }
    }

    private void onUnread(List<UUID> ids) {
        // TODO: chiamata client (dipende da API)
    }

    private void onUnknown(String rawOp) {
        // puoi decidere se fallire subito o ignorare
        // qui scelgo di essere esplicito (meglio nei test)
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
