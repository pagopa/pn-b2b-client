package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notifications;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;

import java.util.List;
import java.util.UUID;

public class NotificationSteps extends AbstractCommonSteps<Notifications, UUID> {
    public NotificationSteps(){
        super("", null, null);
    }

    @When("l'utente tenta di eliminare le notifiche create")
    public void deleteNotifications(){

    }

    @When("")
    public void deleteNotification(){

    }

    @Override
    public void bindActual(SharedStepsContext context, List<Notifications> actualEntities) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<Notifications> bindExpected(SharedStepsContext context) {
      throw new RuntimeException("Not implemented yet");
    }

    @Override
    protected boolean isEqual(Notifications a, Notifications b) {
        throw new RuntimeException("Not implemented yet");
    }
}
