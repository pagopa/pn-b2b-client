package it.pagopa.pn.cucumber.steps.pa;

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

    @Autowired
    private SharedSteps sharedSteps;

    @Autowired
    private PnPaB2bUtils b2bUtils;

    @Autowired
    private IPnPaB2bClient b2bClient;

    private FullSentNotification notificationResponseComplete;
    private HttpStatusCodeException notificationSentError;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA {string}")
    public void laNotificaPuòEssereCorrettamenteRecuperataDalSistemaTramiteCodiceIUNDallaPA(String paType) {
        selectPA(paType);
        AtomicReference<FullSentNotification> notificationByIun = new AtomicReference<>();

        Assertions.assertDoesNotThrow(() ->
                notificationByIun.set(b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun()))
        );

        Assertions.assertNotNull(notificationByIun.get());
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN dalla PA {string}")
    public void siTentaIlRecuperoDalSistemaTramiteCodiceIUNDallaPA(String paType) {
        selectPA(paType);
        try{
            b2bUtils.getNotificationByIun(sharedSteps.getSentNotification().getIun());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        if (e instanceof HttpStatusCodeException) {
            this.notificationSentError = e;
        }
    }

    }


    @When("la notifica viene inviata tramite api b2b dalla PA {string}")
    public void laNotificaVieneInviataTramiteApiBBDallaPA(String paType) {
        selectPA(paType);
        try {
            b2bUtils.uploadNotification(sharedSteps.getNotificationRequest());
        } catch (HttpServerErrorException | IOException e) {
            if(e instanceof HttpServerErrorException){
                this.notificationSentError = (HttpServerErrorException)e;
            }
        }
    }

    @Then("(l'invio ha prodotto)(l'operazione ha generato) un errore con status code {string}")
    public void lInvioHaProdottoUnErroreConStatusCode(String statusCode) {
        Assertions.assertTrue((this.notificationSentError != null) &&
                (this.notificationSentError.getStatusCode().toString().substring(0,3).equals(statusCode)));
    }

    private void selectPA(String apiKey) {
        switch (apiKey){
            case "MVP_1":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_1);
                break;
            case "MVP_2":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.MVP_2);
                break;
            case "GA":
                this.b2bClient.setApiKeys(IPnPaB2bClient.ApiKeyType.GA);
                break;
            default:
                throw new IllegalArgumentException();
        }
        this.b2bUtils.setClient(b2bClient);
    }



}
