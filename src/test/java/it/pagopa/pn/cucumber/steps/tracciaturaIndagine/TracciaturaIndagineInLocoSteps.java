package it.pagopa.pn.cucumber.steps.tracciaturaIndagine;

import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.awaitility.Awaitility.await;

/**
 * Step per la verifica della tracciatura dell'indagine in loco del postino e della fonte dell'indirizzo
 * utilizzato per gli invii cartacei (SRS: Tracciatura Indagine in loco e fonte secondo invio).
 * <p>
 * I campi receiverAddressSourceType e hasOnSiteInvestigation vengono letti direttamente dalla timeline su DynamoDB (pn-Timelines),
 * cosi' da rendere la verifica indipendente dalla versione delle API B2B che li espone.
 */
@Slf4j
public class TracciaturaIndagineInLocoSteps {
    private static final String NULL_VALUE = "NULL";
    private static final String DETAILS = "details";
    private static final String REC_INDEX = "recIndex";
    private static final String SENT_ATTEMPT_MADE = "sentAttemptMade";
    private static final Duration POLLING_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration POLLING_INTERVAL = Duration.ofSeconds(15);

    private final SharedSteps sharedSteps;
    private final DynamoDbService dynamoDbService;

    public TracciaturaIndagineInLocoSteps(SharedSteps sharedSteps, DynamoDbService dynamoDbService) {
        this.sharedSteps = sharedSteps;
        this.dynamoDbService = dynamoDbService;
    }

    /**
     * Attende che l'elemento di timeline del destinatario e del tentativo indicati sia presente su DynamoDB
     * e ne verifica i campi dei details. Il valore NULL indica che il campo non deve essere valorizzato.
     */
    @Then("si verifica che l'elemento di timeline {string} del destinatario {int} al tentativo {int} riporti")
    public void checkTimelineElementDetails(String category, int recIndex, int sentAttemptMade, Map<String, String> expectedDetails) {
        try {
            AtomicReference<Map<String, AttributeValue>> details = new AtomicReference<>();
            await().atMost(POLLING_TIMEOUT)
                    .pollInterval(POLLING_INTERVAL)
                    .untilAsserted(() -> {
                        findDetails(category, recIndex, sentAttemptMade).ifPresent(details::set);
                        assertThat(details.get())
                                .as("Nessun elemento %s trovato per il destinatario %d al tentativo %d", category, recIndex, sentAttemptMade)
                                .isNotNull();
                    });
            log.info("{} destinatario {} tentativo {}: {}", category, recIndex, sentAttemptMade, details.get());
            assertSoftly(softly -> expectedDetails.forEach((field, expectedValue) ->
                    softly.assertThat(readValue(details.get().get(field)))
                            .as("Valore del campo details.%s dell'elemento %s al tentativo %d", field, category, sentAttemptMade)
                            .isEqualTo(NULL_VALUE.equalsIgnoreCase(expectedValue) ? null : expectedValue)));
        } catch (ConditionTimeoutException timeoutException) {
            sharedSteps.throwAssertionErrorWithIUN(new AssertionError(timeoutException.getMessage(), timeoutException));
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Verifica che l'elemento di timeline del destinatario e del tentativo indicati non sia presente su DynamoDB.
     * Da usare dopo aver atteso la conclusione del workflow.
     */
    @Then("si verifica che l'elemento di timeline {string} del destinatario {int} al tentativo {int} non esista")
    public void checkTimelineElementNotExists(String category, int recIndex, int sentAttemptMade) {
        try {
            assertThat(findDetails(category, recIndex, sentAttemptMade))
                    .as("L'elemento %s non dovrebbe esistere per il destinatario %d al tentativo %d", category, recIndex, sentAttemptMade)
                    .isEmpty();
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    private Optional<Map<String, AttributeValue>> findDetails(String category, int recIndex, int sentAttemptMade) {
        List<Map<String, AttributeValue>> items = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_category", AttributeValue.builder().s(category).build()
        )).items();
        return items.stream()
                .map(item -> item.get(DETAILS))
                .filter(details -> details != null && details.hasM())
                .map(AttributeValue::m)
                .filter(details -> String.valueOf(recIndex).equals(readValue(details.get(REC_INDEX))))
                .filter(details -> String.valueOf(sentAttemptMade).equals(readValue(details.get(SENT_ATTEMPT_MADE))))
                .findFirst();
    }

    private static String readValue(AttributeValue value) {
        if (value == null || Boolean.TRUE.equals(value.nul())) {
            return null;
        }
        if (value.s() != null) {
            return value.s();
        }
        if (value.n() != null) {
            return value.n();
        }
        if (value.bool() != null) {
            return String.valueOf(value.bool());
        }
        return value.toString();
    }
}
