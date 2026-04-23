package it.pagopa.pn.cucumber.steps.pa.NotificationInformalStepsV1;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NewNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.NotificationUtilsV25;
import it.pagopa.pn.cucumber.steps.utilitySteps.Destinatario;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Data
@Slf4j
public class NotificationInformalStepsV1 implements NotificationInformalStepsInterface {



    private InformalNotificationRequestV1 notificationRequest;
    private NewNotificationResponse notificationResponse;
    private final SharedSteps sharedSteps;
    private final IPnPaB2bClient b2bClient;
    //private final NotificationVersion version;
    private final NotificationUtilsV25 utils;

    public NotificationInformalStepsV1(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        b2bClient = sharedSteps.getB2bClient();
        //version = NotificationVersion.V25;
        utils = new NotificationUtilsV25(sharedSteps.getContext(), b2bClient, sharedSteps.getPollingFactory());
    }

    @Override
    public void addRecipientToNotification(Destinatario destinatario, Map<String, String> data) {

    }

    @Override
    public void setSenderTaxId(String senderTaxId) {

    }
}
