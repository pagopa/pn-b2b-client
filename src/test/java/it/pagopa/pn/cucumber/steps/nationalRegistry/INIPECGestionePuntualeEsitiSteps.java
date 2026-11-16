package it.pagopa.pn.cucumber.steps.nationalRegistry;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import it.pagopa.pn.client.b2b.pa.domain.DynamoTableName;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.client.b2b.pa.service.SqsService;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.utils.SqsMessageUtils;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

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
 *       (InternalCodeSqsDto con {@code taxId} e prefisso del {@code correlationId}) presente su
 *       {@code pn-national_registry_gateway_inputs-DLQ}</li>
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
     * Attesa che il dato venga scritto/aggiornato sul DB: fino a 2 minuti, una lettura ogni 5 secondi.
     */
    private static final PollingConfig DB_POLLING = PollingConfig.of(Duration.ofMinutes(2), Duration.ofSeconds(5));
    /**
     * Finestra in cui la condizione di assenza deve restare stabile (per evitare check "troppo presto"):
     * 30 secondi più un intervallo, così l'ultima lettura cade a fine finestra.
     */
    private static final PollingConfig DB_ABSENCE_POLLING = PollingConfig.of(Duration.ofSeconds(35), Duration.ofSeconds(5));
    /**
     * Attesa del record SENT_TO_DLQ su DB: il servizio deve prima esaurire internamente tutti i retry.
     */
    private static final PollingConfig DLQ_DB_POLLING = PollingConfig.of(Duration.ofMinutes(10), Duration.ofSeconds(5));
    /**
     * Attesa del messaggio sulla coda DLQ: fino a 10 minuti, una lettura ogni 30 secondi.
     */
    private static final PollingConfig DLQ_QUEUE_POLLING = PollingConfig.of(Duration.ofMinutes(10), Duration.ofSeconds(30));
    /**
     * Attesa della richiesta INIPEC/INAD su DB: fino a 30 secondi, una lettura ogni 5 secondi.
     */
    private static final PollingConfig REGISTRY_REQUEST_POLLING = PollingConfig.of(Duration.ofSeconds(30), Duration.ofSeconds(5));

    public INIPECGestionePuntualeEsitiSteps(DynamoDbService dynamoDbService, SqsService sqsService, SharedSteps sharedSteps) {
        this.dynamoDbService = dynamoDbService;
        this.sqsService = sqsService;
        this.sharedSteps = sharedSteps;
    }


    @Then("la richiesta INIPEC per il cf {string} risulta INVIATA al primo tentativo")
    public void requestSentWithoutRetry(String cf) {
        // Ripete query + assert finché il dato non è presente su DB come SENT con retry = 0, oppure scade il timeout.
        pollUntilVerified(
                () -> retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT),
                items -> {
                    assertThat(items)
                            .as("Nessuna richiesta SENT trovata per cf=%s nella finestra temporale", cf)
                            .isNotEmpty();

                    assertThat(items).allSatisfy(item ->
                            assertThat(retryOf(item))
                                    .as("cf=%s: atteso retry=0 (inviata al primo tentativo)", cf)
                                    .isZero());
                },
                DB_POLLING);
    }

    @Then("la richiesta INIPEC per il cf {string} risulta RIMESSA in retry")
    public void requestRequeuedForRetry(String cf) {
        // Ripete query + assert finché il dato non è presente su DB come NOT_SENT con retry > 0, oppure scade il timeout.
        pollUntilVerified(
                () -> retrieveBatchRequestItemsBySendStatus(cf, SendStatus.NOT_SENT),
                items -> {
                    assertThat(items)
                            .as("Nessuna richiesta NOT_SENT trovata per cf=%s nella finestra temporale", cf)
                            .isNotEmpty();

                    assertThat(items).allSatisfy(item ->
                            assertThat(retryOf(item))
                                    .as("cf=%s: atteso retry>0 (richiesta rimessa in coda)", cf)
                                    .isNotZero());
                },
                DB_POLLING);
    }


    // Il servizio esegue internamente i retry fino al massimo: il numero di tentativi non viene verificato.
    // Ripete query + assert finché il dato non è presente su DB come SENT_TO_DLQ con status = ERROR, oppure scade il timeout.
    @Then("la richiesta INIPEC per il cf {string} risulta in ERROR e inviata in DLQ")
    public void requestInErrorAndSentToDlq(String cf) {
        String iun = sharedSteps.getNotificationIun();
        // Si considerano solo i record della notifica corrente (correlationId contiene lo IUN):
        // la finestra temporale è ampia e potrebbe includere record SENT_TO_DLQ di run precedenti con lo stesso CF.
        List<Map<String, AttributeValue>> dlqItems = pollUntilVerified(
                () -> retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT_TO_DLQ).stream()
                        .filter(item -> item.containsKey("correlationId")
                                && item.get("correlationId").s().contains(iun))
                        .toList(),
                items -> {
                    assertThat(items)
                            .as("Nessuna richiesta SENT_TO_DLQ trovata per cf=%s e iun=%s nella finestra temporale", cf, iun)
                            .isNotEmpty();

                    // `status` è parola riservata su DynamoDB: si verifica lato client invece di interrogare lo status-index.
                    assertThat(items).allSatisfy(item ->
                            assertThat(item.containsKey("status") ? item.get("status").s() : null)
                                    .as("cf=%s: atteso status=ERROR (retry esauriti)", cf)
                                    .isEqualTo(STATUS_ERROR));
                },
                DLQ_DB_POLLING);

        List<String> expectedCorrelationIds = dlqItems.stream()
                .map(item -> item.get("correlationId").s())
                .distinct()
                .toList();

        // Il messaggio in DLQ (InternalCodeSqsDto) viene riconosciuto tramite taxId + prefisso del correlationId del record
        // (già filtrato per IUN): così si escludono messaggi residui di run precedenti con lo stesso CF.
        pollUntilVerified(
                () -> sqsService.receiveMessages(NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ),
                (List<Message> messages) ->
                        assertThat(expectedCorrelationIds).allSatisfy(correlationId ->
                                assertThat(messages)
                                        .as("cf=%s: nessun messaggio in %s per correlationId=%s", cf, NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ, correlationId)
                                        .anyMatch(message -> SqsMessageUtils.isNationalRegistryDlqMessageFor(message, cf, correlationId))),
                DLQ_QUEUE_POLLING);
    }

    @Then("per i cf {string} e {string} non risulta alcuna richiesta INIPEC")
    public void noRequestForEitherCf(String cf1, String cf2) {
        // È un'asserzione di "assenza": la condizione deve restare stabile per tutta la finestra, così da non
        // passare per un controllo troppo anticipato (cioè prima che l'eventuale scrittura su DB avvenga).
        // Il polling si interrompe in anticipo solo se compare un record; lo scadere del timeout è l'esito atteso.
        // List e non Map: Map.of fallirebbe se cf1 e cf2 coincidessero.
        Supplier<List<Integer>> counts = () -> List.of(countAnySendStatus(cf1), countAnySendStatus(cf2));
        List<Integer> lastCounts;
        try {
            lastCounts = Poller.pollUntil(counts, c -> c.stream().anyMatch(n -> n > 0), DB_ABSENCE_POLLING);
        } catch (PollingTimeoutException e) {
            @SuppressWarnings("unchecked")
            List<Integer> lastValue = (List<Integer>) e.getLastValue();
            lastCounts = lastValue;
        }

        assertThat(lastCounts.get(0))
                .as("cf=%s: trovate richieste SENT o NOT_SENT, attese zero", cf1)
                .isZero();

        assertThat(lastCounts.get(1))
                .as("cf=%s: trovate richieste SENT o NOT_SENT, attese zero", cf2)
                .isZero();
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

        pollUntilVerified(
                () -> retrieveRegistryRequests(cf, eservice, iun),
                items -> assertThat(items).as("Nessuna richiesta %s trovata per cf=%s e iun=%s", eservice, cf, iun).isNotEmpty(),
                REGISTRY_REQUEST_POLLING);
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

    /**
     * Ripete {@code call} finché le asserzioni di {@code verify} non passano, oppure scade {@code config}.
     * Allo scadere le asserzioni vengono rieseguite sull'ultimo valore letto, così il test fallisce
     * con il messaggio AssertJ originale.
     *
     * @return l'ultimo valore letto (quello che ha soddisfatto le asserzioni)
     */
    private static <T> T pollUntilVerified(Supplier<T> call, Consumer<T> verify, PollingConfig config) {
        Predicate<T> verified = value -> {
            try {
                verify.accept(value);
                return true;
            } catch (AssertionError e) {
                return false;
            }
        };

        T lastValue;
        try {
            lastValue = Poller.pollUntil(call, verified, config);
        } catch (PollingTimeoutException e) {
            @SuppressWarnings("unchecked")
            T timedOutValue = (T) e.getLastValue();
            lastValue = timedOutValue;
        }
        verify.accept(lastValue);
        return lastValue;
    }
}

