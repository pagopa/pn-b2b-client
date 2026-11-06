package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.SqsService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Verifica l'esito puntuale (per singolo CF) delle richieste batch verso INIPEC.
 *
 * <p>Il backend interroga INIPEC per recuperare la PEC di una PG priva di domicilio digitale e
 * traccia l'esito per ogni CF sulla tabella DynamoDB {@code pn-batchRequests}, tramite l'indice
 * {@code sendStatus-lastReserved-index} (campi {@code sendStatus} e {@code retry}).</p>
 *
 * <p>Mappatura fra esito di business (come scritto nel feature) e stato persistito su DB:</p>
 * <ul>
 *   <li><b>INVIATA al primo tentativo</b> &rarr; {@code sendStatus = SENT}, {@code retry = 0}</li>
 *   <li><b>RIMESSA in retry</b>           &rarr; {@code sendStatus = NOT_SENT}, {@code retry > 0}</li>
 *   <li><b>Nessuna richiesta INIPEC</b>   &rarr; nessun record, né {@code SENT} né {@code NOT_SENT}</li>
 *   <li><b>In ERROR e inviata in DLQ</b>  &rarr; {@code sendStatus = SENT_TO_DLQ}, {@code status = ERROR} e messaggio
 *       presente su {@code pn-national_registry_gateway_inputs-DLQ}</li>
 * </ul>
 */
@Slf4j
public class INIPECGestionePuntualeEsitiSteps {

    @ParameterType("INIPEC|INAD")
    public Eservice eservice(String value) {
        return Eservice.valueOf(value);
    }

    /**
     * Valori del campo {@code sendStatus} sull'indice usato dalla query su pn-batchRequests.
     */
    private enum SendStatus {
        SENT, NOT_SENT, SENT_TO_DLQ
    }

    public enum Eservice {
        INIPEC, INAD
    }

    private static final String MIN_LAST_RESERVED = "2000-01-01T00:00:00Z";
    private static final String STATUS_ERROR = "ERROR";
    private static final String NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ = "pn-national_registry_gateway_inputs-DLQ";

    private final DynamoDbService dynamoDbService;
    private final SqsService sqsService;
    private final SharedSteps sharedSteps;
    private String notificationSentTimestamp;

    /**
     * Tempo massimo di attesa che il dato venga scritto/aggiornato sul DB.
     */
    private static final Duration DB_POLL_TIMEOUT = Duration.ofMinutes(2);
    /**
     * Intervallo fra un tentativo di lettura e il successivo.
     */
    private static final Duration DB_POLL_INTERVAL = Duration.ofSeconds(5);
    /**
     * Finestra in cui la condizione di assenza deve restare stabile (per evitare check "troppo presto").
     */
    private static final Duration DB_ABSENCE_STABILITY = Duration.ofSeconds(30);
    /**
     * Tempo massimo di attesa per l'invio in DLQ: il servizio deve prima esaurire internamente tutti i retry.
     */
    private static final Duration DLQ_POLL_TIMEOUT = Duration.ofMinutes(10);
    /**
     * Intervallo fra una lettura della DLQ e la successiva.
     */
    private static final Duration DLQ_POLL_INTERVAL = Duration.ofSeconds(30);

    public INIPECGestionePuntualeEsitiSteps(DynamoDbService dynamoDbService, SqsService sqsService, SharedSteps sharedSteps) {
        this.dynamoDbService = dynamoDbService;
        this.sqsService = sqsService;
        this.sharedSteps = sharedSteps;
    }


