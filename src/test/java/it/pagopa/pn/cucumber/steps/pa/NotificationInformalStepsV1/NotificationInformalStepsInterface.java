package it.pagopa.pn.cucumber.steps.pa.NotificationInformalStepsV1;

import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;

import java.util.Map;

public interface NotificationInformalStepsInterface {

    void addRecipientToNotification(Destinatario destinatario, Map<String, String> data);

    void setSenderTaxId(String senderTaxId);
}
