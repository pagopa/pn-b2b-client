package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.pa.PnPaB2bUtils;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotification;
import it.pagopa.pn.client.b2b.pa.impl.IPnPaB2bClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicReference;

public class InvioNotificheB2bMultiPa {

    private final SharedSteps sharedSteps;
    private final PnPaB2bUtils b2bUtils;
    private final IPnPaB2bClient b2bClient;

    private HttpStatusCodeException notificationSentError;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public InvioNotificheB2bMultiPa(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.b2bUtils = this.sharedSteps.getB2bUtils();
        this.b2bClient = this.sharedSteps.getB2bClient();
    }



    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA {string}")
    public void laNotificaPuòEssereCorrettamenteRecuperataDalSistemaTramiteCodiceIUNDallaPA(String paType) {
        sharedSteps.selectPA(paType);
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()))
        );

        Assertions.assertNotNull(notificationByIun.get());
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN dalla PA {string}")
    public void siTentaIlRecuperoDalSistemaTramiteCodiceIUNDallaPA(String paType) {
        sharedSteps.selectPA(paType);
        try{
            b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun());
        } catch (HttpStatusCodeException e) {
            this.notificationSentError = e;
        }
    }


    @When("la notifica viene inviata tramite api b2b dalla PA {string}")
    public void laNotificaVieneInviataTramiteApiBBDallaPA(String paType) {
        sharedSteps.selectPA(paType);
        try {
            b2bUtils.uploadNotification(sharedSteps.getNotificationRequest());
        } catch (HttpStatusCodeException | IOException e) {
            if(e instanceof HttpStatusCodeException){
                this.notificationSentError = (HttpStatusCodeException)e;
            }
        }
    }

    @Then("(l'invio ha prodotto)(l'operazione ha generato) un errore con status code {string}")
    public void lInvioHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationSentError != null) &&
                (this.notificationSentError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }


}
