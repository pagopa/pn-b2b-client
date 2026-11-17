package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.tracciaturaIndagine.reader.TimelineDetailsReader;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TracciaturaIndagineInLocoSteps {
    private static final String RECEIVER_ADDRESS_SOURCE_TYPE = "receiverAddressSourceType";
    private static final String HAS_ON_SITE_INVESTIGATION = "hasOnSiteInvestigation";
    private static final PollingConfig POLLING = PollingConfig.of(Duration.ofMinutes(5), Duration.ofSeconds(15));

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
        checkField(elemento, recIndex, RECEIVER_ADDRESS_SOURCE_TYPE, FonteIndirizzo.fromValue(expectedSource).name());
    }

    /**
     * hasOnSiteInvestigation c'è solo sull'esito del primo tentativo e sul secondo invio.
     */
    @Then("{elementoCartaceo} del destinatario {int} riporta che l'indagine in loco {is} stata effettuata")
    public void checkOnSiteInvestigation(ElementoCartaceo elemento, int recIndex, boolean performed) {
        checkField(elemento, recIndex, HAS_ON_SITE_INVESTIGATION, String.valueOf(performed));
    }

    @Then("{elementoCartaceo} del destinatario {int} non riporta l'indagine in loco")
    public void checkOnSiteInvestigationMissing(ElementoCartaceo elemento, int recIndex) {
        checkField(elemento, recIndex, HAS_ON_SITE_INVESTIGATION, null);
    }

    /**
     * Per gli elementi prodotti prima del rilascio della feature.
     */
    @Then("{elementoCartaceo} del destinatario {int} non riporta la fonte dell'indirizzo")
    public void checkAddressSourceMissing(ElementoCartaceo elemento, int recIndex) {
        checkField(elemento, recIndex, RECEIVER_ADDRESS_SOURCE_TYPE, null);
    }

    @Then("{elementoCartaceo} del destinatario {int} è presente")
    public void checkPresent(ElementoCartaceo elemento, int recIndex) {
        try {
            awaitDetails(elemento, recIndex);
        } catch (PollingTimeoutException timeoutException) {
            sharedSteps.throwAssertionErrorWithIUN(new AssertionError(timeoutException.getMessage(), timeoutException));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Senza polling: da usare dopo la conclusione del workflow.
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
        } catch (PollingTimeoutException timeoutException) {
            sharedSteps.throwAssertionErrorWithIUN(new AssertionError(timeoutException.getMessage(), timeoutException));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * L'elemento deve essere unico, per non verificarne uno qualsiasi tra più invii dello stesso destinatario.
     */
    private Map<String, String> awaitDetails(ElementoCartaceo elemento, int recIndex) {
        List<Map<String, String>> found = Poller.pollUntil(
                () -> readDetails(elemento, recIndex),
                details -> !details.isEmpty(),
                POLLING,
                details -> String.format("%s del destinatario %d non trovato (%s)",
                        elemento.getDescrizione(), recIndex, elemento.getCategory()));
        assertThat(found)
                .as("Atteso un solo elemento %s per il destinatario %d", elemento.getCategory(), recIndex)
                .hasSize(1);
        return found.get(0);
    }

    private List<Map<String, String>> readDetails(ElementoCartaceo elemento, int recIndex) {
        return timelineDetailsReader.readDetails(sharedSteps.getNotificationIun(), elemento.getCategory(), recIndex, elemento.getSentAttemptMade());
    }
}
