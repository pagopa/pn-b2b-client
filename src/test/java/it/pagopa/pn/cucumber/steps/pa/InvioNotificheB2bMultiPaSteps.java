package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV24;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV25;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.client.web.generated.openapi.clients.webPa.model.NotificationSearchResponse;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class InvioNotificheB2bMultiPaSteps {

    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;
    private final IPnWebPaClient webPaClient;

    @Autowired
    public InvioNotificheB2bMultiPaSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.b2bUtils = this.sharedSteps.getB2bUtils();
        this.webPaClient = sharedSteps.getWebPaClient();
    }


    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA {string}")
    public void notificationCanBeRetrievedWithIUNByPA(String paType) {
        sharedSteps.selectPA(paType);
        AtomicReference<FullSentNotificationV25> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()))
            );

            Assertions.assertNotNull(notificationByIun.get());
        }catch (AssertionFailedError assertionFailedError){
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }


    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla web PA {string}")
    public void notificationCanBeRetrievedWithIUNByWebPA(String paType) {
        sharedSteps.selectPA(paType);

        AtomicReference<NotificationSearchResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(),null,null,null,sharedSteps.getSentNotification().getIun(),1,null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionFailedError assertionFailedError) {
            sharedSteps.throwAssertFailerWithIUN(assertionFailedError);
        }
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN dalla PA {string}")
    public void retrievalAttemptedIUNFromPA(String paType) {
        sharedSteps.selectPA(paType);
        try{
            b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun());
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }



    @Then("(l'invio ha prodotto)(l'operazione ha generato) un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


}
