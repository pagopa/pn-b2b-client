package it.pagopa.pn.cucumber.steps.pa;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.tracciaturaIndagine.reader.TimelineDetailsReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Notifiche fermate prima di un rilascio e riprese dopo, passando lo IUN con -Dpn.ripresa.iun.&lt;chiave&gt;.
 */
@Slf4j
public class NotificationResumptionSteps {
    private static final String RIPRESA_PROPERTY_PREFIX = "pn.ripresa.iun.";

    private final SharedSteps sharedSteps;
    private final Environment environment;
    private final TimelineDetailsReader timelineDetailsReader;

    public NotificationResumptionSteps(SharedSteps sharedSteps, Environment environment, TimelineDetailsReader timelineDetailsReader) {
        this.sharedSteps = sharedSteps;
        this.environment = environment;
        this.timelineDetailsReader = timelineDetailsReader;
    }

    @Then("si annota lo IUN della notifica con la chiave {string}")
    public void annotateIun(String key) {
        String iun = sharedSteps.getNotificationIun();
        assertThat(iun).as("Nessuna notifica inviata di cui annotare lo IUN").isNotNull();
        log.info("Notifica da riprendere: {}{}={}", RIPRESA_PROPERTY_PREFIX, key, iun);
    }

    @Given("si riprende la notifica annotata con la chiave {string} inviata dal {string}")
    public void resumeNotification(String key, String paName) {
        String iun = readAnnotatedIun(key);
        sharedSteps.setPA(paName);
        FullSentNotificationV29 notification = assertDoesNotThrow(
                () -> sharedSteps.getSentNotificationLastVersionByIun(iun),
                () -> String.format("Notifica %s non recuperabile dalla PA %s", iun, paName));
        sharedSteps.setNotificationIun(notification.getIun());
        sharedSteps.getNotificationIunList().add(notification.getIun());
        sharedSteps.setNotificationCreationDate(notification.getSentAt());
        log.info("Ripresa la notifica {} (chiave {}), stato {}", notification.getIun(), key, notification.getNotificationStatus());
    }

    /**
     * Le bonarie non si leggono con le API delle legali: l'esistenza si verifica sulla timeline.
     */
    @Given("si riprende la comunicazione bonaria annotata con la chiave {string}")
    public void resumeInformalNotification(String key) {
        String iun = readAnnotatedIun(key);
        assertThat(timelineDetailsReader.readDetails(iun, "SEND_ANALOG_MESSAGE", 0, null))
                .as("Nessun invio cartaceo (SEND_ANALOG_MESSAGE) per la comunicazione bonaria %s", iun)
                .isNotEmpty();
        sharedSteps.setNotificationIun(iun);
        sharedSteps.getNotificationIunList().add(iun);
        log.info("Ripresa la comunicazione bonaria {} (chiave {})", iun, key);
    }

    /**
     * Senza IUN lo scenario viene saltato, non fallisce.
     */
    private String readAnnotatedIun(String key) {
        String property = RIPRESA_PROPERTY_PREFIX + key;
        String iun = environment.getProperty(property);
        assumeThat(iun)
                .as("Nessuna notifica preparata per la chiave %s (-D%s=<IUN>): scenario saltato", key, property)
                .isNotBlank();
        return iun.trim();
    }
}