    @Then("la richiesta INIPEC per il cf {string} risulta INVIATA al primo tentativo")
    public void requestSentWithoutRetry(String cf) {
        // Ripete query + assert finché il dato non è presente su DB come SENT con retry = 0, oppure scade il timeout.
        await().atMost(DB_POLL_TIMEOUT)
                .pollInterval(DB_POLL_INTERVAL)
                .untilAsserted(() -> {
                    List<Map<String, AttributeValue>> items = retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT);

                    assertThat(items)
                            .as("Nessuna richiesta SENT trovata per cf=%s nella finestra temporale", cf)
                            .isNotEmpty();

                    assertThat(items).allSatisfy(item ->
                            assertThat(retryOf(item))
                                    .as("cf=%s: atteso retry=0 (inviata al primo tentativo)", cf)
                                    .isZero());
                });
    }

    @Then("la richiesta INIPEC per il cf {string} risulta RIMESSA in retry")
    public void requestRequeuedForRetry(String cf) {
        // Ripete query + assert finché il dato non è presente su DB come NOT_SENT con retry > 0, oppure scade il timeout.
        await().atMost(DB_POLL_TIMEOUT)
                .pollInterval(DB_POLL_INTERVAL)
                .untilAsserted(() -> {
                    List<Map<String, AttributeValue>> items = retrieveBatchRequestItemsBySendStatus(cf, SendStatus.NOT_SENT);

                    assertThat(items)
                            .as("Nessuna richiesta NOT_SENT trovata per cf=%s nella finestra temporale", cf)
                            .isNotEmpty();

                    assertThat(items).allSatisfy(item ->
                            assertThat(retryOf(item))
                                    .as("cf=%s: atteso retry>0 (richiesta rimessa in coda)", cf)
                                    .isNotZero());
                });
    }


    // Il servizio esegue internamente i retry fino al massimo: il numero di tentativi non viene verificato.
    // Ripete query + assert finché il dato non è presente su DB come SENT_TO_DLQ con status = ERROR, oppure scade il timeout.
    @Then("la richiesta INIPEC per il cf {string} risulta in ERROR e inviata in DLQ")
    public void requestInErrorAndSentToDlq(String cf) {
        List<Map<String, AttributeValue>> dlqItems = new ArrayList<>();
        await().atMost(DLQ_POLL_TIMEOUT)
                .pollInterval(DB_POLL_INTERVAL)
                .untilAsserted(() -> {
                    List<Map<String, AttributeValue>> items = retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT_TO_DLQ);

                    assertThat(items)
                            .as("Nessuna richiesta SENT_TO_DLQ trovata per cf=%s nella finestra temporale", cf)
                            .isNotEmpty();

                    // `status` è parola riservata su DynamoDB: si verifica lato client invece di interrogare lo status-index.
                    assertThat(items).allSatisfy(item ->
                            assertThat(item.containsKey("status") ? item.get("status").s() : null)
                                    .as("cf=%s: atteso status=ERROR (retry esauriti)", cf)
                                    .isEqualTo(STATUS_ERROR));

                    dlqItems.clear();
                    dlqItems.addAll(items);
                });

        // Il messaggio in DLQ viene riconosciuto tramite il correlationId del record (o il CF, se assente).
        List<String> expectedReferences = dlqItems.stream()
                .map(item -> item.containsKey("correlationId") ? item.get("correlationId").s() : cf)
                .distinct()
                .toList();

        await().atMost(DLQ_POLL_TIMEOUT)
                .pollInterval(DLQ_POLL_INTERVAL)
                .untilAsserted(() -> {
                    List<Message> messages = sqsService.receiveMessages(NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ);

                    assertThat(expectedReferences).allSatisfy(reference ->
                            assertThat(messages)
                                    .as("cf=%s: nessun messaggio in %s contenente %s", cf, NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ, reference)
                                    .anyMatch(message -> message.body() != null && message.body().contains(reference)));
                });
    }

    @Then("per i cf {string} e {string} non risulta alcuna richiesta INIPEC")
    public void noRequestForEitherCf(String cf1, String cf2) {
        // È un'asserzione di "assenza": richiediamo che la condizione resti stabile per una finestra
        // (during) entro il timeout, così da non passare per un controllo troppo anticipato
        // (cioè prima che l'eventuale scrittura su DB avvenga).
        await().atMost(DB_POLL_TIMEOUT)
                .pollInterval(DB_POLL_INTERVAL)
                .during(DB_ABSENCE_STABILITY)
                .untilAsserted(() -> {
                    assertThat(countAnySendStatus(cf1))
                            .as("cf=%s: trovate richieste SENT o NOT_SENT, attese zero", cf1)
                            .isZero();

                    assertThat(countAnySendStatus(cf2))
                            .as("cf=%s: trovate richieste SENT o NOT_SENT, attese zero", cf2)
                            .isZero();
                });
    }

    @And("salvo il timestamp corrente")
    public void storeCurrentTimestamp() {
        // Limite inferiore della finestra temporale usato dalla query (lastReserved > questo valore).
        // ATTENZIONE: lo scostamento di 2 ore è un workaround per allineare il fuso orario locale
        // (+02:00) a quello con cui `lastReserved` è salvato su DB; il confronto su DynamoDB è fra
        // stringhe ISO. Mantenuto invariato di proposito (refactor di sola leggibilità).
        notificationSentTimestamp = java.time.OffsetDateTime.now().minusHours(2).minusMinutes(1).toString();
    }

    /**
     * Numero totale di richieste per il CF, sia SENT sia NOT_SENT, nella finestra temporale.
     */
    private int countAnySendStatus(String cf) {
        return retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT).size()
                + retrieveBatchRequestItemsBySendStatus(cf, SendStatus.NOT_SENT).size();
    }

    /**
     * Legge il contatore di retry da un item di pn-batchRequests.
     */
    private static int retryOf(Map<String, AttributeValue> item) {
        return Integer.parseInt(item.get("retry").n());
    }

    private List<Map<String, AttributeValue>> retrieveBatchRequestItemsBySendStatus(String cf, SendStatus sendStatus) {

        // Parametri della query sull'indice sendStatus-lastReserved-index:
        // sendStatus = :v_sendStatus AND lastReserved > :v_lastReserved
        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_sendStatus", AttributeValue.builder().s(sendStatus.name()).build(),
                ":v_lastReserved", AttributeValue.builder().s(notificationSentTimestamp).build()
        );

        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(
                DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS,
                attributeValues
        );

        // La query indicizza su sendStatus/lastReserved: filtriamo qui per CF.
        return items.stream()
                .filter(item -> item.containsKey("cf")
                        && cf.equals(item.get("cf").s()))
                .toList();
    }

    @Then("risulta una richiesta {eservice} per il cf {string}")
    public void verifyRegistryRequestExists(Eservice eservice, String cf) {

        String iun = sharedSteps.getNotificationIun();

        await().atMost(Duration.ofSeconds(30)).pollInterval(DB_POLL_INTERVAL).untilAsserted(() -> {

            List<Map<String, AttributeValue>> items = retrieveRegistryRequests(cf, eservice, iun);
            assertThat(items).as("Nessuna richiesta %s trovata per cf=%s e iun=%s", eservice, cf, iun).isNotEmpty();
        });
    }

    private List<Map<String, AttributeValue>> retrieveRegistryRequests(String cf, Eservice eservice, String iun) {

        Map<String, AttributeValue> attributeValues = Map.of(
                ":v_sendStatus", AttributeValue.builder().s(SendStatus.SENT.name()).build(),
                ":v_lastReserved", AttributeValue.builder().s(MIN_LAST_RESERVED).build()
        );
        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS, attributeValues);

        return items.stream()
                .filter(item -> cf.equals(item.get("cf").s()))
                .filter(item -> eservice.name().equals(item.get("eservice").s()))
                .filter(item -> item.get("correlationId").s().contains(iun))
                .toList();
    }
}

