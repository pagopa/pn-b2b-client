package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.tracciaturaIndagine.reader.TimelineDetailsReader;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Step per la verifica della tracciatura dell'indagine in loco del postino e della fonte dell'indirizzo
 * utilizzato per gli invii cartacei (SRS: Tracciatura Indagine in loco e fonte secondo invio).
 */
@Slf4j
public class TracciaturaIndagineInLocoSteps {
    private static final String RECEIVER_ADDRESS_SOURCE_TYPE = "receiverAddressSourceType";
    private static final String HAS_ON_SITE_INVESTIGATION = "hasOnSiteInvestigation";
    private static final Duration POLLING_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration POLLING_INTERVAL = Duration.ofSeconds(15);

    private final SharedSteps sharedSteps;
    private final TimelineDetailsReader timelineDetailsReader;

    public TracciaturaIndagineInLocoSteps(SharedSteps sharedSteps, TimelineDetailsReader timelineDetailsReader) {
        this.sharedSteps = sharedSteps;
        this.timelineDetailsReader = timelineDetailsReader;
    }

    @ParameterType(ElementoCartaceo.REGEX)
    public ElementoCartaceo elementoCartaceo(String descrizione) {
        return ElementoCartaceo.fromDescrizione(descrizione);
    }

    @Then("{elementoCartaceo} del destinatario {int} riporta come fonte dell'indirizzo {string}")
    public void checkAddressSource(ElementoCartaceo elemento, int recIndex, String expectedSource) {
        checkField(elemento, recIndex, RECEIVER_ADDRESS_SOURCE_TYPE, expectedSource);
    }

    @Then("{elementoCartaceo} del destinatario {int} riporta che l'indagine in loco {is} stata effettuata")
    public void checkOnSiteInvestigation(ElementoCartaceo elemento, int recIndex, boolean performed) {
        checkField(elemento, recIndex, HAS_ON_SITE_INVESTIGATION, String.valueOf(performed));
    }

    @Then("{elementoCartaceo} del destinatario {int} non riporta l'indagine in loco")
    public void checkOnSiteInvestigationMissing(ElementoCartaceo elemento, int recIndex) {
        checkField(elemento, recIndex, HAS_ON_SITE_INVESTIGATION, null);
    }

    /**
     * Da usare dopo aver atteso la conclusione del workflow: la verifica è immediata, senza polling.
     */
    @Then("{elementoCartaceo} del destinatario {int} non è presente")
    public void checkNotPresent(ElementoCartaceo elemento, int recIndex) {
        try {
            assertThat(readDetails(elemento, recIndex))
                    .as("%s del destinatario %d non dovrebbe essere presente (%s)", elemento.getDescrizione(), recIndex, elemento.getCategory())
                    .isEmpty();
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private void checkField(ElementoCartaceo elemento, int recIndex, String field, String expectedValue) {
        try {
            Map<String, String> details = awaitDetails(elemento, recIndex);
            log.info("{} del destinatario {}: {}", elemento.getDescrizione(), recIndex, details);
            assertThat(details.get(field))
                    .as("Campo details.%s di %s del destinatario %d (%s)", field, elemento.getDescrizione(), recIndex, elemento.getCategory())
                    .isEqualTo(expectedValue);
        } catch (ConditionTimeoutException timeoutException) {
            sharedSteps.throwAssertionErrorWithIUN(new AssertionError(timeoutException.getMessage(), timeoutException));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Attende che l'elemento sia presente e lo restituisce. L'elemento deve essere unico: per gli elementi senza
     * tentativo questo evita di verificarne uno qualsiasi tra più invii dello stesso destinatario.
     */
    private Map<String, String> awaitDetails(ElementoCartaceo elemento, int recIndex) {
        AtomicReference<List<Map<String, String>>> found = new AtomicReference<>(List.of());
        await().atMost(POLLING_TIMEOUT)
                .pollInterval(POLLING_INTERVAL)
                .untilAsserted(() -> {
                    found.set(readDetails(elemento, recIndex));
                    assertThat(found.get())
                            .as("%s del destinatario %d non trovato (%s)", elemento.getDescrizione(), recIndex, elemento.getCategory())
                            .isNotEmpty();
                });
        assertThat(found.get())
                .as("Atteso un solo elemento %s per il destinatario %d", elemento.getCategory(), recIndex)
                .hasSize(1);
        return found.get().get(0);
    }

    private List<Map<String, String>> readDetails(ElementoCartaceo elemento, int recIndex) {
        return timelineDetailsReader.readDetails(sharedSteps.getNotificationIun(), elemento.getCategory(), recIndex, elemento.getSentAttemptMade());
    }
}
