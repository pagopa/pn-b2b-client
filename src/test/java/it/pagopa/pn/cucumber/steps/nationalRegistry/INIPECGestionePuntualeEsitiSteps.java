package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Duration;
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
 * </ul>
 */
@Slf4j
public class INIPECGestionePuntualeEsitiSteps {

    /** Valori del campo {@code sendStatus} sull'indice usato dalla query su pn-batchRequests. */
    private enum SendStatus {
        SENT, NOT_SENT
    }

    private final DynamoDbService dynamoDbService;
    private String notificationSentTimestamp;

    /** Tempo massimo di attesa che il dato venga scritto/aggiornato sul DB. */
    private static final Duration DB_POLL_TIMEOUT = Duration.ofMinutes(2);
    /** Intervallo fra un tentativo di lettura e il successivo. */
    private static final Duration DB_POLL_INTERVAL = Duration.ofSeconds(5);
    /** Finestra in cui la condizione di assenza deve restare stabile (per evitare check "troppo presto"). */
    private static final Duration DB_ABSENCE_STABILITY = Duration.ofSeconds(30);

    public INIPECGestionePuntualeEsitiSteps(DynamoDbService dynamoDbService) {
        this.dynamoDbService = dynamoDbService;
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

    /** Numero totale di richieste per il CF, sia SENT sia NOT_SENT, nella finestra temporale. */
    private int countAnySendStatus(String cf) {
        return retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT).size()
                + retrieveBatchRequestItemsBySendStatus(cf, SendStatus.NOT_SENT).size();
    }

    /** Legge il contatore di retry da un item di pn-batchRequests. */
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

}
