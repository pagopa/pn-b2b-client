package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.common.util.CloudWatchQueryBuilder;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class AwsServiceSteps {
    private final SharedSteps sharedSteps;
    private final CloudWatchLogsClient cloudWatchLogsClient;
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
     * Il nome delle chiavi impostato nel file feature è del tutto irrilevante, serve unicamente a migliorare la leggibilità.
     * La sola eccezione è il campo iun (ignoreCase): se è impostato su auto, viene recuperato dal valore di notificationIun;
     * in tutti gli altri casi, viene impostato col valore passato.
     * La scrittura dei log su CloudWatch è repentina, ma possono tuttavia capitare casi in cui il log viene scritto pochi secondi dopo
     * rispetto a quando viene eseguita la ricerca. Per evitare fail dei test dovuti a questa motivazione, il metodo riesegue la ricerca
     * tramite polling prima di dichiarare fallito il test.
     */
    @And("verifico la presenza di un audit log su {string} negli ultimi {int} minuti riportante i seguenti dati nel messaggio")
    public void checkAuditLogFromAws(String microservice, int minutes, Map<String, String> queryFiltersMap) {
        if (!checkAuditLogDisabled) {
            try {
                StringBuilder sb = new StringBuilder();
                queryFiltersMap.forEach((key, value) -> {
                    if (key.equalsIgnoreCase("IUN")) {
                        sb.append("\"").append(value.equals("auto") ? sharedSteps.getNotificationIun() : value).append("\" ");
                    } else {
                        sb.append("\"").append(value).append("\" ");
                    }
                });
                String search = sb.toString().trim();

                await().atMost(2, TimeUnit.MINUTES).pollInterval(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
                    FilterLogEventsRequest logRequest = CloudWatchQueryBuilder.search(microservice, search, minutes);
                    FilterLogEventsResponse logResponse = cloudWatchLogsClient.filterLogEvents(logRequest);
                    assertThat(logResponse.events().size()).as("Alle %s non è stato trovato nessun log che soddisfi la search %s", DateTime.now(), search).isGreaterThan(0);
                });
            } catch (AssertionError assertionError) {
                sharedSteps.throwAssertionErrorWithIUN(assertionError);
            }
        }
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
}
