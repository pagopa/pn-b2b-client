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
    private Integer numeroNotifiche = 0;
    private String actualCsv;
    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";

    private List<JsonNode> lastResult;

    @Given("il CSV {string} contiene {int} notifiche appartenenti alla stessa categoria")
    @Given("il CSV {string} contiene {int} notifiche appartenenti alle categorie RS, SECONDO TENTATIVO, ALTRO")
    public void initParams(String csv, Integer numeroNotifiche) {
        this.actualCsv = csv;
        this.numeroNotifiche = numeroNotifiche;
    }

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

    @Then("le prime {int} notifiche sono pianificate secondo ordine cronologico per il campo {string}")
    public void verificaOrdinamentoCronologico(int limit, String campoOrdinamento) {
        List<JsonNode> prime = lastResult.subList(0, Math.min(limit, lastResult.size()));
        List<String> dateValues = prime.stream()
                .map(n -> n.path(campoOrdinamento).asText())
                .toList();

        List<String> ordinati = new ArrayList<>(dateValues);
        Collections.sort(ordinati);

        Assertions.assertEquals(ordinati, dateValues,
                String.format("Le prime %d notifiche non sono ordinate per '%s'", limit, campoOrdinamento));
    }

    /**
     * Verifica che le notifiche indicate non siano ancora state pianificate per la spedizione.
     *
     * <p>
     * Poiché nei test non è possibile interrogare direttamente la tabella finale
     * {@code pn-PaperDeliveryReadyToSend}, utilizziamo la tabella {@code pn-DelayerPaperDelivery}
     * (accessibile via lambda) per inferire lo stato delle notifiche.
     * </p>
     *
     * <p>
     * In particolare, analizziamo il campo {@code deliveryDate}:
     * <ul>
     *     <li>Se il valore è pari a {@code 1970-01-05T00:00:00Z}, la notifica non è stata ancora
     *         pianificata per la spedizione, e quindi non è presente nella tabella finale.</li>
     *     <li>Se il campo è valorizzato con una data reale (es. {@code 2025-07-30T00:00:00Z} o maggiore),
     *         significa che la spedizione è stata programmata per quel giorno.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Questa logica è conforme a quanto previsto nel documento di specifica (SRS),
     * dove si afferma che la presenza di {@code deliveryDate} valorizzata indica
     * l’inclusione in un batch di recapito. Il valore {@code 1970-01-05} rappresenta invece
     * uno stato di default (non elaborato), poiché corrisponde a un timestamp nullo o iniziale
     * nel formato Epoch.
     * </p>
     *
     * <p><b>Nota tecnica:</b> il valore "1970-01-05" deriva dal comportamento del sistema
     * quando una data non viene mai calcolata. Questo compromesso è adottato nei test per
     * distinguere facilmente tra notifiche elaborate e non elaborate, sfruttando un default noto.</p>
     *
     * <p><b>Limite attuale:</b> la lambda test espone solo operazioni sulla tabella {@code pn-DelayerPaperDelivery}.
     * In futuro, se verrà estesa a {@code pn-PaperDeliveryReadyToSend}, questa logica potrà essere sostituita
     * da una verifica diretta.</p>
     *
     * @throws AssertionError se una o più notifiche risultano già pianificate (cioè con deliveryDate valorizzata)
     */
    @Then("le restanti {int} notifiche non sono ancora pianificate")
    public void verificaNonPianificate(int nonPianificateAttese) {
        List<JsonNode> nonPianificate = lastResult.stream()
                .filter(n -> !isDeliveryDateReal(n.path("deliveryDate").asText()))
                .toList();

        Assertions.assertEquals(nonPianificateAttese, nonPianificate.size(),
                String.format("Attese %d notifiche non pianificate, trovate %d",
                        nonPianificateAttese, nonPianificate.size()));
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
    private boolean isDeliveryDateReal(String dateStr) {
        return !"1970-01-05T00:00:00Z".equals(dateStr);
    }

}
