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
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.sqs.model.Message;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
 *   <li><b>Con polling in ERROR e inviata in DLQ</b> &rarr; campo {@code batchId} letto dal record su
 *       {@code pn-batchRequests}, record su {@code pn-batchPolling} per quel {@code batchId} con {@code status = ERROR}, {@code retry = 3}
 *       e poi messaggio su {@code pn-national_registry_gateway_inputs-DLQ}</li>
 * </ul>
 */
@Slf4j
public class INIPECGestionePuntualeEsitiSteps {

    @ParameterType("INIPEC|INAD")
    public Eservice eservice(String value) {
        return Eservice.valueOf(value);
    }

    @ParameterType("NOT_WORKED|WORKING|WORKED|ERROR")
    public RequestStatus requestStatus(String value) {
        return RequestStatus.valueOf(value);
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

    private enum RequestStatus {
        NOT_WORKED, WORKING, WORKED, ERROR
    }

    private static final String MIN_LAST_RESERVED = "2000-01-01T00:00:00Z";
    private static final String STATUS_ERROR = "ERROR";
    private static final String NO_BATCH_ID = "NO_BATCH_ID";
    /**
     * Numero di retry del polling atteso su pn-batchPolling quando i tentativi sono esauriti.
     */
    private static final int MAX_POLLING_RETRY = 3;
    private static final String NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ = "pn-national_registry_gateway_inputs-DLQ";

    private final DynamoDbService dynamoDbService;
    private final SqsService sqsService;
    private final SharedSteps sharedSteps;
    private String notificationSentTimestamp;
    private static final String MIN_ATTEMPT_AFTER = "2000-01-01T00:00:00Z";

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
    private static final PollingConfig DLQ_DB_POLLING = PollingConfig.of(Duration.ofMinutes(10), Duration.ofSeconds(30));
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

    // Le query restituiscono la lista grezza: Poller ritenta solo sulle AssertionError del Consumer,
    // quindi tutte le asserzioni (anche quelle sulla cardinalità) vanno nel Consumer, non nel Supplier.
    private List<Map<String, AttributeValue>> retrieveNationalRegistryBatchesByBatchId(String batchId) {

        Map<String, AttributeValue> attributes = Map.of(":v_batchId", AttributeValue.builder().s(batchId).build());
        QueryResponse response = dynamoDbService.call(DynamoTableName.NATIONAL_REGISTRIES_BATCHES, attributes);
        return response.items();
    }

    @Then("la risposta sul registro {eservice} per il cf {string} non contiene recapiti")
    public void verifyResponseWithoutAddresses(Eservice eservice, String cf) {

        Poller.pollUntil(
                () -> retrieveNationalRegistryRequests(eservice),
                items -> {
                    Map<String, AttributeValue> request = assertSingleRequest(items, eservice, cf);
                    assertThat(request.get("status").s()).as("Stato atteso").isEqualTo(RequestStatus.WORKED.name());

                    String resultPayload = request.containsKey("resultPayload") ? request.get("resultPayload").s() : null;

                    if (eservice == Eservice.INAD) {
                        assertThat(resultPayload).isNotBlank();
                        assertThat(resultPayload).contains("\"digitalAddress\":[]");
                    } else if (eservice == Eservice.INIPEC) {
                        assertThat(resultPayload).isBlank();
                    }
                },
                DB_POLLING);
    }

    private List<Map<String, AttributeValue>> retrieveNationalRegistryRequests(Eservice eservice) {

        String correlationId = sharedSteps.getCorrelationId();
        return retrieveNationalRegistryRequestsByCorrelationId(correlationId).stream()
                .filter(item -> item.containsKey("registry"))
                .filter(item -> eservice.name().equals(item.get("registry").s()))
                .toList();
    }

    private Map<String, AttributeValue> assertSingleRequest(List<Map<String, AttributeValue>> items, Eservice eservice, String cf) {

        assertThat(items).as("Attesa una sola request %s per correlationId=%s", eservice, sharedSteps.getCorrelationId()).hasSize(1);
        Map<String, AttributeValue> request = items.get(0);
        assertThat(request.get("taxId").s()).as("CF atteso").isEqualTo(cf);
        assertThat(request.get("registry").s()).as("Registro atteso").isEqualTo(eservice.name());
        return request;
    }

    /**
     * Attende la request con batchId valorizzato, poi il relativo batch, su cui applica {@code batchAssertions}.
     */
    private void verifyBatchOf(Eservice eservice, String cf, Consumer<Map<String, AttributeValue>> batchAssertions) {

        // 1. Request con batchId valorizzato (il placeholder NO_BATCH_ID non conta).
        List<Map<String, AttributeValue>> requests = Poller.pollUntil(
                () -> retrieveNationalRegistryRequests(eservice),
                items -> {
                    Map<String, AttributeValue> request = assertSingleRequest(items, eservice, cf);
                    assertThat(hasBatchId(request)).as("cf=%s: batchId non ancora valorizzato", cf).isTrue();
                },
                DB_POLLING);
        String batchId = requests.get(0).get("batchId").s();

        // 2. Batch associato, con le asserzioni specifiche dello step.
        Poller.pollUntil(
                () -> retrieveNationalRegistryBatchesByBatchId(batchId),
                batches -> {
                    assertThat(batches).as("Nessun batch trovato per batchId=%s", batchId).hasSize(1);
                    batchAssertions.accept(batches.get(0));
                },
                DB_POLLING);
    }

    private List<Map<String, AttributeValue>> retrieveNationalRegistryRequestsByCorrelationId(String correlationId) {

        Map<String, AttributeValue> attributes = Map.of(":v_correlationId", AttributeValue.builder().s(correlationId).build());
        QueryResponse response = dynamoDbService.call(DynamoTableName.NATIONAL_REGISTRIES_REQUESTS, attributes);
        return response.items();
    }

    @Then("la richiesta verso il servizio {eservice} per il cf {string} risulta in errore")
    public void verifyRequestInError(Eservice eservice, String cf) {
        verifyRequestStatus(eservice, cf, RequestStatus.ERROR);
    }

    @Then("il batch associato alla richiesta verso {eservice} per il cf {string} risulta in errore")
    public void verifyBatchInError(Eservice eservice, String cf) {

        verifyBatchOf(eservice, cf, batch -> {
            assertThat(batch.get("status").s()).as("Il batch deve risultare in errore").isEqualTo(STATUS_ERROR);
        });
    }

    @Then("il ritentativo della chiamata verso {eservice} per il cf {string} risulta correttamente registrato")
    public void verifyBatchInRetry(Eservice eservice, String cf) {

        verifyBatchOf(eservice, cf, batch -> {
            assertThat(batch.get("status").s()).as("Il batch deve risultare nuovamente pianificato").isEqualTo("REQUEST_SENT");
            assertThat(batch.get("registry").s()).as("Registro atteso").isEqualTo(eservice.name());
            int errorRetryCount = getNumber(batch, "errorRetryCount");
            int inProgressRetryCount = getNumber(batch, "inProgressRetryCount");
            assertThat(errorRetryCount > 0 || inProgressRetryCount > 0).as("Atteso almeno un retry registrato sul batch").isTrue();
        });
    }

    private static int getNumber(Map<String, AttributeValue> item, String field) {
        AttributeValue value = item.get(field);
        if (value == null) {
            return 0;
        }
        String number = value.n();
        if (number == null || number.isBlank()) {
            return 0;
        }
        return Integer.parseInt(number);
    }

    @Then("la richiesta verso il servizio {eservice} per il cf {string} risulta in stato {requestStatus}")
    public void verifyRequestStatus(Eservice eservice, String cf, RequestStatus expectedStatus) {

        Poller.pollUntil(
                () -> retrieveNationalRegistryRequests(eservice),
                items -> {
                    Map<String, AttributeValue> request = assertSingleRequest(items, eservice, cf);
                    assertThat(request.get("status").s()).as("Stato atteso").isEqualTo(expectedStatus.name());
                },
                DB_POLLING);
    }

    @Then("il batch associato alla richiesta verso {eservice} per il cf {string} risulta in stato {requestStatus}")
    public void verifyBatchStatus(Eservice eservice, String cf, RequestStatus expectedStatus) {

        verifyBatchOf(eservice, cf, batch -> {
            assertThat(batch.get("registry").s()).as("Registro atteso").isEqualTo(eservice.name());
            assertThat(batch.get("status").s()).as("Stato atteso").isEqualTo(expectedStatus.name());
        });
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Then("la richiesta INIPEC per il cf {string} risulta INVIATA al primo tentativo")
    public void requestSentWithoutRetry(String cf) {
        // Ripete query + assert finché il dato non è presente su DB come SENT con retry = 0, oppure scade il timeout.
        Poller.pollUntil(
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
        Poller.pollUntil(
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
        List<Map<String, AttributeValue>> dlqItems = Poller.pollUntil(
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

        assertDlqMessagesFor(cf, expectedCorrelationIds);
    }

    // Fallimento sul polling dell'esito: il batchId (campo del record su pn-batchRequests) identifica il polling
    // su pn-batchPolling; solo quando questo è in ERROR (retry del polling esauriti) si cerca il messaggio in DLQ.
    @Then("la richiesta INIPEC per il cf {string} risulta con polling in ERROR e inviata in DLQ")
    public void requestPollingInErrorAndSentToDlq(String cf) {
        String iun = sharedSteps.getNotificationIun();

        // 1. Record della richiesta su pn-batchRequests: il sendStatus in questa fase non è noto, quindi si interrogano
        //    tutti i valori. Filtro su IUN per escludere record di run precedenti con lo stesso CF.
        List<Map<String, AttributeValue>> requestItems = Poller.pollUntil(
                () -> Arrays.stream(SendStatus.values())
                        .flatMap(sendStatus -> retrieveBatchRequestItemsBySendStatus(cf, sendStatus).stream())
                        .filter(item -> item.containsKey("correlationId")
                                && item.get("correlationId").s().contains(iun))
                        .filter(INIPECGestionePuntualeEsitiSteps::hasBatchId)
                        .toList(),
                // Lambda a blocco: un'espressione sarebbe ambigua fra gli overload Predicate/Consumer di Poller.pollUntil.
                items -> {
                    assertThat(items)
                            .as("Nessuna richiesta con batchId valorizzato trovata per cf=%s e iun=%s", cf, iun)
                            .isNotEmpty();
                },
                DLQ_DB_POLLING);

        List<String> batchIds = requestItems.stream()
                .map(item -> item.get("batchId").s())
                .distinct()
                .toList();
        List<String> correlationIds = requestItems.stream()
                .map(item -> item.get("correlationId").s())
                .distinct()
                .toList();

        // 2. Polling su pn-batchPolling per batchId, finché lo status non è ERROR.
        Poller.pollUntil(
                () -> batchIds.stream()
                        .collect(Collectors.toMap(
                                batchId -> batchId,
                                batchId -> dynamoDbService.callAllPages(DynamoTableName.BATCH_POLLING,
                                        Map.of(":v_batchId", AttributeValue.builder().s(batchId).build())))),
                (Map<String, List<Map<String, AttributeValue>>> pollingByBatchId) -> {
                    assertThat(pollingByBatchId).allSatisfy((batchId, pollingItems) -> {
                        assertThat(pollingItems)
                                .as("cf=%s: nessun record su pn-batchPolling per batchId=%s", cf, batchId)
                                .isNotEmpty();
                        assertThat(pollingItems).allSatisfy(item -> {
                            assertThat(item.containsKey("status") ? item.get("status").s() : null)
                                    .as("cf=%s: atteso status=ERROR su pn-batchPolling per batchId=%s", cf, batchId)
                                    .isEqualTo(STATUS_ERROR);
                            assertThat(item.containsKey("retry") ? retryOf(item) : null)
                                    .as("cf=%s: atteso retry=%s su pn-batchPolling per batchId=%s", cf, MAX_POLLING_RETRY, batchId)
                                    .isEqualTo(MAX_POLLING_RETRY);
                        });
                    });
                },
                DLQ_DB_POLLING);

        // 3. Messaggio in DLQ.
        assertDlqMessagesFor(cf, correlationIds);
    }

    /**
     * Il batchId è valorizzato solo dopo la creazione del batch: prima il backend usa il placeholder {@code NO_BATCH_ID}.
     */
    private static boolean hasBatchId(Map<String, AttributeValue> item) {
        return item.containsKey("batchId")
                && item.get("batchId").s() != null
                && !item.get("batchId").s().isEmpty()
                && !NO_BATCH_ID.equals(item.get("batchId").s());
    }

    private void assertDlqMessagesFor(String cf, List<String> expectedCorrelationIds) {
        // Il messaggio in DLQ (InternalCodeSqsDto) viene riconosciuto tramite taxId + prefisso del correlationId del record
        // (già filtrato per IUN): così si escludono messaggi residui di run precedenti con lo stesso CF.
        // Il servizio pubblica correlationId.split(batchRequestPkSeparator)[0], quindi quello del messaggio è un prefisso di quello su DB.
        Poller.pollUntil(
                () -> sqsService.receiveMessages(NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ),
                (List<Message> messages) -> {
                    assertThat(expectedCorrelationIds).allSatisfy(correlationId ->
                            assertThat(messages)
                                    .as("cf=%s: nessun messaggio in %s per correlationId=%s", cf, NATIONAL_REGISTRY_GATEWAY_INPUTS_DLQ, correlationId)
                                    .anyMatch(message -> SqsMessageUtils.hasTaxId(message, cf)
                                            && SqsMessageUtils.fieldMatches(message, "correlationId",
                                                    id -> !id.isEmpty() && correlationId.startsWith(id))));
                },
                DLQ_QUEUE_POLLING);
    }

    @Then("per i cf {string} e {string} non risulta alcuna richiesta INIPEC")
    public void noRequestForEitherCf(String cf1, String cf2) {
        // È un'asserzione di "assenza": la condizione deve restare stabile per tutta la finestra, così da non
        // passare per un controllo troppo anticipato (cioè prima che l'eventuale scrittura su DB avvenga).
        // Il polling si interrompe in anticipo solo se compare un record; lo scadere del timeout è l'esito atteso.
        // List e non Map: Map.of fallirebbe se cf1 e cf2 coincidessero.
        Supplier<List<Integer>> counts = () -> List.of(countAnySendStatus(cf1), countAnySendStatus(cf2));
        // Variabile tipizzata: una lambda inline sarebbe ambigua fra gli overload Predicate/Consumer di Poller.pollUntil.
        Predicate<List<Integer>> anyRequestFound = c -> c.stream().anyMatch(n -> n > 0);
        List<Integer> lastCounts;
        try {
            lastCounts = Poller.pollUntil(counts, anyRequestFound, DB_ABSENCE_POLLING);
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
        return retrieveBatchRequestItemsBySendStatus(cf, SendStatus.SENT).size() + retrieveBatchRequestItemsBySendStatus(cf, SendStatus.NOT_SENT).size();
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
        Map<String, AttributeValue> attributeValues = Map.of(":v_sendStatus", AttributeValue.builder().s(sendStatus.name()).build(), ":v_lastReserved", AttributeValue.builder().s(notificationSentTimestamp).build());

        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS, attributeValues);

        // La query indicizza su sendStatus/lastReserved: filtriamo qui per CF.
        return items.stream().filter(item -> item.containsKey("cf") && cf.equals(item.get("cf").s())).toList();
    }

    @Then("risulta una richiesta {eservice} per il cf {string}")
    public void verifyRegistryRequestExists(Eservice eservice, String cf) {

        String iun = sharedSteps.getNotificationIun();

        Poller.pollUntil(
                () -> retrieveRegistryRequests(cf, eservice, iun),
                items -> {
                    assertThat(items).as("Nessuna richiesta %s trovata per cf=%s e iun=%s", eservice, cf, iun).isNotEmpty();
                },
                REGISTRY_REQUEST_POLLING);
    }

    private List<Map<String, AttributeValue>> retrieveRegistryRequests(String cf, Eservice eservice, String iun) {

        Map<String, AttributeValue> attributeValues = Map.of(":v_sendStatus", AttributeValue.builder().s(SendStatus.SENT.name()).build(), ":v_lastReserved", AttributeValue.builder().s(MIN_LAST_RESERVED).build());
        List<Map<String, AttributeValue>> items = dynamoDbService.callAllPages(DynamoTableName.BATCH_REQUESTS_WITH_INDEX_SEND_STATUS, attributeValues);

        return items.stream().filter(item -> cf.equals(item.get("cf").s())).filter(item -> eservice.name().equals(item.get("eservice").s())).filter(item -> item.get("correlationId").s().contains(iun)).toList();
    }
}

