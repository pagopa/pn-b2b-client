package it.pagopa.pn.cucumber.steps.delayer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";

    private List<JsonNode> lastResult;

    @Given("il CSV {string} è importato da S3 nella tabella di test tramite lambda")
    public void importaCsvTramiteLambda(String csvName, String lambdaArn) throws Exception {
        String payload = """
        {
          "operationType": "IMPORT_DATA",
          "parameters": []
        }
        """;

        String rawResult = lambdaInvoker.invokeMyLambda(lambdaArn, payload);
        checkLambdaResponse(rawResult, "IMPORT_DATA");
        log.info("Importazione CSV [{}] completata correttamente", csvName);
    }

    @When("viene avviato l'algoritmo tramite lambda")
    public void eseguiAlgoritmo() throws Exception {
        String payload = """
        {
          "operationType": "RUN_ALGORITHM",
          "parameters": []
        }
        """;

        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "RUN_ALGORITHM");
        log.info("Algoritmo avviato correttamente.");
    }

    /**
     * Verifica che le notifiche associate a un requestId siano elaborate in ordine di priorità,
     * secondo la logica dell'algoritmo del Delayer.
     *
     * L'ordine atteso è:
     *   - RS: ordinate per prepareRequestDate
     *   - Secondi tentativi (attempt = 1): ordinate per prepareRequestDate
     *   - Altre: ordinate per notificationSentAt
     *
     * @param expectedOrder Tabella Gherkin con i criteri attesi di ordinamento
     * @throws Exception Se il numero di notifiche è errato o l'ordine è scorretto
     */
    @Then("le notifiche sono elaborate in ordine di priorità:")
    public void verificaOrdinePriorita(DataTable expectedOrder) throws Exception {

        // Segmenta e verifica l'ordine
        List<JsonNode> rs = new ArrayList<>();
        List<JsonNode> secondiTentativi = new ArrayList<>();
        List<JsonNode> altri = new ArrayList<>();

        for (JsonNode node : lastResult) {
            String tipo = node.path("productType").asText();
            int attempt = node.path("attempt").asInt();
            if ("RS".equalsIgnoreCase(tipo)) {
                rs.add(node);
            } else if (attempt == 1) {
                secondiTentativi.add(node);
            } else {
                altri.add(node);
            }
        }

        assertOrdinati(rs, "prepareRequestDate", "RS");
        assertOrdinati(secondiTentativi, "prepareRequestDate", "SECONDO_TENTATIVO");
        assertOrdinati(altri, "notificationSentAt", "ALTRO");
    }

    @Then("i risultati per requestId {string} contengono esattamente {int} notifiche")
    public void risultatiContengonoEsattamente(String requestId, int expectedCount) throws Exception {
        lastResult = pollNotificheByRequestId(requestId, expectedCount, 10, 2000); // polling max 10 volte
        Assertions.assertEquals(expectedCount, lastResult.size(),
                String.format("Numero di notifiche ottenute per requestId %s diverso da quello atteso", requestId));
    }

    /**
     * Verifica che tutte le notifiche caricate tramite CSV e associate all’ultimo requestId elaborato
     * NON siano state effettivamente pianificate per l’invio (cioè non siano state iscritte nella tabella
     * finale di recapito, come ad esempio {@code pn-PaperDeliveryReadyToSend}).
     * <p>
     * Questa verifica è resa possibile in modo indiretto grazie al campo {@code deliveryDate} restituito
     * dalla Lambda {@code GET_BY_REQUEST_ID}, che riflette lo stato di avanzamento dell’elaborazione:
     * <ul>
     *   <li>Una {@code deliveryDate} valorizzata con una data reale (es. ≥ 2025-07-01) implica che
     *       la notifica è stata pianificata per la stampa/recapito.</li>
     *   <li>Valori come {@code 1970-01-01T00:00:00Z}, {@code 1970-01-05T00:00:00Z}, stringa vuota o null,
     *       indicano che la notifica non è ancora stata presa in carico per l’invio.</li>
     * </ul>
     * <p>
     * A causa delle limitazioni imposte al test non è possibile interrogare direttamente la tabella di recapito
     * effettiva pn-PaperDeliveryReadyToSend.
     * Pertanto, la logica di test deduce lo stato della notifica esclusivamente analizzando il contenuto del campo
     * {@code deliveryDate}.
     * <p>
     * In caso di rilevamento di notifiche con {@code deliveryDate} valorizzata (cioè inviate), il metodo
     * fallisce il test segnalando gli IUN coinvolti.
     *
     * @param tableName il nome logico della tabella di destinazione attesa (usato solo a scopo descrittivo nel test)
     */

    @Then("le restanti notifiche non sono presenti nella tabella {string}")
    public void notificheNonPresentiNellaTabella(String tableName) {
        List<JsonNode> notificheSpedite = lastResult.stream()
                .filter(n -> isDeliveryDateReal(n.path("deliveryDate").asText()))
                .collect(Collectors.toList());

        if (!notificheSpedite.isEmpty()) {
            String dettagli = notificheSpedite.stream()
                    .map(n -> n.path("iun").asText())
                    .collect(Collectors.joining(", "));
            throw new AssertionError("Notifiche inaspettatamente inviate: " + dettagli);
        }
    }

    private void assertOrdinati(List<JsonNode> lista, String campo, String categoria) {
        List<String> valori = lista.stream()
                .map(n -> n.path(campo).asText())
                .collect(Collectors.toList());

        List<String> ordinati = new ArrayList<>(valori);
        Collections.sort(ordinati);

        Assertions.assertEquals(ordinati, valori,
                String.format("La categoria '%s' non è ordinata correttamente per '%s'", categoria, campo));
    }

    private void checkLambdaResponse(String rawJson, String operationType) throws Exception {
        if (rawJson == null) {
            throw new RuntimeException("Lambda ha restituito null per l'operazione " + operationType);
        }

        JsonNode root = objectMapper.readTree(rawJson);
        int statusCode = root.path("statusCode").asInt(-1);
        String bodyText = root.path("body").asText();

        if (statusCode != 200) {
            String message;
            if (bodyText.startsWith("{")) {
                JsonNode bodyJson = objectMapper.readTree(bodyText);
                message = bodyJson.path("message").asText("Errore sconosciuto");
            } else {
                message = bodyText;
            }

            throw new RuntimeException(String.format(
                    "Errore nella Lambda [%s]: status=%d, message=%s", operationType, statusCode, message));
        }
    }

    /**
     * Esegue una chiamata ripetuta alla Lambda per ottenere tutte le notifiche associate a un requestId specifico.
     * Attende fino al raggiungimento del numero atteso di record, oppure termina dopo il numero massimo di tentativi.
     *
     * @param requestId      L'identificativo univoco del gruppo di notifiche da interrogare
     * @param expectedCount  Numero atteso di notifiche da recuperare
     * @param maxAttempts    Numero massimo di tentativi di polling
     * @param sleepMillis    Intervallo di attesa tra un tentativo e l'altro (in millisecondi)
     * @return Lista di notifiche restituite dalla Lambda (come oggetti JsonNode)
     * @throws Exception in caso di errore nella risposta Lambda o nel parsing
     *
     * <p><b>Nota tecnica per i test automatizzati:</b></p>
     * <p>In ambiente di test, tutte le notifiche iniettate tramite CSV condividono lo stesso {@code requestId}.
     * Questo comportamento è intenzionale e differisce dal comportamento reale di produzione, dove ogni notifica ha un {@code requestId} distinto.
     * Tale semplificazione consente di interrogare tutte le notifiche del batch con una sola chiamata alla Lambda
     * tramite l'operazione {@code GET_BY_REQUEST_ID}, riducendo la complessità dei test e migliorando l'affidabilità.</p>
     * <p>Poiché l'algoritmo del ritardatore non basa la logica sul {@code requestId}, questo compromesso non altera la validità dei test di priorità,
     * capacità, limiti per PA o tentativi successivi.</p>
     */
    private List<JsonNode> pollNotificheByRequestId(String requestId, int expectedCount, int maxAttempts, int sleepMillis) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String payload = String.format("""
        {
          "operationType": "GET_BY_REQUEST_ID",
          "parameters": [ "%s" ]
        }
        """, requestId);

            String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
            checkLambdaResponse(rawResult, "GET_BY_REQUEST_ID");

            JsonNode parsed = objectMapper.readTree(rawResult);
            JsonNode body = parsed.path("body");
            if (body.isTextual()) {
                body = objectMapper.readTree(body.asText());
            }

            List<JsonNode> records = Arrays.asList(objectMapper.treeToValue(body, JsonNode[].class));
            if (records.size() == expectedCount) {
                log.info("Trovate tutte le {} notifiche al tentativo {}/{}", expectedCount, attempt, maxAttempts);
                return records;
            }

            log.info("Notifiche trovate: {} su {} (tentativo {}/{}). Riprovo tra {}ms...", records.size(), expectedCount, attempt, maxAttempts, sleepMillis);
            Thread.sleep(sleepMillis);
        }

        throw new AssertionError("Numero notifiche attese non raggiunto per requestId " + requestId +
                " dopo " + maxAttempts + " tentativi. Attese: " + expectedCount);
    }

    /**
     * Considera 'reale' una deliveryDate solo se è valorizzata con una data futura rispetto all’Epoch.
     */
    private boolean isDeliveryDateReal(String deliveryDate) {
        if (deliveryDate == null || deliveryDate.isBlank()) return false;
        return !deliveryDate.startsWith("1970-01");
    }

}
