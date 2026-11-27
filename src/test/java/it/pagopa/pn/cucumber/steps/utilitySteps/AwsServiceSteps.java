package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.util.CloudWatchLogsService;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class AwsServiceSteps {
    private final SharedSteps sharedSteps;
    private final CloudWatchLogsService cloudWatchLogsService;
    private boolean checkAuditLogDisabled;
    private final DynamoDbService dynamoDbService;

    /**
     * Ricerca uno specifico elemento su pn-timelines e verifica la sua presenza (o meno)
     */
    @Then("verifico che su DynamoDB {is} presente in timeline l'elemento {string}")
    public void checkTimelineFromDynamoDB(boolean isPresent, String timelineElement) {
        QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
                ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
                ":v_category", AttributeValue.builder().s(timelineElement).build()
        ));
        log.info("Elementi trovati con categoria {}: {}", timelineElement, queryResponse.count());
        try {
            if (isPresent) {
                assertThat(queryResponse.items().size()).as("La response non contiene nessun elemento con category " + timelineElement).isGreaterThan(0);

                for (int i = 0; i < queryResponse.items().size(); i++) {
                    Map<String, AttributeValue> item = queryResponse.items().get(i);
                    log.info("--- ELEMENTO TIMELINE {} ---", i + 1);
                    item.forEach((key, value) -> {
                        Object val = (value.s() != null) ? value.s() :
                                (value.n() != null) ? value.n() :
                                        (value.bool() != null) ? value.bool() : value.toString();
                        log.info("{}: {}", key, val);
                    });
                }
            } else {
                assertThat(queryResponse.items().size()).as("La response non deve contenere nessun elemento con category " + timelineElement).isEqualTo(0);
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /**
     * Dalla mappa di parametri passata in input si va a comporre la stringa con i filtri di ricerca per i log.
     * Il nome delle chiavi impostato nel file feature è del tutto irrilevante, serve unicamente a migliorare la leggibilità,
     * con due eccezioni (ignoreCase):
     * <ul>
     *     <li>{@code iun}: se il valore è {@code auto}, viene recuperato da {@code notificationIun} invece di essere
     *     preso alla lettera;</li>
     *     <li>{@code exclude}: il valore (una o più frasi separate da virgola) non va a comporre il filtro per
     *     inclusione ma per esclusione, tramite l'operatore {@code -} dei filter pattern CloudWatch (es. per
     *     verificare l'assenza di un campo, passando il nome del campo come frase da escludere).</li>
     * </ul>
     * La scrittura dei log su CloudWatch è repentina, ma possono tuttavia capitare casi in cui il log viene scritto pochi secondi dopo
     * rispetto a quando viene eseguita la ricerca. Per evitare fail dei test dovuti a questa motivazione, il metodo riesegue la ricerca
     * tramite polling prima di dichiarare fallito il test.
     */
    @And("verifico la presenza di un audit log su {string} negli ultimi {int} minuti riportante i seguenti dati nel messaggio")
    public void checkAuditLogFromAws(String microservice, int minutes, Map<String, String> queryFiltersMap) {
        if (!checkAuditLogDisabled) {
            try {
                List<String> includeTerms = new ArrayList<>();
                List<String> excludeTerms = new ArrayList<>();
                queryFiltersMap.forEach((key, value) -> {
                    if (key.equalsIgnoreCase("exclude")) {
                        excludeTerms.addAll(splitTerms(value));
                    } else if (key.equalsIgnoreCase("IUN") && value.equals("auto")) {
                        includeTerms.add(sharedSteps.getNotificationIun());
                    } else {
                        includeTerms.add(value);
                    }
                });

                Poller.pollUntil(
                        () -> countMatchingEventsIgnoringTransientErrors(microservice, includeTerms, excludeTerms, minutes),
                        matchCount -> {
                            assertThat(matchCount).as("Alle %s non è stato trovato nessun log che soddisfi la search %s (esclusi: %s)",
                                    DateTime.now(), includeTerms, excludeTerms).isGreaterThan(0);
                        },
                        PollingConfig.of(Duration.ofMinutes(2), Duration.ofSeconds(10)));
            } catch (AssertionError assertionError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionError);
            }
        }
    }

    private static List<String> splitTerms(String value) {
        return Arrays.stream(value.split(",")).map(String::trim).filter(term -> !term.isEmpty()).toList();
    }

    /**
     * Metodo a soli scopi di debugging per evitare che vengano eseguiti i controlli sugli audit con tempistiche obsolete
     * (da usare solitamente in combo con il metodo che imposta lo iun e la PA di SharedSteps)
     */
    @Given("vengono disabilitati i check sugli audit log")
    public void disableAuditLogCheck() {
        checkAuditLogDisabled = true;
    }

    @Then("verifico che su DynamoDB {is} presente l'elemento {string} con errorCode {string} nella tabella paperRequestError al tentativo {int}")
    public void checkPaperErrorInDynamoDB(boolean isPresent, String timelineElement, String errorCode, int attempt) {
        String requestId = String.format("%s.IUN_%s.RECINDEX_0.ATTEMPT_%d", timelineElement, sharedSteps.getNotificationIun(), attempt);
        QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.PAPER_REQUEST_ERROR, Map.of(
                ":v_requestId", AttributeValue.builder().s(requestId).build()
        ));
        log.info("Elementi trovati con requestId {}: {}", requestId, queryResponse.count());
        try {
            if (isPresent) {
                assertThat(queryResponse.items().size()).as("La response non contiene nessun elemento con category " + timelineElement).isGreaterThan(0);
                assertThat(queryResponse.items())
                        .anyMatch(item ->
                                item.containsKey("error")
                                        && item.get("error").s().contains(errorCode)
                        );
                for (int i = 0; i < queryResponse.items().size(); i++) {
                    Map<String, AttributeValue> item = queryResponse.items().get(i);
                    log.info("--- ELEMENTO TIMELINE {} ---", i + 1);
                    item.forEach((key, value) -> {
                        Object val = (value.s() != null) ? value.s() :
                                (value.n() != null) ? value.n() :
                                        (value.bool() != null) ? value.bool() : value.toString();
                        log.info("{}: {}", key, val);
                    });
                }
            } else {
                assertThat(queryResponse.items().size()).as("La response non deve contenere nessun elemento con category " + timelineElement).isEqualTo(0);
            }
        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    @Then("si verifica che su DynamoDB {is} presente l'elemento {string} nella tabella pn-PaperTrackings con statusCodes {string}")
    public void checkPaperTrackingsInDynamoDB(
            boolean isPresent,
            String element,
            String statusCodes) {

        String trackingId = String.format(
                "%s.IUN_%s.RECINDEX_0.ATTEMPT_0.PCRETRY_0",
                element,
                sharedSteps.getNotificationIun()
        );

        List<String> expectedStatusCodes = Arrays.stream(statusCodes.split(","))
                .map(String::trim)
                .filter(statusCode -> !statusCode.isEmpty())
                .toList();

        QueryResponse queryResponse = dynamoDbService.call(
                DynamoTableName.PAPER_TRACKINGS,
                Map.of(
                        ":v_trackingId",
                        AttributeValue.builder()
                                .s(trackingId)
                                .build()
                )
        );

        log.info(
                "Elementi trovati con trackingId {}: {}",
                trackingId,
                queryResponse.count()
        );

        try {
            if (isPresent) {

                assertThat(queryResponse.items())
                        .as("La response non contiene nessun elemento per trackingId %s", trackingId)
                        .isNotEmpty();

                List<String> foundStatusCodes = queryResponse.items().stream()
                        .map(item -> item.get("events"))
                        .filter(Objects::nonNull)
                        .filter(AttributeValue::hasL)
                        .flatMap(events -> events.l().stream())
                        .filter(AttributeValue::hasM)
                        .map(AttributeValue::m)
                        .map(event -> event.get("statusCode"))
                        .filter(Objects::nonNull)
                        .map(AttributeValue::s)
                        .filter(Objects::nonNull)
                        .toList();

                log.info("StatusCode attesi: {}", expectedStatusCodes);
                log.info("StatusCode trovati: {}", foundStatusCodes);

                assertThat(foundStatusCodes)
                        .as(
                                "Non sono stati trovati tutti gli statusCode attesi. Attesi: %s - Trovati: %s",
                                expectedStatusCodes,
                                foundStatusCodes
                        )
                        .containsAll(expectedStatusCodes);

            } else {

                assertThat(queryResponse.items())
                        .as("La response non deve contenere elementi per trackingId %s", trackingId)
                        .isEmpty();
            }

        } catch (AssertionError assertionError) {
            sharedSteps.throwAssertionErrorWithIUN(assertionError);
        }
    }

    /** Ignora errori transitori di CloudWatch (es. throttling) riprovando al giro di polling successivo, invece di far fallire subito il check. */
    private int countMatchingEventsIgnoringTransientErrors(String microservice, List<String> includeTerms, List<String> excludeTerms, int minutes) {
        try {
            return cloudWatchLogsService.countMatchingEvents(microservice, includeTerms, excludeTerms, minutes);
        } catch (RuntimeException e) {
            log.warn("Errore transitorio durante la ricerca su CloudWatch, nuovo tentativo al prossimo giro di polling", e);
            return 0;
        }
    }

    private Object extractValue(AttributeValue value) {
        if (value.s() != null)
            return value.s();
        else if (value.n() != null)
            return value.n();
        else if (value.bool() != null)
            return value.bool();
        else
            return value.toString();
    }
}
