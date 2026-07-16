package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.pa.recipient.BffLegalNotificationsResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.service.IPnWebPaClient;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
public class InvioNotificheB2bMultiPaSteps {

    private final SharedSteps sharedSteps;
    private final IPnWebPaClient webPaClient;

    @Autowired
    public InvioNotificheB2bMultiPaSteps(SharedSteps sharedSteps) {
        this.sharedSteps = sharedSteps;
        this.webPaClient = sharedSteps.getWebPaClient();
    }


    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA {string}")
    public void notificationCanBeRetrievedWithIUNByPA(String paName) {
        sharedSteps.setPA(paName);
        try {
            AtomicReference<FullSentNotificationV29> fullSentNotification = new AtomicReference<>();
            assertThatCode(() -> fullSentNotification.set(sharedSteps.getSentNotificationLastVersion()))
                    .as("L'invocazione del metodo per il recupero della fullSentNotification non deve lanciare eccezioni")
                    .doesNotThrowAnyException();
            assertThat(fullSentNotification.get()).as("La fullSentNotification recuperata non dev'essere null").isNotNull();
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla web PA {string}")
    public void notificationCanBeRetrievedWithIUNByWebPA(String paName) {
        sharedSteps.setPA(paName);
        AtomicReference<BffLegalNotificationsResponse> notificationByIun = new AtomicReference<>();
        try {
            Assertions.assertDoesNotThrow(() ->
                    notificationByIun.set(webPaClient.searchSentNotification(OffsetDateTime.now().minusDays(1), OffsetDateTime.now(), null, null, null, sharedSteps.getNotificationIun(), 1, null))
            );
            Assertions.assertNotNull(notificationByIun.get());
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("si tenta il recupero dal sistema tramite codice IUN dalla PA {string}")
    public void retrievalAttemptedIUNFromPA(String paName) {
        sharedSteps.setPA(paName);
        try {
            sharedSteps.getSentNotificationLastVersion();
        } catch (HttpStatusCodeException e) {
            this.sharedSteps.setNotificationError(e);
        }
    }

    @Then("(l'invio ha prodotto)(l'operazione ha generato) un errore con status code {string}")
    public void operationProducedAnError(String statusCode) {
        HttpStatusCodeException httpStatusCodeException = this.sharedSteps.consumeNotificationError();
        Assertions.assertTrue((httpStatusCodeException != null) &&
                (httpStatusCodeException.getStatusCode().toString().substring(0, 3).equals(statusCode)));
    }
}
