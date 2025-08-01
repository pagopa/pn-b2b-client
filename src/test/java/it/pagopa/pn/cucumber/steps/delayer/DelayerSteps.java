package it.pagopa.pn.cucumber.steps.delayer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.utils.FileUtils;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {


    @Getter
    static class DelayerPaperDelivery {
        private String pk;
        private String sk;
        private String requestId;
        private String notificationSentAt;
        private String prepareRequestDate;
        private String productType;
        private String senderPaId;
        private String province;
        private String cap;
        private String attempt;
        private String iun;
        private String unifiedDeliveryDriver;

        public DelayerPaperDelivery(List<String> header, List<String> csvLine) {
            if (header == null || csvLine == null || header.size() != csvLine.size()) {
                throw new IllegalArgumentException("CSV non valido: header e riga devono avere la stessa lunghezza");
            }

            Map<String, String> rowMap = new HashMap<>();
            for (int i = 0; i < header.size(); i++) {
                rowMap.put(header.get(i).trim(), csvLine.get(i).trim());
            }

            this.requestId = requireField(rowMap, "requestId");
            this.notificationSentAt = requireField(rowMap, "notificationSentAt");
            this.prepareRequestDate = requireField(rowMap, "prepareRequestDate");
            this.productType = requireField(rowMap, "productType");
            this.senderPaId = requireField(rowMap, "senderPaId");
            this.province = requireField(rowMap, "province");
            this.cap = requireField(rowMap, "cap");
            this.attempt = requireField(rowMap, "attempt");
            this.iun = requireField(rowMap, "iun");
        }

        public DelayerPaperDelivery(JsonNode tableRecord) {
            this.pk = tableRecord.get("pk").asText();
            this.sk = tableRecord.get("sk").asText();
            this.requestId = requireField(tableRecord, "requestId", false);
            this.notificationSentAt = requireField(tableRecord, "notificationSentAt", false);
            this.prepareRequestDate = requireField(tableRecord, "prepareRequestDate", false);
            this.productType = requireField(tableRecord, "productType", false);
            this.senderPaId = requireField(tableRecord, "senderPaId", false);
            this.province = requireField(tableRecord, "province", false);
            this.cap = requireField(tableRecord, "cap", false);
            this.attempt = requireField(tableRecord, "attempt", false);
            this.iun = requireField(tableRecord, "iun", false);
            this.unifiedDeliveryDriver = requireField(tableRecord, "unifiedDeliveryDriver", true);
        }

        private String requireField(JsonNode node, String fieldName, boolean nullable) {
            JsonNode field = node.get(fieldName);
            if (field == null || field.isNull() || field.asText().isBlank()) {
                if (nullable) return null;
                else throw new IllegalArgumentException("Campo obbligatorio mancante o vuoto: " + fieldName);
            }

            return field.asText();
        }

        private String requireField(Map<String, String> rowMap, String fieldName) {
            String value = rowMap.get(fieldName);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Campo CSV mancante o vuoto: " + fieldName);
            }
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            DelayerPaperDelivery that = (DelayerPaperDelivery) o;
            return Objects.equals(pk, that.pk) && Objects.equals(requestId, that.requestId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pk, requestId);
        }
    }

    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";
    private static final String CSV_PATH = "it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/delayer/csv";
    enum WorkflowStep {
        EVALUATE_SENDER_LIMIT(0),
        EVALUATE_DRIVER_CAPACITY(1),
        EVALUATE_PRINT_CAPACITY(2),
        SENT_TO_PREPARE_PHASE_2(3);

        private final int index;

        WorkflowStep(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public static Optional<WorkflowStep> fromIndex(int index) {
            return Arrays.stream(values())
                    .filter(ws -> ws.index == index)
                    .findFirst();
        }

        public static Optional<WorkflowStep> fromString(String name) {
            return Arrays.stream(values())
                    .filter(ws -> ws.name().equalsIgnoreCase(name))
                    .findFirst();
        }

        @Override
        public String toString() {
            return name() + "(" + index + ")";
        }
    }

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Map<String, List<DelayerPaperDelivery>> groupedByPkSubstring = new HashMap<>();
    private Integer numeroNotifiche = 0;
    private Integer senderLimit = null;
    private Integer driverCapacity = null;
    private Integer printCapacity = 0;
    private String expectedDeliveryDate = null;
    private List<DelayerPaperDelivery> actualCsv = new ArrayList<>();


    @Given("il CSV {string} contiene {int} notifiche cosi distribuite:")
    public void initParams(String csv, Integer numeroNotifiche, DataTable dataTable) {
        // Inizializzazione CSV
        List<List<String>> actualCsv = FileUtils.readCsvSafe(String.join("/", CSV_PATH, csv), ";", false);
        List<String> header = actualCsv.get(0);

        this.numeroNotifiche = actualCsv.size() - 1;

        if (!this.numeroNotifiche.equals(numeroNotifiche))
            throw new RuntimeException("Il numero di notifiche dichiarate non corrisponde al numero di notifiche lette nel file csv");

        for (int i = 1; i <= numeroNotifiche; i++) {
            this.actualCsv.add(new DelayerPaperDelivery(header, actualCsv.get(i)));
        }

        if (this.actualCsv.size() != this.numeroNotifiche)
            throw new RuntimeException("Sono state lette " + this.actualCsv.size() + " notifiche su " + this.numeroNotifiche + " totali nel file csv");

        // Inizializzazione Map workflowItem-notifiche
        for (WorkflowStep key :  WorkflowStep.values()) groupedByPkSubstring.put(key.toString(), new ArrayList<>());

        // Inizializzazione expected delivery
        /*
         Gli elementi usciti dalla PREPARE fase 1 alla settimana W vengono inseriti nella pn-DelayerPaperDelivey con la deliveryDate che punta a W+1
         Dal punto di vista del test siamo nel POV della valutazione, quindi è come se le notifiche fossero state caricate in tabella la settimana scorsa (W)
         con la deliveryDate alla W+1(corrente) e ora, settimana W+1 le stiamo valutando
        */
        if (expectedDeliveryDate == null) {
            LocalDate lunedi = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            expectedDeliveryDate = lunedi.toString();
        }

    }

    @And("si presuppone che il limite mittente settimanale \\(paId-product_type-province-deliveryDate) sia {word} {int}")
    @And("si presuppone che il limite mittente settimanale \\(paId-product_type-province-deliveryDate) sia {word} a {int}")
    public void siPresupponeCheIlLimiteMittenteSettimanalePaIdProduct_typeProvinceDeliveryDateSiaAlmeno(String compare, int limit) {
        senderLimit = calculateLimitByComparativo(compare, limit);
        if (senderLimit < 0) throw new IllegalArgumentException("SenderLimit non valido");
    }

    @And("si presuppone che il limite recapitista unificato settimanale \\(unifiedDeliveryDriver-provincia-deliveryDate) sia {word} {int}")
    @And("si presuppone che il limite recapitista unificato settimanale \\(unifiedDeliveryDriver-provincia-deliveryDate) sia {word} a {int}")
    public void siPresupponeCheIlLimiteRecapitistaUnificatoSettimanaleUnifiedDeliveryDriverProvinciaDeliveryDateSia(String compare, int limit) {
        driverCapacity = calculateLimitByComparativo(compare, limit);
        if (driverCapacity < 0) throw new IllegalArgumentException("DriverCapacity non valido");
    }

    @And("si presuppone che la capacità di stampa giornaliera sia {word}")
    public void siPresupponeCheLaCapacitàDiStampaGiornalieraSiaSufficiente(String compare) {
        switch (compare) {
            case "sufficiente":
                this.printCapacity = 180_000;
                break;
            case "insufficiente":
                this.printCapacity = 0;
                break;
            default:
                throw new IllegalArgumentException("Il comparativo non è valido: " + compare);
        }
    }

    @And("si presuppone che la capacità di stampa giornaliera sia {word} {int}")
    public void siPresupponeCheLaCapacitàDiStampaGiornalieraSiaSufficiente(String compare, int limit) {
        printCapacity = calculateLimitByComparativo(compare, limit);
        if (printCapacity < 0) throw new IllegalArgumentException("PrintCapacity non valido");
    }

    @Given("il CSV {string} è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test")
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

    @Then("il processo valutato fino al workflow step {string} ha rispettato i criteri di ranking:")
    public void verificaOrdinePrioritaLimitate(String ws, DataTable expectedOrder) {
        WorkflowStep workflowStep = WorkflowStep.valueOf(ws);

        Map<String, List<DelayerPaperDelivery>> expectedNotifiche = this.calculateExpectedWorkflowItems(workflowStep);
        List<DelayerPaperDelivery> expected = expectedNotifiche.get(workflowStep.name());
        List<DelayerPaperDelivery> actual = groupedByPkSubstring.get(workflowStep.name());

        if (expected == null || expected.isEmpty() || actual == null || actual.isEmpty()) {
            throw new IllegalArgumentException("Expected or actual notifications are null for workflowStep: " + workflowStep);
        }

        Set<Map<String, String>> expectedSet = expected.stream()
                .map(n -> toComparableMap(n, workflowStep))
                .collect(Collectors.toSet());

        Set<Map<String, String>> actualSet = actual.stream()
                .map(n -> toComparableMap(n, workflowStep))
                .collect(Collectors.toSet());

        Assertions.assertTrue(expectedSet.containsAll(actualSet),
                () -> {
                    Set<Map<String, String>> missing = new HashSet<>(actualSet);
                    missing.removeAll(expectedSet);
                    return "Alcune notifiche attese non corrispondono:\n" + missing;
                });
    }

    @Then("esattamente {int} notifiche sono al workflow step {string}")
    public void risultatiContengonoEsattamente(int expectedCount, String ws) throws Exception {
        WorkflowStep workflowStep = WorkflowStep.valueOf(ws);
        List<DelayerPaperDelivery> notifiche = findByWorkflowStep(expectedCount, workflowStep, expectedDeliveryDate);

        groupedByPkSubstring.get(workflowStep.name()).clear();
        groupedByPkSubstring.get(workflowStep.name()).addAll(notifiche);
    }

    @Then("esattamente {int} notifiche sono state congelate e ricaricate con workflow step {string} e deliveryDate alla settimana seguente")
    public void esattamenteNEvaluateSenderLimitNotificheSonoStateCongelateERicaricateConWorkflowStepEDeliveryDateAllaSettimanaSeguente(int congelate, String workflowStep) throws Exception {
        findCongelate(congelate);
    }

    @Given("verifica che la capacità disponibile per ogni tripla \\(unifiedDeliveryDriver-provincia-deliveryDate) sia esattamente {int}")
    @And("verifica che la capacità disponibile per ogni tripla \\(unifiedDeliveryDriver-provincia-deliveryDate) sia almeno {int}")
    public void verificaCapacitaPredettaDaPrepareRequestDate(Integer capacitaMinimaAttesa, String compareToken) {

        List<DelayerPaperDelivery> notifiche = groupedByPkSubstring.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name());

        if (notifiche == null || notifiche.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica pianificata trovata.");
        }

        @EqualsAndHashCode(of = {"provincia", "driver", "deliveryDate"})
        @AllArgsConstructor
        class CapacityRequest {
            String provincia;
            String driver;
            String deliveryDate;
        }

        Set<CapacityRequest> requests =
                notifiche.stream()
                        .map(dpd -> {
                            String deliveryDate = extractDeliveryDate(dpd);
                            return new CapacityRequest(dpd.getProvince(), dpd.getUnifiedDeliveryDriver(), deliveryDate);
                        })
                        .collect(Collectors.toSet());


        // Verifica capacità per ogni deliveryDate
        for (CapacityRequest request : requests) {
            String driver = request.driver;
            String deliveryDate = request.deliveryDate;
            String provincia = request.provincia;

            String payload = String.format("""
                    {
                      "operationType": "GET_USED_CAPACITY",
                      "parameters": [ "%s", "%s", "%s" ]
                    }
                    """, driver, provincia, deliveryDate);

            try {
                String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
                checkLambdaResponse(rawResult, "GET_USED_CAPACITY");

                JsonNode body = objectMapper.readTree(rawResult).path("body");
                if (body.isTextual()) body = objectMapper.readTree(body.asText());

                int capacity = body.path("capacity").asInt(-1);
                int used = body.path("usedCapacity").asInt(-1);
                int available = capacity - used;

                log.info("Capacità driver {} su {} per {}: disponibile {} su {}", driver, provincia, deliveryDate, available, capacity);

                switch (compareToken) {
                    case "esattamente":
                        if (available <= capacitaMinimaAttesa) {
                            throw new AssertionError(String.format(
                                    "Capacità insufficiente per deliveryDate %s: attesi almeno %d slot liberi, disponibili %d",
                                    deliveryDate, capacitaMinimaAttesa, available));
                        }
                        break;

                    case "almeno":
                        if (available == capacitaMinimaAttesa) {
                            throw new AssertionError(String.format(
                                    "Capacità insufficiente per deliveryDate %s: attesi esattamente %d slot liberi, disponibili %d",
                                    deliveryDate, capacitaMinimaAttesa, available));
                        }
                        break;

                    default:
                        throw new AssertionError(String.format("Unrecognized compareToken: %s", compareToken));
                }

            } catch (Exception e) {
                throw new RuntimeException("Errore durante la verifica della capacità per deliveryDate: " + deliveryDate, e);
            }
        }
    }

    private LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data non valida: " + dateStr, e);
        }
    }

    private String extractDeliveryDate(DelayerPaperDelivery notifica) {
        String pk = notifica.getPk();

        if (pk == null || pk.isBlank()) {
            throw new IllegalStateException("Campo pk mancante o vuoto: " + pk);
        }

        String[] parts = pk.split("~");
        if (parts.length == 0) {
            throw new IllegalStateException("Formato pk non valido: " + pk);
        }

        String deliveryDate = parts[0];

        if (deliveryDate == null || deliveryDate.isBlank()) {
            throw new AssertionError(String.format("La notifica (requestId: %s) ha una pk vuota: %s", notifica.getRequestId(), pk));
        }

        return deliveryDate;
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
     * Termina dopo il numero massimo di tentativi.
     *
     * @param requestId   L'identificativo univoco del gruppo di notifiche da interrogare
     * @param maxAttempts Numero massimo di tentativi di polling
     * @param sleepMillis Intervallo di attesa tra un tentativo e l'altro (in millisecondi)
     * @return Lista di notifiche restituite dalla Lambda (come oggetti JsonNode)
     * @throws Exception in caso di errore nella risposta Lambda o nel parsing
     */
    private List<DelayerPaperDelivery> pollNotificheByRequestId(String requestId, Integer maxAttempts, Integer sleepMillis) throws Exception {
        int localMaxAttempts = maxAttempts == null ? 10 : maxAttempts;
        int localSleepMillis = sleepMillis == null ? 2000 : sleepMillis;

        for (int attempt = 1; attempt <= localMaxAttempts; attempt++) {
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
            if (!records.isEmpty()) {
                log.debug("Trovate {} notifiche al tentativo {}/{}", records.size(), attempt, maxAttempts);

                List<DelayerPaperDelivery> results = new ArrayList<>(records.size());
                for (JsonNode node : records) {
                    results.add(new DelayerPaperDelivery(node));
                }

                return results;
            }

            log.info("Nessuna notifica trovata (tentativo {}/{}). Riprovo tra {}ms...", attempt, maxAttempts, localSleepMillis);
            Thread.sleep(localSleepMillis);
        }

        log.warn("Polling esaurito: nessuna notifica trovata per requestId {}", requestId);
        return Collections.emptyList();
    }

    private boolean isMittenteCensito(String senderKey) {


        if (senderKey == null || senderKey.isBlank()) {
            throw new IllegalStateException("SenderKey mancante o vuoto: " + senderKey);
        }

        String[] parts = senderKey.split("~");
        if (parts.length == 0) {
            throw new IllegalStateException("Formato senderKey non valido: " + senderKey);
        }

        String paId = parts[0];

        if (paId == null || paId.isBlank()) {
            throw new AssertionError(String.format("La SenderKey (%s) ha una paId vuota: %s", senderKey, paId));
        }

        return !paId.equals("unknow");
    }

    private Map<String, List<DelayerPaperDelivery>> calculateExpectedWorkflowItems(WorkflowStep workflowStep) {
        Map<String, List<DelayerPaperDelivery>> groupedByStep = new HashMap<>();
        for (WorkflowStep step : WorkflowStep.values()) {
            groupedByStep.put(step.name(), new ArrayList<>());
        }

        Map<String, List<DelayerPaperDelivery>> bySenderKey = new HashMap<>();
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        List<DelayerPaperDelivery> mittentiNonCensiti = new ArrayList<>();
        List<DelayerPaperDelivery> notifiche = new ArrayList<>(actualCsv);

        // Tutte iniziano nello step iniziale
        groupedByStep.get(WorkflowStep.EVALUATE_SENDER_LIMIT.name()).addAll(notifiche);
        if (workflowStep.equals(WorkflowStep.EVALUATE_SENDER_LIMIT)) return groupedByStep;

        // I driver sono disponibili a partire dall'evento EVALUATE_DRIVER_CAPACITY, li considero da qui
        // Crea una mappa per accesso rapido alle notifiche originali
        Map<String, DelayerPaperDelivery> byRequestId = notifiche.stream()
                .collect(Collectors.toMap(d -> d.requestId, d -> d));

        // Itera sul gruppo e aggiorna se esiste una corrispondenza
        List<DelayerPaperDelivery> notificheConDriver = this.groupedByPkSubstring.get("EVALUATE_DRIVER_LIMIT");
        if (notificheConDriver != null && !notificheConDriver.isEmpty()) {
            for (DelayerPaperDelivery n : notificheConDriver) {
                DelayerPaperDelivery match = byRequestId.get(n.requestId);
                if (match != null) {
                    match.unifiedDeliveryDriver = n.unifiedDeliveryDriver;
                }
            }
        }

        // Raggruppa per mittente
        for (DelayerPaperDelivery n : notifiche) {
            String senderKey = String.join("~", n.getSenderPaId(), n.getProductType(), n.getProvince());
            bySenderKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).add(n);
        }

        // Applica limite mittente (prima censiti, poi non censiti)
        List<DelayerPaperDelivery> postSenderLimit = new ArrayList<>();

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : bySenderKey.entrySet()) {
            List<DelayerPaperDelivery> gruppo = prioritaDelayer(entry.getValue());

            if (isMittenteCensito(entry.getKey())) {
                postSenderLimit.addAll(gruppo.stream().limit(senderLimit).toList());
            } else {
                mittentiNonCensiti.addAll(gruppo); // salva per dopo
            }
        }

        // Raggruppa per recapitista
        for (DelayerPaperDelivery n : postSenderLimit) {
            String driverKey = String.join("~", n.getUnifiedDeliveryDriver(), n.getProvince());
            byDriverKey.computeIfAbsent(driverKey, k -> new ArrayList<>()).add(n);
        }

        // Calcola capacità residua per driver
        Map<String, Integer> driverResidua = new HashMap<>();
        for (String driverKey : byDriverKey.keySet()) {
            driverResidua.put(driverKey, Math.max(0, driverCapacity - byDriverKey.get(driverKey).size()));
        }

        // Assegna capacità residua ai non censiti (dopo i censiti)
        for (DelayerPaperDelivery n : mittentiNonCensiti) {
            String driverKey = String.join("~", n.getUnifiedDeliveryDriver(), n.getProvince());
            int available = driverResidua.getOrDefault(driverKey, 0);
            if (available > 0) {
                postSenderLimit.add(n);
                driverResidua.put(driverKey, available - 1);
            }
        }

        groupedByStep.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name()).addAll(postSenderLimit);
        if (workflowStep.equals(WorkflowStep.EVALUATE_DRIVER_CAPACITY)) return groupedByStep;

        // Raggruppa nuovamente per driver per il secondo filtro
        byDriverKey.clear();
        for (DelayerPaperDelivery n : postSenderLimit) {
            String driverKey = String.join("~", n.getUnifiedDeliveryDriver(), n.getProvince());
            byDriverKey.computeIfAbsent(driverKey, k -> new ArrayList<>()).add(n);
        }

        // Applica limite recapitista
        List<DelayerPaperDelivery> postDriverLimit = new ArrayList<>();
        for (List<DelayerPaperDelivery> gruppo : byDriverKey.values()) {
            postDriverLimit.addAll(prioritaDelayer(gruppo).stream().limit(driverCapacity).toList());
        }

        groupedByStep.get(WorkflowStep.EVALUATE_PRINT_CAPACITY.name()).addAll(postDriverLimit);
        if (workflowStep.equals(WorkflowStep.EVALUATE_PRINT_CAPACITY)) return groupedByStep;

        // Applica capacità di stampa
        List<DelayerPaperDelivery> postPrintLimit;
        if (printCapacity == null || printCapacity >= postDriverLimit.size()) {
            postPrintLimit = postDriverLimit;
        } else {
            postPrintLimit = prioritaDelayer(postDriverLimit).subList(0, printCapacity);
        }

        groupedByStep.get("SENT_TO_PREPARE_PHASE_2").addAll(postPrintLimit);

        return groupedByStep;
    }

    private List<DelayerPaperDelivery> prioritaDelayer(List<DelayerPaperDelivery> notifiche) {
        List<DelayerPaperDelivery> rs = new ArrayList<>();
        List<DelayerPaperDelivery> secondi = new ArrayList<>();
        List<DelayerPaperDelivery> altri = new ArrayList<>();

        for (DelayerPaperDelivery n : notifiche) {
            String tipo = n.getProductType();
            int att = Integer.parseInt(n.getAttempt());
            if ("RS".equalsIgnoreCase(tipo)) {
                rs.add(n);
            } else if (att == 1) {
                secondi.add(n);
            } else {
                altri.add(n);
            }
        }

        Comparator<DelayerPaperDelivery> byPrepare = Comparator.comparing(d -> parseDate(d.getPrepareRequestDate()));
        Comparator<DelayerPaperDelivery> byNotification = Comparator.comparing(d -> parseDate(d.getNotificationSentAt()));

        rs.sort(byPrepare);
        secondi.sort(byPrepare);
        altri.sort(byNotification);

        List<DelayerPaperDelivery> ordinati = new ArrayList<>();
        ordinati.addAll(rs);
        ordinati.addAll(secondi);
        ordinati.addAll(altri);
        return ordinati;
    }

    private Map<String, String> toComparableMap(DelayerPaperDelivery d, WorkflowStep workflowStep) {
        int wsIndex = workflowStep.getIndex();
        Map<String, String> result = new LinkedHashMap<>();
        result.put("pk", d.getPk());
        result.put("sk", d.getSk());
        result.put("requestId", d.getRequestId());
        result.put("notificationSentAt", d.getNotificationSentAt());
        result.put("prepareRequestDate", d.getPrepareRequestDate());
        result.put("productType", d.getProductType());
        result.put("senderPaId", d.getSenderPaId());
        result.put("province", d.getProvince());
        result.put("cap", d.getCap());
        result.put("attempt", d.getAttempt());
        result.put("iun", d.getIun());
        if (wsIndex > 0) result.put("unifiedDeliveryDriver", d.getUnifiedDeliveryDriver());
        return result;
    }

    private List<DelayerPaperDelivery> findByWorkflowStep(int expectedCount, WorkflowStep workflowStep, String deliveryDate) throws Exception {

        final int totalBudgetMillis = 900_000;
        final int pollingFrequency = 3000;
        final int maxTotalAttempts = totalBudgetMillis / pollingFrequency;
        final int maxAttempts = Math.max(1, maxTotalAttempts / this.actualCsv.size());

        Set<String> requestIdsDaTrovare = this.actualCsv.stream()
                .map(DelayerPaperDelivery::getRequestId)
                .collect(Collectors.toSet());

        Set<DelayerPaperDelivery> notificheTrovate = new LinkedHashSet<>();
        String stepKey = workflowStep.name();

        int attempt = 1;
        while (!requestIdsDaTrovare.isEmpty() && notificheTrovate.size() < expectedCount && attempt <= maxAttempts) {
            log.info("Tentativo {}/{} - RequestId rimanenti: {}", attempt, maxAttempts, requestIdsDaTrovare);

            Iterator<String> iterator = requestIdsDaTrovare.iterator();
            while (iterator.hasNext()) {
                String requestId = iterator.next();

                List<DelayerPaperDelivery> risultati = pollNotificheByRequestId(requestId, 3, null);
                String chosenDeliveryDate = (deliveryDate == null) ? this.expectedDeliveryDate : deliveryDate;

                try {
                    LocalDate.parse(chosenDeliveryDate, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Formato deliveryDate non valido. Atteso formato ISO 8601 'YYYY-MM-DD', es: 2025-08-04. Ricevuto: " + chosenDeliveryDate,
                            e
                    );
                }


                Optional<DelayerPaperDelivery> maybeRecord = risultati.stream()
                        .filter(r -> r.getPk().contains(stepKey) && r.getPk().contains(chosenDeliveryDate))
                        .findFirst();  // uno solo per combinazione requestId-step

                if (maybeRecord.isPresent()) {
                    DelayerPaperDelivery record = maybeRecord.get();
                    if (notificheTrovate.add(record)) {
                        //groupedByPkSubstring.get(stepKey).add(record);
                        iterator.remove();
                    }
                }
            }

            if (notificheTrovate.size() < expectedCount) {
                Thread.sleep(pollingFrequency);
            }

            attempt++;
        }

        int trovate = notificheTrovate.size();
        Assertions.assertEquals(
                expectedCount,
                trovate,
                String.format("Numero di notifiche trovate al workflow step '%s' diverso da quello atteso. Trovate: %d, Attese: %d",
                        stepKey, trovate, expectedCount)
        );

        return notificheTrovate.stream().toList();
    }

    private List<DelayerPaperDelivery> findCongelate(int expectedCount) throws Exception {
        LocalDate lunedi = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String deliveryDate = lunedi.toString();

        return findByWorkflowStep(expectedCount, WorkflowStep.EVALUATE_SENDER_LIMIT, deliveryDate);
    }

    private int calculateLimitByComparativo(String compare, int limit) {
        return switch (compare) {
            case "almeno", "esattamente" -> limit;
            case "inferiore" -> limit - 1;
            default -> throw new IllegalArgumentException("Il comparativo non è valido: " + compare);
        };
    }


}
