package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationSteps extends AbstractCommonSteps<Notification, UUID> {
    public NotificationSteps(){
        super("", null, null);
    }

    @When("l'utente tenta di {word} le notifiche recuperate")
    public void readOrDeleteNotifications(String op){
        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
            }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di {word} delle notifiche con almeno un id {entityIdType}")
    public void readOrDeleteNotifications(String op, EntityIdType entityIdType){
        UUID invalidOrNotExistentId = super.generateId(entityIdType);
        UUID validId = super.generateId(null);

        List<UUID> ids = new ArrayList<>();
        ids.add(invalidOrNotExistentId);
        ids.add(validId);

        //TODO: leggere o eliminare
        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
            }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di {word} la notifica con un id {entityIdType}")
    public void readOrDeleteNotification(String op, EntityIdType entityIdType){
        UUID invalidOrNotExistentId = super.generateId(entityIdType);
        UUID validId = super.generateId(null);

        //TODO: leggere o eliminare dagli actual se presenti altrimenti fare polling automatico
        switch(op){
            case "eliminare" -> {
                //TODO: inserisco in unexpected le notifiche che saranno eliminate
                }
            case "leggere" -> {}
            default -> {}
        }
    }

    @When("l'utente tenta di recuperare la lista di notifiche create")
    @When("nessuna notifica è stata eliminata")
    public void pollUntil(){
        //TODO: implementare metodo bind e equals
        //TODO: dovrei rendere piu efficiente la ricerca usando gli id se disponibili
        super.getAllUntil(() -> null, AssertCheckType.PRESENT_AND_MATCHING);
    }

    @When("le notifiche create sono state eliminate")
    public void checkDelete(){
        // TODO: devo assegnare le notifiche create a unexpected
        super.unexpectedEntities.clear();
        super.getAllUntil(() -> null, AssertCheckType.EXPECTED_NOT_PRESENT);
    }

    @When("le notifiche recuperate sono nello stato {word}")
    public void checkRead(String readState){
        boolean read = readState.equals("read");

        if(read){
            // TODO: createdAt dovrebbe essere un timestamp
        } else {
            // TODO: createdAt dovrebbe essere null
        }
    }

    @Override
    public void bindActual(SharedStepsContext context, List<Notification> actualEntities) {

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
