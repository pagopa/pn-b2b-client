package it.pagopa.pn.cucumber.steps.delayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {

    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";
    private static final String DEFAULT_DELIVERY_DATE = "1970-01-05T00:00:00Z";
    private static final List<String> WORKFLOW_STEPS = List.of(
            "EVALUATE_SENDER_LIMIT",
            "EVALUATE_DRIVER_CAPACITY",
            "EVALUATE_PRINT_CAPACITY",
            "SENT_TO_PREPARE_PHASE_2"
    );


    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<JsonNode> lastResult;
    Map<String, List<JsonNode>> groupedByPkSubstring = new HashMap<>();
    private Integer numeroNotifiche = 0;
    private String actualCsv;
    private Map<String, String> predictDeliveryDateMap = new HashMap<>();

    @Given("il CSV {string} contiene {int} notifiche appartenenti alla stessa categoria")
    @Given("il CSV {string} contiene {int} notifiche appartenenti alle categorie RS, SECONDO TENTATIVO, ALTRO")
    @Given("il CSV {string} contiene {int} notifiche: 30 da mittenti censiti e 10 da mittenti non censiti")
    @Given("il CSV {string} contiene {int} notifiche: 10 RS e 10 con attempt = 1")
    @Given("il CSV {string} contiene {int} notifiche: 10 valide e 10 non valide")
    @Given("il CSV {string} contiene {int} notifiche non prioritarie da mittenti non censiti")
    @Given("il CSV {string} contiene {int} notifiche da mittenti validi ma con provincia non mappata")
    public void initParams(String csv, Integer numeroNotifiche) {
        this.actualCsv = csv;
        this.numeroNotifiche = numeroNotifiche;
    }

    @Given("il CSV {string} è importato da S3 nella tabella di test tramite lambda")
    public void importaCsvTramiteLambda(String csvName) throws Exception {
        String payload = """
                {
                  "operationType": "IMPORT_DATA",
                  "parameters": []
                }
                """;

        String rawResult = lambdaInvoker.invokeMyLambda(this.LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "IMPORT_DATA");
        log.info("Importazione CSV [{}] completata correttamente", csvName);
    }

    @When("viene avviato l'algoritmo tramite lambda")
    @When("viene avviato nuovamente l'algoritmo tramite lambda")
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

    @Then("la capacità disponibile per il driver {string}, provincia {string}, delivery date corrispondente è superiore a {int}")
    public void verificaCapacitaPerDeliveryDateCalcolata(String driver, String provincia, int minExpected) throws Exception {
        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica trovata per verificare la deliveryDate.");
        }

        // Prendo la deliveryDate dalla prima notifica elaborata
        String deliveryDate = lastResult.get(0).path("deliveryDate").asText();
        if (deliveryDate == null || deliveryDate.isBlank() || deliveryDate.equals("1970-01-05T00:00:00Z")) {
            throw new AssertionError("La deliveryDate non è stata valorizzata correttamente.");
        }

        String payload = String.format("""
                {
                  "operationType": "GET_USED_CAPACITY",
                  "parameters": [ "%s", "%s", "%s" ]
                }
                """, driver, provincia, deliveryDate);

        String raw = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(raw, "GET_USED_CAPACITY");

        JsonNode body = objectMapper.readTree(raw).path("body");
        if (body.isTextual()) {
            body = objectMapper.readTree(body.asText());
        }

        int capacity = body.path("capacity").asInt(-1);
        int used = body.path("usedCapacity").asInt(-1);

        log.info("Capacità disponibile per {}~{}@{}: {}/{}", driver, provincia, deliveryDate, used, capacity);

        if ((capacity - used) < minExpected) {
            throw new AssertionError(String.format(
                    "Capacità insufficiente: disponibile %d, atteso almeno %d", capacity - used, minExpected
            ));
        }
    }

    /**
     * Verifica che le prime {@code limit} notifiche siano elaborate in ordine di priorità,
     * seguendo le regole dell'algoritmo Delayer:
     *
     * <ul>
     *   <li><b>RS</b>: ordinate per {@code prepareRequestDate}</li>
     *   <li><b>Secondi tentativi</b> (attempt = 1): ordinate per {@code prepareRequestDate}</li>
     *   <li><b>Altri</b>: ordinate per {@code notificationSentAt}</li>
     * </ul>
     *
     * @param limit         Numero di notifiche da considerare (le prime N)
     * @param expectedOrder Tabella Gherkin con la sequenza attesa
     */
    @Then("le prime {int} notifiche per il workflow step {string} sono selezionate secondo l’ordine di priorità:")
    public void verificaOrdinePrioritaLimitate(int limit, String workflowStep, DataTable expectedOrder) {

        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica disponibile da verificare.");
        }

        List<JsonNode> primeNotifiche =  groupedByPkSubstring.get(workflowStep).subList(0, Math.min(limit, groupedByPkSubstring.get(workflowStep).size()));

        List<JsonNode> rs = new ArrayList<>();
        List<JsonNode> secondiTentativi = new ArrayList<>();
        List<JsonNode> altri = new ArrayList<>();

        for (JsonNode node : primeNotifiche) {
            String tipo = node.path("productType").asText();
            int attempt = node.path("attempt").asInt();

            if ("RS".equalsIgnoreCase(tipo)) rs.add(node);
            else if (attempt == 1) secondiTentativi.add(node);
            else altri.add(node);
        }

        assertOrdinati(rs, "prepareRequestDate", "RS");
        assertOrdinati(secondiTentativi, "prepareRequestDate", "SECONDO_TENTATIVO");
        assertOrdinati(altri, "notificationSentAt", "ALTRO");
    }

    @Then("i risultati per requestId {string} e workflow step {string} contengono esattamente {int} notifiche")
    public void risultatiContengonoEsattamente(String requestId, int expectedCount, String workflowStep) throws Exception {
        lastResult = pollNotificheByRequestId(requestId, expectedCount, 10, 2000);

        for (String key : WORKFLOW_STEPS) {
            groupedByPkSubstring.put(key, new ArrayList<>());
        }

        for (JsonNode node : lastResult) {
            String pk = node.path("pk").asText();
            for (String key : WORKFLOW_STEPS) {
                if (pk.contains(key)) groupedByPkSubstring.get(key).add(node);
            }
        }

        Assertions.assertEquals(expectedCount, groupedByPkSubstring.get(workflowStep).size(),
                String.format("Numero di notifiche ottenute per requestId %s e workflow step %s diverso da quello atteso", requestId, workflowStep));
    }

    @Then("le prime {int} notifiche sono pianificate secondo ordine cronologico per il campo {string}")
    public void verificaOrdinamentoCronologico(int limit, String campoOrdinamento) {
        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica disponibile da verificare.");
        }

        List<JsonNode> listaPianificate = groupedByPkSubstring.get(WORKFLOW_STEPS.get(WORKFLOW_STEPS.size()-1));
        List<JsonNode> primeNotifiche =  listaPianificate.subList(0, Math.min(limit, listaPianificate.size()));

        List<String> dateValues = primeNotifiche.stream()
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
                .filter(n -> !isDeliveryPlanned(n))
                .toList();

        Assertions.assertEquals(nonPianificateAttese, nonPianificate.size(),
                String.format("Attese %d notifiche non pianificate, trovate %d",
                        nonPianificateAttese, nonPianificate.size()));
    }

    @Then("la capacità usata per il driver {string}, provincia {string}, data {string} è pari a {int} su una capacità totale di {int}")
    public void verificaCapacitaUsata(String driver, String provincia, String deliveryDateStr, int expectedUsed, int expectedCapacity) throws Exception {
        String payload = String.format("""
                {
                  "operationType": "GET_USED_CAPACITY",
                  "parameters": [ "%s", "%s", "%s" ]
                }
                """, driver, provincia, deliveryDateStr);

        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "GET_USED_CAPACITY");

        JsonNode root = objectMapper.readTree(rawResult);
        JsonNode body = root.path("body");
        if (body.isTextual()) {
            body = objectMapper.readTree(body.asText());
        }

        int used = body.path("usedCapacity").asInt(-1);
        int capacity = body.path("capacity").asInt(-1);

        Assertions.assertEquals(expectedUsed, used,
                String.format("Capacità usata errata per %s~%s a %s: attesa %d, trovata %d", driver, provincia, deliveryDateStr, expectedUsed, used));

        Assertions.assertEquals(expectedCapacity, capacity,
                String.format("Capacità totale errata per %s~%s a %s: attesa %d, trovata %d", driver, provincia, deliveryDateStr, expectedCapacity, capacity));
    }

    /**
     * Verifica che la capacità disponibile per un determinato recapitista (driver),
     * su una provincia e per un tipo di prodotto specifico, alla data di delivery prevista,
     * corrisponda al valore atteso.
     *
     * <p>
     * Questo controllo sfrutta la lambda `GET_DRIVER_CAPACITY`, che restituisce i dati
     * dalla tabella {@code pn-PaperDeliveryDriverWeeklyCapacity}, filtrando per:
     * driver + provincia (geoKey) + productType + deliveryDate.
     * </p>
     *
     * <p>
     * La deliveryDate è calcolata a partire dalle notifiche del test, per garantire
     * che il controllo sia allineato alla settimana in cui si collocano le notifiche.
     * </p>
     *
     * @param driver           il nome del recapitista (unifiedDeliveryDriver)
     * @param provincia        la provincia/geoKey
     * @param productType      il tipo di prodotto (es. "AR", "RS")
     * @param expectedCapacity il valore atteso di capacità configurata
     * @throws Exception in caso di errore di invocazione o mismatch
     */
    @Then("la capacità disponibile per il driver {string}, provincia {string}, prodotto {string} è configurata a {int}")
    public void verificaCapacitaDriverProvinciaProdotto(String driver, String provincia, String productType, int expectedCapacity) throws Exception {
        // Calcoliamo la deliveryDate settimanale attesa (basata sulle notifiche nel CSV)
        String deliveryDate = extractDeliveryDateFromLastResult();

        // Costruiamo il payload per la lambda
        String payload = String.format("""
                {
                  "operationType": "GET_DRIVER_CAPACITY",
                  "parameters": [ "%s", "%s", "%s", "%s" ]
                }
                """, driver, provincia, productType, deliveryDate);

        // Invoca lambda
        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "GET_DRIVER_CAPACITY");

        JsonNode parsed = objectMapper.readTree(rawResult);
        JsonNode body = parsed.path("body");
        if (body.isTextual()) {
            body = objectMapper.readTree(body.asText());
        }

        int actualCapacity = body.path("capacity").asInt(-1);
        Assertions.assertEquals(expectedCapacity, actualCapacity, String.format(
                "Capacità attesa per driver %s, provincia %s, prodotto %s e data %s: %d, ma trovata %d",
                driver, provincia, productType, deliveryDate, expectedCapacity, actualCapacity
        ));
    }

    @Then("tutte le {int} notifiche sono posticipate: deliveryDate corrisponde al lunedì della settimana successiva")
    public void tutteNotifichePosticipate(int expectedCount) {
        if (lastResult == null || lastResult.size() != expectedCount) {
            throw new AssertionError("Numero notifiche inatteso: attese " + expectedCount + ", trovate " + (lastResult == null ? 0 : lastResult.size()));
        }

        // Calcola il lunedì della settimana successiva
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        ZonedDateTime expectedDate = nextMonday.atStartOfDay(ZoneOffset.UTC);

        for (JsonNode notifica : lastResult) {
            String deliveryDateRaw = notifica.path("deliveryDate").asText();
            ZonedDateTime deliveryDate;
            try {
                deliveryDate = ZonedDateTime.parse(deliveryDateRaw);
            } catch (Exception e) {
                throw new AssertionError("Data non valida: " + deliveryDateRaw);
            }

            if (!deliveryDate.toLocalDate().equals(expectedDate.toLocalDate())) {
                throw new AssertionError(String.format("Notifica %s ha deliveryDate %s, ma ci si aspettava %s",
                        notifica.path("iun").asText(), deliveryDateRaw, expectedDate));
            }
        }

        log.info("Tutte le {} notifiche sono posticipate correttamente al lunedì della settimana successiva: {}", expectedCount, expectedDate);
    }

    /**
     * Verifica il numero di notifiche dei mittenti non censiti che sono state elaborate o accantonate,
     * in base al tipo richiesto.
     *
     * <p>Le notifiche dei mittenti non censiti sono identificate controllando l'assenza del mittente
     * nella tabella dei limiti settimanali (che nei test lasciamo volutamente vuota).
     * Per distinguere se una notifica è stata elaborata o accantonata:
     * <ul>
     *     <li>Le notifiche <b>elaborate</b> hanno una {@code deliveryDate} valorizzata (diversa da {@code 1970-01-05}).</li>
     *     <li>Le notifiche <b>accantonate</b> hanno {@code deliveryDate} uguale a {@code 1970-01-05T00:00:00Z}.</li>
     * </ul>
     *
     * @param tipo          "elaborate" o "accantonate"
     * @param expectedCount numero atteso di notifiche per quel tipo
     * @throws AssertionError se il conteggio non corrisponde
     */
    @Then("sono state {word} esattamente {int} notifiche dei mittenti non censiti")
    public void verificaNotificheNonCensitePerStato(String tipo, int expectedCount) {
        boolean elaborate = tipo.equalsIgnoreCase("elaborate");

        List<JsonNode> filtrate = lastResult.stream()
                .filter(n -> !isMittenteCensito(n))
                .filter(n -> elaborate ? isDeliveryPlanned(n) : !isDeliveryPlanned(n))
                .toList();

        Assertions.assertEquals(expectedCount, filtrate.size(), "Numero inatteso di notifiche " + tipo);
    }

    @Then("tutte le {int} notifiche restano in attesa perché la capacità di stampa giornaliera è esaurita")
    public void notificheRestanoInAttesaPerStampaPiena(int expectedCount) {
        List<JsonNode> nonPianificate = lastResult.stream()
                .filter(this::isDeliveryPlanned)
                .toList();

        if (nonPianificate.size() != expectedCount) {
            throw new AssertionError(String.format(
                    "Attese %d notifiche non pianificate (stampa piena), ma trovate %d",
                    expectedCount, nonPianificate.size()
            ));
        }
    }

    @Then("nessuna notifica è stata ancora pianificata per la spedizione")
    public void nessunaNotificaPianificata() {
        List<JsonNode> pianificate = lastResult.stream()
                .filter(this::isDeliveryPlanned)
                .toList();

        if (!pianificate.isEmpty()) {
            String iuns = pianificate.stream().map(n -> n.path("iun").asText()).collect(Collectors.joining(", "));
            throw new AssertionError("Alcune notifiche risultano inaspettatamente pianificate per la spedizione: " + iuns);
        }
    }

    /**
     * Verifica che la capacità residua disponibile per la stampa (PRINT) sia pari al valore atteso
     * nella data specificata dal campo {@code deliveryDate} delle notifiche elaborate.
     *
     * <p>
     * L'operazione si basa sull'invocazione della lambda test con operationType {@code GET_USED_CAPACITY},
     * che interroga la tabella {@code pn-PaperDeliveryDriverUsedCapacities} per ottenere la capacità settimanale
     * del recapitista nel contesto di stampa, ovvero per:
     * <ul>
     *     <li>{@code unifiedDeliveryDriver}: "Poste"</li>
     *     <li>{@code geoKey}: "PRINT"</li>
     *     <li>{@code deliveryDate}: valorizzata in base alle notifiche testate</li>
     * </ul>
     * </p>
     *
     * <p>
     * Il test è utile in tutti gli scenari in cui si intende verificare la capacità di stampa giornaliera.
     * </p>
     *
     * <h3>📄 Requisiti per il CSV</h3>
     * <ul>
     *     <li>Tutte le notifiche devono avere la stessa {@code deliveryDate} predefinita</li>
     *     <li>{@code unifiedDeliveryDriver} deve essere impostato a {@code Poste}</li>
     *     <li>{@code geoKey} deve essere esattamente {@code PRINT}</li>
     * </ul>
     *
     * @param expectedAvailable capacità residua attesa per la stampa (es. 0)
     * @throws Exception se la capacità residua è diversa dal valore atteso
     */
    @And("la capacità disponibile per la stampa nella data prevista di delivery è configurata a {int}")
    public void verificaCapacitaStampaConValoreAtteso(int expectedAvailable) throws Exception {
        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica trovata per verificare la deliveryDate.");
        }

        String deliveryDate = lastResult.get(0).path("deliveryDate").asText();
        if (deliveryDate == null || deliveryDate.isBlank()) {
            throw new IllegalStateException("deliveryDate mancante o non valida nella notifica.");
        }

        String payload = String.format("""
                {
                  "operationType": "GET_USED_CAPACITY",
                  "parameters": [ "Poste", "PRINT", "%s" ]
                }
                """, deliveryDate);

        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "GET_USED_CAPACITY");

        JsonNode body = objectMapper.readTree(rawResult).path("body");
        if (body.isTextual()) {
            body = objectMapper.readTree(body.asText());
        }

        int capacity = body.path("capacity").asInt(-1);
        int used = body.path("usedCapacity").asInt(-1);

        log.info("Capacità stampa su PRINT per {}: total={}, used={}", deliveryDate, capacity, used);

        if (capacity == -1 || used == -1) {
            throw new IllegalStateException("Risposta lambda incompleta: capacity o usedCapacity mancanti.");
        }

        int actualAvailable = capacity - used;

        if (actualAvailable != expectedAvailable) {
            throw new AssertionError(String.format(
                    "Capacità residua di stampa errata per il %s: attesa %d, trovata %d (totale=%d, usata=%d)",
                    deliveryDate, expectedAvailable, actualAvailable, capacity, used));
        }
    }

    @Given("la capacità disponibile per il driver {string} su provincia {string} e per ogni deliveryDate attesa è almeno {int}")
    public void verificaCapacitaPredettaDaPrepareRequestDate(String driver, String provincia, Integer capacitaMinimaAttesa) {

        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica trovata per verificare la deliveryDate.");
        }

        // Recupera univocamente le prepareRequestDate
        Set<String> prepareRequestDates = new HashSet<>();

        for (JsonNode node : groupedByPkSubstring.get(WORKFLOW_STEPS.get(0))) {
            String prepareRequestDate = node.path("prepareRequestDate").asText();
            if (prepareRequestDate == null || prepareRequestDate.isBlank()) {
                throw new IllegalStateException("deliveryDate mancante o non valida nella notifica.");
            }
            prepareRequestDates.add(prepareRequestDate);
        }

        // Calcola tutte le deliveryDate associate alle prepareRequestDate
        prepareRequestDates.forEach(rd -> {
            LocalDate lunedi = LocalDate.parse(rd.substring(0, 10)).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            String deliveryDate = lunedi.toString() + "T00:00:00Z";
            this.predictDeliveryDateMap.put(rd, deliveryDate);
        });

        // Verifica che per ogni deliveryDate ci sia abbastanza capacità
        predictDeliveryDateMap.forEach((rd, deliveryDate) -> {
            String payload = String.format("""
                    {
                      "operationType": "GET_USED_CAPACITY",
                      "parameters": [ "%s", "%s", "%s" ]
                    }
                    """, driver, provincia, deliveryDate);

            String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
            JsonNode body = null;

            try {
                checkLambdaResponse(rawResult, "GET_USED_CAPACITY");
                body = objectMapper.readTree(rawResult).path("body");
                if (body.isTextual()) body = objectMapper.readTree(body.asText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            int capacity = body.path("capacity").asInt(-1);
            int used = body.path("usedCapacity").asInt(-1);
            int available = capacity - used;

            log.info("Capacità driver {} su {} per {}: disponibile {} su {}", driver, provincia, deliveryDate, available, capacity);

            if (available < capacitaMinimaAttesa) {
                throw new AssertionError(String.format(
                        "Capacità insufficiente: attesi almeno %d slot liberi, disponibili %d",
                        capacitaMinimaAttesa, available));
            }
        });
    }

    @Then("sono state {word} tutte le {int} notifiche \\(verifica basata su deliveryDate)")
    public void verificaNotifichePianificateOAccantonate(String stato, int expectedCount) {
        List<JsonNode> selezionate;

        if ("pianificate".equalsIgnoreCase(stato)) {
            selezionate = lastResult.stream()
                    .filter(n -> isDeliveryPlanned(n))
                    .toList();
        } else if ("accantonate".equalsIgnoreCase(stato)) {
            selezionate = lastResult.stream()
                    .filter(n -> !isDeliveryPlanned(n))
                    .toList();
        } else {
            throw new IllegalArgumentException("Stato atteso non riconosciuto: " + stato);
        }

        Assertions.assertEquals(expectedCount, selezionate.size(),
                String.format("Attese %d notifiche '%s', ma trovate %d", expectedCount, stato, selezionate.size()));
    }

    /**
     * Verifica che tutte le notifiche attualmente in memoria ({@code lastResult}) siano state pianificate,
     * ovvero che il campo {@code deliveryDate} sia valorizzato con una data reale (diversa dal default "1970-01-05T00:00:00Z").
     *
     * <p>
     * La valorizzazione del campo {@code deliveryDate} indica che la notifica è stata assegnata
     * a una data di recapito effettiva, risultando quindi elaborata dal sistema.
     * </p>
     *
     * @param expectedCount Numero atteso di notifiche pianificate
     * @throws AssertionError se alcune notifiche risultano non pianificate o il conteggio non corrisponde
     */
    @Then("tutte le {int} notifiche sono state pianificate \\(deliveryDate valorizzato)")
    public void tutteNotifichePianificate(int expectedCount) {
        if (lastResult.size() != expectedCount) {
            throw new AssertionError(String.format(
                    "Numero di notifiche attese: %d, trovate: %d", expectedCount, lastResult.size()));
        }

        List<JsonNode> nonPianificate = lastResult.stream()
                .filter(n -> !isDeliveryPlanned(n))
                .toList();

        if (!nonPianificate.isEmpty()) {
            String dettagli = nonPianificate.stream()
                    .map(n -> n.path("iun").asText())
                    .collect(Collectors.joining(", "));
            throw new AssertionError("Le seguenti notifiche non sono state pianificate: " + dettagli);
        }
    }

    @Then("la deliveryDate delle notifiche coincide con la deliveryDate attesa")
    public void verificaDeliveryDateValorizzata() {
        if (lastResult == null || lastResult.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica trovata per verificare la deliveryDate.");
        }

        for (JsonNode node : groupedByPkSubstring.get(WORKFLOW_STEPS.get(WORKFLOW_STEPS.size() - 1))) {
            String sk = node.path("sk").asText();
            String deliveryDate;

            if (sk != null && !sk.isBlank()) {
                String[] parts = sk.split("~");

                if (parts.length > 0) deliveryDate = parts[0];
                else throw new IllegalStateException("Formato sk non valido: " + sk);

            } else
                throw new IllegalStateException("Campo sk mancante o vuoto nel nodo: " + node);


            if (deliveryDate == null || deliveryDate.isBlank()) {
                throw new AssertionError("Una notifica ha deliveryDate mancante o vuota.");
            }

            if (deliveryDate.equals(DEFAULT_DELIVERY_DATE)) {
                throw new AssertionError("La deliveryDate non è stata pianificata correttamente (rimasta al default).");
            }

            String predictDeliveryDate = predictDeliveryDateMap.get(deliveryDate);
            if (!deliveryDate.equals(predictDeliveryDate))
                throw new AssertionError("Errore di pianificazione: la deliveryDate ricevuta ('" + deliveryDate + "') non coincide con la deliveryDate predetta ('" + predictDeliveryDate + "').");
        }

        log.info("Tutte le notifiche hanno deliveryDate correttamente valorizzata.");
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
     * @param requestId     L'identificativo univoco del gruppo di notifiche da interrogare
     * @param expectedCount Numero atteso di notifiche da recuperare
     * @param maxAttempts   Numero massimo di tentativi di polling
     * @param sleepMillis   Intervallo di attesa tra un tentativo e l'altro (in millisecondi)
     * @return Lista di notifiche restituite dalla Lambda (come oggetti JsonNode)
     * @throws Exception in caso di errore nella risposta Lambda o nel parsing
     *
     *                   <p><b>Nota tecnica per i test automatizzati:</b></p>
     *                   <p>In ambiente di test, tutte le notifiche iniettate tramite CSV condividono lo stesso {@code requestId}.
     *                   Questo comportamento è intenzionale e differisce dal comportamento reale di produzione, dove ogni notifica ha un {@code requestId} distinto.
     *                   Tale semplificazione consente di interrogare tutte le notifiche del batch con una sola chiamata alla Lambda
     *                   tramite l'operazione {@code GET_BY_REQUEST_ID}, riducendo la complessità dei test e migliorando l'affidabilità.</p>
     *                   <p>Poiché l'algoritmo del ritardatore non basa la logica sul {@code requestId}, questo compromesso non altera la validità dei test di priorità,
     *                   capacità, limiti per PA o tentativi successivi.</p>
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

    public boolean isDeliveryDateReal(String deliveryDate) {
        return deliveryDate != null && !deliveryDate.isBlank()
                && !"1970-01-05T00:00:00Z".equals(deliveryDate);
    }

    private boolean isDeliveryPlanned(JsonNode node) {
        return !DEFAULT_DELIVERY_DATE.equals(node.path("deliveryDate").asText());
    }

    private boolean isMittenteCensito(JsonNode node) {
        String mittente = node.path("senderPaId").asText();
        return mittente != null && (mittente.equals("idMittente1") || mittente.equals("idMittente2"));
    }

    private String extractDeliveryDateFromLastResult() {
        return lastResult.stream()
                .map(n -> n.path("deliveryDate").asText())
                .filter(d -> isDeliveryDateReal(d))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna deliveryDate valida trovata"));
    }

}
