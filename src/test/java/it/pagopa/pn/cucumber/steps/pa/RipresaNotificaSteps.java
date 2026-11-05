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
 * Step per i test a cavallo di un rilascio: la notifica viene preparata e fermata prima del rilascio
 * (es. sequence OK_AR_BLOCKED) e ripresa dopo il rilascio per farla proseguire (es. tramite il consolidatore).
 * La ripresa è attiva solo se viene passato lo IUN della notifica preparata, altrimenti lo scenario viene saltato.
 */
@Slf4j
public class RipresaNotificaSteps {
    private static final String RIPRESA_PROPERTY_PREFIX = "pn.ripresa.iun.";

    private final SharedSteps sharedSteps;
    private final Environment environment;
    private final TimelineDetailsReader timelineDetailsReader;

    public RipresaNotificaSteps(SharedSteps sharedSteps, Environment environment, TimelineDetailsReader timelineDetailsReader) {
        this.sharedSteps = sharedSteps;
        this.environment = environment;
        this.timelineDetailsReader = timelineDetailsReader;
    }

    /**
     * Logga lo IUN con il nome della proprietà da valorizzare per riprendere la notifica,
     * es. {@code pn.ripresa.iun.CASO_1=XXXX-XXXX-XXXX-202611-X-1}.
     */
    @Then("si annota lo IUN della notifica con la chiave {string}")
    public void annotateIun(String key) {
        String iun = sharedSteps.getNotificationIun();
        assertThat(iun).as("Nessuna notifica inviata di cui annotare lo IUN").isNotNull();
        log.info("Notifica da riprendere: {}{}={}", RIPRESA_PROPERTY_PREFIX, key, iun);
    }

    /**
     * Riprende una notifica creata in un'esecuzione precedente: imposta la PA mittente e lo IUN di SharedSteps,
     * così gli step successivi (timeline, consolidatore, verifiche) lavorano su quella notifica.
     * Il valore viene letto dalla proprietà {@code pn.ripresa.iun.<chiave>}, valorizzabile con -D o con la variabile
     * d'ambiente {@code PN_RIPRESA_IUN_<CHIAVE>}.
     */
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
     * Riprende una comunicazione bonaria creata in un'esecuzione precedente. Le bonarie non si recuperano con le API
     * delle notifiche legali: si verifica sulla timeline che l'invio cartaceo esista e si imposta lo IUN di SharedSteps,
     * usato dal consolidatore e dalle verifiche sugli elementi cartacei.
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
     * Senza una notifica preparata per la chiave lo scenario viene saltato (non fallisce): i test di ripresa girano
     * nell'esecuzione ordinaria e diventano attivi solo quando si passa lo IUN di una notifica preparata in anticipo.
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
