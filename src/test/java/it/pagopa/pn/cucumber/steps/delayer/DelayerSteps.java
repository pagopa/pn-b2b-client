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
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        private String priority;

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
            this.unifiedDeliveryDriver = requireField(rowMap, "unifiedDeliveryDriver");
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
            this.priority = requireField(tableRecord, "priority", true);
        }

        public DelayerPaperDelivery(DelayerPaperDelivery source) {
            this.pk = source.pk;
            this.sk = source.sk;
            this.requestId = source.requestId;
            this.notificationSentAt = source.notificationSentAt;
            this.prepareRequestDate = source.prepareRequestDate;
            this.productType = source.productType;
            this.senderPaId = source.senderPaId;
            this.province = source.province;
            this.cap = source.cap;
            this.attempt = source.attempt;
            this.iun = source.iun;
            this.unifiedDeliveryDriver = source.unifiedDeliveryDriver;
            this.priority = source.priority;
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

    @Getter
    enum WorkflowStep {
        EVALUATE_SENDER_LIMIT(0),
        EVALUATE_DRIVER_CAPACITY(1),
        EVALUATE_PRINT_CAPACITY(2),
        SENT_TO_PREPARE_PHASE_2(3),
        EVALUATE_RESIDUAL_CAPACITY(4);

        private final int index;

        WorkflowStep(int index) {
            this.index = index;
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
    private Integer printCapacity = 0;
    private String expectedDeliveryDate = null;
    private final Map<String, Integer> senderLimitMap = new HashMap<>();
    private final Map<String, Integer> driverCapacityMap = new HashMap<>();
    private final List<DelayerPaperDelivery> actualCsv = new ArrayList<>();

    // Refactoring seed
    Map<String, List<DelayerPaperDelivery>> groupedBySeed = new HashMap<>();
    Map<String, Map<String, List<DelayerPaperDelivery>>> expectedPianification = new HashMap<>();
    Map<String, Map<String, List<DelayerPaperDelivery>>> actualPianification = new HashMap<>();
    Map<String, String> failPianification = new HashMap<>();
    private Map<String, List<String>> priorityConfigMap;


    @Given("il CSV {string} contiene {int} notifiche distribuite tra i seguenti test case:")
    public void initParams(String csv, Integer expectedNotificationCount, DataTable dataTable) {
        readCsv(csv, expectedNotificationCount);
        initializeWorkflowStepMap();
        initializeExpectedDeliveryDate();
        initializeSenderAndDriverMaps();
        initializeSeedMaps(dataTable);
        initializePriorityConfigMap();
    }

    @And("si presuppone che il limite {word} settimanale \\(paId-product_type-province) sia:")
    @And("si verifica che il limite {word} unificato settimanale \\(unifiedDeliveryDriver-province) sia:")
    public void initSenderOrDriverLimit(String subject, DataTable dataTable) {
        boolean isMittente;
        if ("mittente".equalsIgnoreCase(subject)) {
            isMittente = true;
        } else if ("recapitista".equalsIgnoreCase(subject)) {
            isMittente = false;
        } else {
            throw new IllegalArgumentException("Subject non valido: " + subject);
        }

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String idKey = isMittente ? "senderId" : "unifiedDeliveryDriverId";
            String entityId = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit;

            try {
                rawLimit = Integer.parseInt(row.get("limit"));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valore limite non numerico per " + entityId + ": " + row.get("limit"), e);
            }

            int calculatedLimit = calculateLimitByComparativo(comparative, rawLimit);
            if (calculatedLimit < 0) {
                throw new IllegalArgumentException("Comparativo non valido per " + entityId + ": " + comparative);
            }

            log.debug("Letto limite {}: {} {} -> {}", subject, entityId, comparative, calculatedLimit);

            Map<String, Integer> targetMap = isMittente ? senderLimitMap : driverCapacityMap;

            if (!targetMap.containsKey(entityId)) {
                log.warn((isMittente ? "senderId" : "driverId") +
                        " non presente nel file CSV caricato: " + entityId);
            }

            targetMap.put(entityId, calculatedLimit);

            if (!isMittente) {
                List<DelayerPaperDelivery> notifiche = getExpectedNotification(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name());
                validateDriverCapacitiesViaLambda(notifiche, dataTable);
            }
        }
    }

    private void validateDriverCapacitiesViaLambda(List<DelayerPaperDelivery> notifiche, DataTable dataTable) {
        // Mappa driverKey -> notifiche
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        byDriverKey(notifiche, byDriverKey);

        // Map driverKey -> (comparative, expectedLimit)
        Map<String, ComparativeLimit> expectedLimits = new HashMap<>();
        for (Map<String, String> row : dataTable.asMaps()) {
            String driverKey = row.get("unifiedDeliveryDriverId");
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            expectedLimits.put(driverKey, new ComparativeLimit(comparative, rawLimit));
        }

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : byDriverKey.entrySet()) {
            String driverKey = entry.getKey();
            ComparativeLimit limitInfo = expectedLimits.get(driverKey);

            if (limitInfo == null) {
                throw new IllegalStateException("Nessun limite definito in feature file per driver: " + driverKey);
            }

            String[] parts = driverKey.split("~");
            if (parts.length != 2) {
                throw new IllegalStateException("Formato driverKey non valido: " + driverKey);
            }

            String driverId = parts[0];
            String provincia = parts[1];
            String deliveryDate = expectedDeliveryDate;

            String payload = String.format("""
                    {
                      "operationType": "GET_USED_CAPACITY",
                      "parameters": [ "%s", "%s", "%s" ]
                    }
                    """, driverId, provincia, deliveryDate);

            try {
                String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
                checkLambdaResponse(rawResult, "GET_USED_CAPACITY");

                JsonNode body = objectMapper.readTree(rawResult).path("body");
                if (body.isTextual()) {
                    body = objectMapper.readTree(body.asText());
                }

                int declared = body.path("declaredCapacity").asInt(-1);
                int used = body.path("usedCapacity").asInt(-1);
                int available = declared - used;

                log.info("Driver {} - Capacità: dichiarata {}, usata {}, disponibile {}", driverKey, declared, used, available);

                switch (limitInfo.comparative.toLowerCase()) {
                    case "almeno":
                        if (available < limitInfo.expected) {
                            throw new AssertionError(String.format(
                                    "Driver %s - attesi almeno %d slot disponibili, trovati %d",
                                    driverKey, limitInfo.expected, available));
                        }
                        break;
                    case "esattamente":
                        if (available != limitInfo.expected) {
                            throw new AssertionError(String.format(
                                    "Driver %s - attesi esattamente %d slot disponibili, trovati %d",
                                    driverKey, limitInfo.expected, available));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Comparativo non supportato: " + limitInfo.comparative);
                }

            } catch (Exception e) {
                throw new RuntimeException("Errore durante la verifica via Lambda per driver: " + driverKey, e);
            }
        }
    }

    private static class ComparativeLimit {
        final String comparative;
        final int expected;

        ComparativeLimit(String comparative, int expected) {
            this.comparative = comparative;
            this.expected = expected;
        }
    }

    @And("si presuppone che la capacità di stampa giornaliera sia {word} {int}")
    public void initPrintCapacity(String compare, int limit) {
        printCapacity = calculateLimitByComparativo(compare, limit);
        if (printCapacity < 0) throw new IllegalArgumentException("PrintCapacity non valido");
    }

    @Given("il CSV {string} è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test")
    public void populateTargetTable(String csvName) throws Exception {
        String payload = String.format("""
                {
                  "operationType": "IMPORT_DATA",
                  "parameters": ["%s"]
                }
                """, csvName);

        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "IMPORT_DATA");
        log.info("Importazione CSV [{}] completata correttamente", csvName);
    }

    @And("viene simulato internamente l'algoritmo di pianificazione")
    public void runSimulation() {
        expectedPianification.replaceAll((seed, oldStepMap) ->
                simulateAlgorithm(WorkflowStep.SENT_TO_PREPARE_PHASE_2, seed)
        );
    }

    @When("viene avviato l'algoritmo tramite lambda")
    public void runAlgorithm() throws Exception {
        String payload = """
                {
                  "operationType": "RUN_ALGORITHM",
                  "parameters": []
                }
                """;

        String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        checkLambdaResponse(rawResult, "RUN_ALGORITHM");
        log.debug("Algoritmo avviato correttamente.");
    }

    @Then("vengono recuperate le notifiche al workflow step {string}")
    public void fetchNotification(String ws) throws Exception {
        WorkflowStep workflowStep = WorkflowStep.valueOf(ws);
        List<DelayerPaperDelivery> toFind = getExpectedNotification(workflowStep.name());
        List<DelayerPaperDelivery> notifiche = findByWorkflowStep(toFind, workflowStep, expectedDeliveryDate);

        updateActualPianification(workflowStep, notifiche);
    }

    private void updateActualPianification(WorkflowStep workflowStep, List<DelayerPaperDelivery> notifiche) {
        Map<String, List<DelayerPaperDelivery>> groupedBySeed = bySeed(notifiche);

        // 1. Identifica i seed mancanti
        Set<String> missingSeeds = new HashSet<>(this.groupedBySeed.keySet());
        missingSeeds.removeAll(groupedBySeed.keySet());

        // 2. Aggiorna actualPianification e rileva mismatch
        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : groupedBySeed.entrySet()) {
            String seed = entry.getKey();
            List<DelayerPaperDelivery> updatedList = entry.getValue();

            List<DelayerPaperDelivery> actualList = this.actualPianification
                    .getOrDefault(seed, Collections.emptyMap())
                    .get(workflowStep.name());

            if (actualList != null) {
                actualList.clear();
                actualList.addAll(updatedList);
            }

            List<DelayerPaperDelivery> expectedList = this.expectedPianification
                    .getOrDefault(seed, Collections.emptyMap())
                    .get(workflowStep.name());

            if (expectedList == null) {
                String errorMsg = String.format(
                        "Seed '%s': pianificazione attesa assente per lo step '%s'.",
                        seed, workflowStep.name()
                );
                registerFailureIfAbsent(seed, errorMsg);

            } else if (actualList == null || expectedList.size() != actualList.size()) {
                String errorMsg = String.format(
                        "Seed '%s': mismatch nel numero di notifiche per lo step '%s' - Attese: %d, Trovate: %d",
                        seed,
                        workflowStep.name(),
                        expectedList.size(),
                        actualList == null ? 0 : actualList.size()
                );
                registerFailureIfAbsent(seed, errorMsg);
            }
        }

        // 3. Segnala i seed completamente mancanti
        for (String missingSeed : missingSeeds) {
            String errorMsg = String.format(
                    "Seed '%s': nessuna notifica trovata per lo step '%s' (seed completamente mancante).",
                    missingSeed, workflowStep.name()
            );
            registerFailureIfAbsent(missingSeed, errorMsg);
        }

        // 4. Verifica se tutti i seed hanno fallito
        if (failPianification.keySet().containsAll(this.groupedBySeed.keySet())) {
            assertPianifications();
        }
    }

    private void registerFailureIfAbsent(String seed, String errorMsg) {
        failPianification.putIfAbsent(seed, errorMsg);
    }

    private Map<String, List<DelayerPaperDelivery>> bySeed(List<DelayerPaperDelivery> notifiche) {
        Map<String, List<DelayerPaperDelivery>> groupedBySeed = new HashMap<>();

        for (DelayerPaperDelivery delivery : notifiche) {
            if (delivery.requestId == null) continue;

            Optional<String> maybeSeed = this.groupedBySeed.keySet().stream()
                    .filter(delivery.requestId::contains)
                    .findFirst();

            if (maybeSeed.isPresent()) {
                String seed = maybeSeed.get();
                groupedBySeed
                        .computeIfAbsent(seed, k -> new ArrayList<>())
                        .add(delivery);
            } else {
                throw new RuntimeException(String.format("RequestId '%s' does not match any known seed", delivery.requestId));
            }
        }

        return groupedBySeed;
    }

    private List<DelayerPaperDelivery> getExpectedNotification(String workflowStep) {
        List<DelayerPaperDelivery> expected = new ArrayList<>();

        expectedPianification.forEach((seed, pianification) -> {
            if (!failPianification.containsKey(seed)) expected.addAll(pianification.get(workflowStep));
        });

        return expected;
    }

    @Then("verifica che il processo fino al workflow step {string} abbia rispettato i criteri di ranking per almeno un test case:")
    public void checkRanking(String ws, DataTable expectedOrder) {
        WorkflowStep workflowStep = WorkflowStep.valueOf(ws);

        Set<String> seedsToCheck = this.groupedBySeed.keySet().stream()
                .filter(seed -> !failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (seedsToCheck.isEmpty()) {
            throw new IllegalStateException("Tutti i seed hanno fallito lo step di pianificazione: " + workflowStep.name());
        }

        boolean almenoUnSeedValido = false;

        for (String seed : seedsToCheck) {
            Map<String, List<DelayerPaperDelivery>> expectedMap = expectedPianification.get(seed);
            Map<String, List<DelayerPaperDelivery>> actualMap = actualPianification.get(seed);

            if (expectedMap == null) {
                putFailIfAbsent(seed, "Seed '%s': pianificazione attesa mancante per lo step '%s'.".formatted(seed, workflowStep.name()));
                continue;
            }

            if (actualMap == null) {
                putFailIfAbsent(seed, "Seed '%s': pianificazione reale mancante per lo step '%s'.".formatted(seed, workflowStep.name()));
                continue;
            }

            List<DelayerPaperDelivery> expected = expectedMap.get(workflowStep.name());
            List<DelayerPaperDelivery> actual = actualMap.get(workflowStep.name());

            if (expected == null || expected.isEmpty()) {
                putFailIfAbsent(seed, "Seed '%s': notifiche attese vuote o nulle per lo step '%s'.".formatted(seed, workflowStep.name()));
                continue;
            }

            if (actual == null || actual.isEmpty()) {
                putFailIfAbsent(seed, "Seed '%s': notifiche reali vuote o nulle per lo step '%s'.".formatted(seed, workflowStep.name()));
                continue;
            }

            // Ranking check
            List<Map<String, String>> expectedRank = expected.stream()
                    .map(n -> toComparableMap(n, workflowStep))
                    .toList();

            List<Map<String, String>> actualRank = actual.stream()
                    .map(n -> toComparableMap(n, workflowStep))
                    .toList();

            try {
                Assertions.assertEquals(expectedRank, actualRank,
                        () -> "Ranking diverso per seed: " + seed + "\nExpected: " + expectedRank + "\nActual: " + actualRank);
                almenoUnSeedValido = true;
            } catch (AssertionError e) {
                putFailIfAbsent(seed, "Seed '%s': ordine delle notifiche non rispettato allo step '%s'.%nDettaglio: %s".formatted(seed, workflowStep.name(), e.getMessage()));
            }
        }

        if (!almenoUnSeedValido) {
            assertPianifications();
        }
    }

    private void putFailIfAbsent(String seed, String errorMsg) {
        if (!failPianification.containsKey(seed)) {
            failPianification.put(seed, errorMsg);
        }
    }

    @Then("verifica che le opportune notifiche siano state congelate e ricaricate con workflow step {string} e deliveryDate alla settimana seguente per almeno un test case")
    public void checkFrozen(String ws) {
        WorkflowStep workflowStep = WorkflowStep.valueOf(ws);
        Set<String> validSeeds = this.groupedBySeed.keySet().stream()
                .filter(seed -> !failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            assertPianifications();
        }

        List<DelayerPaperDelivery> allExpectedFrozen = new ArrayList<>();
        List<DelayerPaperDelivery> allActualFrozen;

        try {
            allActualFrozen = findCongelate(getExpectedNotification("FROZEN"));
        } catch (Exception e) {
            validSeeds.forEach(seed ->
                    registerFailureIfAbsent(seed, "Seed '%s': errore durante il recupero delle notifiche congelate: %s"
                            .formatted(seed, e.getMessage()))
            );
            return;
        }

        for (String seed : validSeeds) {
            Map<String, List<DelayerPaperDelivery>> expectedMap = expectedPianification.get(seed);
            Map<String, List<DelayerPaperDelivery>> actualMap = actualPianification.get(seed);

            if (expectedMap == null || actualMap == null) continue;

            List<DelayerPaperDelivery> expectedFrozen = expectedMap.get("FROZEN");
            List<DelayerPaperDelivery> actualFrozen = actualMap.get("FROZEN");

            if (expectedFrozen != null) allExpectedFrozen.addAll(expectedFrozen);
            if (actualFrozen != null) allActualFrozen.addAll(actualFrozen);
        }

        if (allExpectedFrozen.size() != allActualFrozen.size()) {
            validSeeds.forEach(seed -> registerFailureIfAbsent(seed,
                    "Seed '%s': numero di notifiche congelate non corrispondente. Attese: %d, Trovate: %d"
                            .formatted(seed, allExpectedFrozen.size(), allActualFrozen.size())));
            return;
        }

        Set<Map<String, String>> expectedSet = allExpectedFrozen.stream()
                .map(n -> toComparableMap(n, workflowStep))
                .collect(Collectors.toSet());

        Set<Map<String, String>> missing = allActualFrozen.stream()
                .map(n -> toComparableMap(n, workflowStep)).collect(Collectors.toSet());
        missing.removeAll(expectedSet);

        if (!missing.isEmpty()) {
            validSeeds.forEach(seed -> registerFailureIfAbsent(seed,
                    "Seed '%s': notifiche congelate non corrispondono a quelle attese.%nDifferenze: %s"
                            .formatted(seed, missing)));
        }
    }

    @Given("verifica che la capacità disponibile per ogni tripla \\(unifiedDeliveryDriver-provincia-deliveryDate) sia {word} {int}")
    public void checkDriverCapacity(String compareToken, Integer capacitaMinimaAttesa) {
        List<DelayerPaperDelivery> notifiche = getExpectedNotification(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name());

        if (notifiche.isEmpty()) {
            throw new IllegalStateException("Nessuna notifica pianificata trovata per EVALUATE_DRIVER_CAPACITY.");
        }

        // Raggruppa notifiche per seed
        Map<String, List<DelayerPaperDelivery>> bySeed = bySeed(notifiche);

        // Filtro solo i seed validi
        Set<String> validSeeds = bySeed.keySet().stream()
                .filter(seed -> !failPianification.containsKey(seed))
                .collect(Collectors.toSet());

        if (validSeeds.isEmpty()) {
            throw new IllegalStateException("Tutti i seed hanno fallito: nessuna capacità da verificare.");
        }

        @EqualsAndHashCode(of = {"provincia", "driver", "deliveryDate"})
        @AllArgsConstructor
        class CapacityRequest {
            String provincia;
            String driver;
            String deliveryDate;
        }

        for (String seed : validSeeds) {
            List<DelayerPaperDelivery> seedNotifiche = bySeed.get(seed);

            Set<CapacityRequest> requests = seedNotifiche.stream()
                    .map(dpd -> new CapacityRequest(
                            dpd.getProvince(),
                            dpd.getUnifiedDeliveryDriver(),
                            extractDeliveryDate(dpd)))
                    .collect(Collectors.toSet());

            for (CapacityRequest request : requests) {
                String payload = String.format("""
                        {
                          "operationType": "GET_USED_CAPACITY",
                          "parameters": [ "%s", "%s", "%s" ]
                        }
                        """, request.driver, request.provincia, request.deliveryDate);

                try {
                    String rawResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
                    checkLambdaResponse(rawResult, "GET_USED_CAPACITY");

                    JsonNode body = objectMapper.readTree(rawResult).path("body");
                    if (body.isTextual()) {
                        body = objectMapper.readTree(body.asText());
                    }

                    int capacity = body.path("declaredCapacity").asInt(-1);
                    int used = body.path("usedCapacity").asInt(-1);
                    int available = capacity - used;

                    log.info("Seed [{}] - Capacità driver {} su {} per {}: disponibile {} su {}",
                            seed, request.driver, request.provincia, request.deliveryDate, available, capacity);

                    switch (compareToken.toLowerCase()) {
                        case "esattamente":
                            if (available != capacitaMinimaAttesa) {
                                throw new AssertionError(String.format(
                                        "Seed [%s]: capacità esatta non rispettata per deliveryDate %s. Attesi: %d, Disponibili: %d",
                                        seed, request.deliveryDate, capacitaMinimaAttesa, available));
                            }
                            break;

                        case "almeno":
                            if (available < capacitaMinimaAttesa) {
                                throw new AssertionError(String.format(
                                        "Seed [%s]: capacità minima non rispettata per deliveryDate %s. Attesi almeno: %d, Disponibili: %d",
                                        seed, request.deliveryDate, capacitaMinimaAttesa, available));
                            }
                            break;

                        default:
                            throw new AssertionError("Compare token non riconosciuto: " + compareToken);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Errore durante la verifica della capacità per seed '" + seed +
                            "', deliveryDate: " + request.deliveryDate, e);
                }
            }
        }
    }

    @Then("verifica la corretta pianificazione di ogni test case")
    public void assertPianifications() {
        if (!failPianification.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Pianificazione fallita per i seguenti seed:\n");

            failPianification.forEach((seed, error) -> {
                message.append("• Seed: ").append(seed).append("\n")
                        .append("  Errore: ").append(error).append("\n\n");
            });

            Assertions.fail(message.toString());
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

    /*
    private Map<String, List<DelayerPaperDelivery>> simulateAlgorithm(WorkflowStep endAt, String seed) {
        Map<String, List<DelayerPaperDelivery>> groupedByStep = new HashMap<>();
        Map<String, List<DelayerPaperDelivery>> frozenByStep = new HashMap<>();

        // Inizializza gruppi per ogni step + frozenByStep
        for (WorkflowStep step : WorkflowStep.values()) {
            groupedByStep.put(step.name(), new ArrayList<>());
            frozenByStep.put(step.name(), new ArrayList<>());
        }

        Map<String, List<DelayerPaperDelivery>> bySenderKey = new HashMap<>();
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        List<DelayerPaperDelivery> mittentiNonCensiti = new ArrayList<>();
        List<DelayerPaperDelivery> notifiche = new ArrayList<>(groupedBySeed.get(seed));

        if (notifiche.isEmpty())
            throw new RuntimeException("");

        // Tutte iniziano nello step iniziale
        groupedByStep.get(WorkflowStep.EVALUATE_SENDER_LIMIT.name())
                .addAll(
                        notifiche.stream()
                                .map(DelayerPaperDelivery::new)
                                .peek(n -> {
                                    n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_SENDER_LIMIT, this.expectedDeliveryDate);
                                    n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_SENDER_LIMIT, n);
                                    n.priority = calculatePriority(n);
                                })
                                .toList()
                );

        if (endAt.equals(WorkflowStep.EVALUATE_SENDER_LIMIT)) {
            groupedByStep.put("FROZEN", List.of());
            return groupedByStep;
        }

        // Raggruppa per mittente
        for (DelayerPaperDelivery n : notifiche) {
            String senderKey = getSenderKey(n);
            bySenderKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).add(n);
        }

        // Applica limite mittente (prima censiti, poi non censiti)
        List<DelayerPaperDelivery> postSenderLimit = new ArrayList<>();

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : bySenderKey.entrySet()) {
            List<DelayerPaperDelivery> gruppo = prioritaDelayer(entry.getValue());

            if (isMittenteCensito(entry.getKey())) {
                int senderLimit = getSenderLimit(entry);

                postSenderLimit.addAll(gruppo.stream().limit(senderLimit).toList());

                frozenByStep.get(WorkflowStep.EVALUATE_SENDER_LIMIT.name()).addAll(
                        gruppo.stream()
                                .skip(senderLimit)
                                .map(DelayerPaperDelivery::new)
                                .peek(n -> {
                                    n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_SENDER_LIMIT, this.expectedDeliveryDate);
                                    n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_SENDER_LIMIT, n);
                                    n.priority = calculatePriority(n);
                                })
                                .toList()
                );
            } else {
                mittentiNonCensiti.addAll(gruppo); // salva per dopo
            }
        }

        // Raggruppa per recapitista
        byDriverKey(postSenderLimit, byDriverKey);

        // Calcola capacità residua per driver
        Map<String, Integer> driverResidua = new HashMap<>();
        for (String driverKey : byDriverKey.keySet()) {
            Integer driverCapacity = getDriverCapacity(driverKey);
            driverResidua.put(driverKey, Math.max(0, driverCapacity - byDriverKey.get(driverKey).size()));
        }

        // Assegna capacità residua ai non censiti (dopo i censiti)
        for (DelayerPaperDelivery n : mittentiNonCensiti) {
            String driverKey = getDriverKey(n);
            int available = driverResidua.getOrDefault(driverKey, 0);
            if (available > 0) {
                postSenderLimit.add(n);
                driverResidua.put(driverKey, available - 1);
            } else {
                DelayerPaperDelivery not = new DelayerPaperDelivery(n);
                n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_SENDER_LIMIT, expectedDeliveryDate);
                n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_SENDER_LIMIT, n);
                n.priority = calculatePriority(n);
                frozenByStep.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name()).add(not);
            }
        }

        groupedByStep.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name())
                .addAll(
                        postSenderLimit
                                .stream()
                                .peek(n -> {
                                    n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_DRIVER_CAPACITY, this.expectedDeliveryDate);
                                    n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_DRIVER_CAPACITY, n);
                                    n.priority = calculatePriority(n);
                                })
                                .map(DelayerPaperDelivery::new).toList()
                );

        if (endAt.equals(WorkflowStep.EVALUATE_DRIVER_CAPACITY)) {
            groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
            return groupedByStep;
        }

        // Raggruppa nuovamente per driver per il secondo filtro
        byDriverKey.clear();
        byDriverKey(postSenderLimit, byDriverKey);

        // Applica limite recapitista
        List<DelayerPaperDelivery> postDriverLimit = new ArrayList<>();

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : byDriverKey.entrySet()) {
            String driverId = entry.getKey();
            List<DelayerPaperDelivery> gruppo = entry.getValue();

            List<DelayerPaperDelivery> ordinati = prioritaDelayer(gruppo);
            int driverCapacity = getDriverCapacity(driverId);

            // Prende solo fino alla capacità
            postDriverLimit.addAll(ordinati.stream().limit(driverCapacity).toList());

            // Congela il resto per lo step EVALUATE_DRIVER_CAPACITY
            frozenByStep.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name()).addAll(
                    ordinati.stream()
                            .skip(driverCapacity)
                            .peek(n -> {
                                n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_SENDER_LIMIT, this.expectedDeliveryDate);
                                n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_SENDER_LIMIT, n);
                                n.priority = calculatePriority(n);
                            })
                            .map(DelayerPaperDelivery::new).toList()
            );
        }


        groupedByStep.get(WorkflowStep.EVALUATE_PRINT_CAPACITY.name())
                .addAll(
                        postDriverLimit.stream()
                                .peek(n -> {
                                    n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_PRINT_CAPACITY, this.expectedDeliveryDate);
                                    n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_PRINT_CAPACITY, n);
                                    n.priority = calculatePriority(n);
                                })
                                .map(DelayerPaperDelivery::new).toList()
                );

        if (endAt.equals(WorkflowStep.EVALUATE_PRINT_CAPACITY)) {
            groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
            return groupedByStep;
        }

        // Applica capacità di stampa
        List<DelayerPaperDelivery> postPrintLimit;
        List<DelayerPaperDelivery> ordinati = prioritaDelayer(postDriverLimit);

        if (printCapacity == null || printCapacity >= ordinati.size()) {
            postPrintLimit = ordinati;
        } else {
            postPrintLimit = ordinati.subList(0, printCapacity);
            frozenByStep.get(WorkflowStep.EVALUATE_PRINT_CAPACITY.name()).addAll(
                    ordinati.subList(printCapacity, ordinati.size()).stream()
                            .peek(n -> {
                                n.pk = calculateNotificationPk(WorkflowStep.EVALUATE_SENDER_LIMIT, this.expectedDeliveryDate);
                                n.sk = calculateNotificationSk(WorkflowStep.EVALUATE_SENDER_LIMIT, n);
                                n.priority = calculatePriority(n);
                            })
                            .map(DelayerPaperDelivery::new).toList()
            );
        }


        groupedByStep.get(WorkflowStep.SENT_TO_PREPARE_PHASE_2.name())
                .addAll(
                        postPrintLimit.stream()
                                .peek(n -> {
                                    n.pk = calculateNotificationPk(WorkflowStep.SENT_TO_PREPARE_PHASE_2, this.expectedDeliveryDate);
                                    n.sk = calculateNotificationSk(WorkflowStep.SENT_TO_PREPARE_PHASE_2, n);
                                    n.priority = calculatePriority(n);
                                })
                                .map(DelayerPaperDelivery::new).toList()
                );

        // Congelati finali aggregati da tutti gli step
        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));

        return groupedByStep;
    }
     */

    private Map<String, List<DelayerPaperDelivery>> simulateAlgorithm(WorkflowStep endAt, String seed) {
        Map<String, List<DelayerPaperDelivery>> groupedByStep = initWorkflowMap();
        Map<String, List<DelayerPaperDelivery>> frozenByStep = initWorkflowMap();

        List<DelayerPaperDelivery> notifications = new ArrayList<>(groupedBySeed.get(seed));
        if (notifications.isEmpty()) {
            throw new RuntimeException("Nessuna notifica trovata per il seed " + seed);
        }

        // Step 1: Sender Limit
        var pairResult = applySenderLimit(notifications, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> toDriverCapacity = pairResult.getLeft();
        List<DelayerPaperDelivery> toResidualCapacity = pairResult.getRight();
        if (endAt == WorkflowStep.EVALUATE_SENDER_LIMIT) return finalizeResult(groupedByStep, frozenByStep);

        // Step 2a: Residual Capacity 
        List<DelayerPaperDelivery> residualNotifications = applyResidualCapacity(toResidualCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowStep.EVALUATE_RESIDUAL_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);
        
        // Step 2b: Driver Capacity
        List<DelayerPaperDelivery> postDriverCapacity = applyDriverCapacity(toDriverCapacity, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> processedResidues = applyDriverCapacity(residualNotifications, groupedByStep, frozenByStep);
        List<DelayerPaperDelivery> toPrintCapacity = new ArrayList<>(postDriverCapacity);
        toPrintCapacity.addAll(processedResidues);
        if (endAt == WorkflowStep.EVALUATE_DRIVER_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 3: Print Capacity
        List<DelayerPaperDelivery> toPreparePhase2 = applyPrintCapacity(toPrintCapacity, groupedByStep, frozenByStep);
        if (endAt == WorkflowStep.EVALUATE_PRINT_CAPACITY) return finalizeResult(groupedByStep, frozenByStep);

        // Step 4: Sent to Prepare Phase 2
        groupedByStep.get(WorkflowStep.SENT_TO_PREPARE_PHASE_2.name())
                .addAll(deepCopyAndUpdateKeys(toPreparePhase2, WorkflowStep.SENT_TO_PREPARE_PHASE_2, this.expectedDeliveryDate));

        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private List<DelayerPaperDelivery> applyResidualCapacity(List<DelayerPaperDelivery> toResidualCapacity, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        groupedByStep.get(WorkflowStep.EVALUATE_RESIDUAL_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(toResidualCapacity, WorkflowStep.EVALUATE_RESIDUAL_CAPACITY, this.expectedDeliveryDate));

        return toResidualCapacity;
    }

    private Map<String, List<DelayerPaperDelivery>> initWorkflowMap() {
        Map<String, List<DelayerPaperDelivery>> map = new HashMap<>();
        for (WorkflowStep step : WorkflowStep.values()) {
            map.put(step.name(), new ArrayList<>());
        }
        return map;
    }

    private Map<String, List<DelayerPaperDelivery>> finalizeResult(Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        groupedByStep.put("FROZEN", collectAllFrozen(frozenByStep));
        return groupedByStep;
    }

    private List<DelayerPaperDelivery> deepCopyAndUpdateKeys(List<DelayerPaperDelivery> source, WorkflowStep step, String deliveryDate) {
        return source.stream()
                .map(DelayerPaperDelivery::new)
                .peek(n -> {
                    n.pk = calculateNotificationPk(step, deliveryDate);
                    n.sk = calculateNotificationSk(step, n);
                    n.priority = calculatePriority(n);
                })
                .toList();
    }

    private void freezeNotifications(List<DelayerPaperDelivery> list, WorkflowStep step, Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        String deliveryDate = getNextWeekMonday();
        frozenByStep.get(step.name()).addAll(deepCopyAndUpdateKeys(list, WorkflowStep.EVALUATE_SENDER_LIMIT, deliveryDate));
    }

    private String getNextWeekMonday() {
        LocalDate today = LocalDate.now();
        LocalDate nextMonday = today
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        return nextMonday.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private Pair<List<DelayerPaperDelivery>,List<DelayerPaperDelivery>> applySenderLimit(List<DelayerPaperDelivery> notifications, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> postSenderLimit = new ArrayList<>();
        List<DelayerPaperDelivery> toResidualCapacity = new ArrayList<>();

        // 1. Tutto parte in EVALUTE_SENDER_LIMIT
        groupedByStep.get(WorkflowStep.EVALUATE_SENDER_LIMIT.name())
                .addAll(deepCopyAndUpdateKeys(notifications, WorkflowStep.EVALUATE_SENDER_LIMIT, this.expectedDeliveryDate));

        // 2. Separa RS e secondi tentativi
        List<DelayerPaperDelivery> rsOrSecondAttempt = notifications.stream()
                .filter(n -> isRS(n) || isSecondAttempt(n))
                .toList();

        List<DelayerPaperDelivery> toEvaluateNormally = notifications.stream()
                .filter(n -> !(isRS(n) || isSecondAttempt(n)))
                .toList();

        // 3. RS e secondi tentativi vanno direttamente allo step DRIVER_CAPACITY
        postSenderLimit.addAll(deepCopyAndUpdateKeys(rsOrSecondAttempt,WorkflowStep.EVALUATE_DRIVER_CAPACITY, this.expectedDeliveryDate));

        //4. Gli 890 vengono processati per mittente censito e non
        Map<String, List<DelayerPaperDelivery>> bySenderKey = groupBySender(toEvaluateNormally);

        for (Map.Entry<String, List<DelayerPaperDelivery>> entry : bySenderKey.entrySet()) {
            List<DelayerPaperDelivery> sorted = sortByPriority(entry.getValue());

            if (isMittenteCensito(entry.getKey())) {
                int senderLimit = getSenderLimit(entry);
                postSenderLimit.addAll(deepCopyAndUpdateKeys(sorted.stream().limit(senderLimit).toList(), WorkflowStep.EVALUATE_DRIVER_CAPACITY, this.expectedDeliveryDate));
                toResidualCapacity.addAll(deepCopyAndUpdateKeys(sorted.stream().skip(senderLimit).toList(), WorkflowStep.EVALUATE_RESIDUAL_CAPACITY, this.expectedDeliveryDate));
            } else {
                toResidualCapacity.addAll(deepCopyAndUpdateKeys(sorted, WorkflowStep.EVALUATE_RESIDUAL_CAPACITY, this.expectedDeliveryDate));
            }
        }

        return Pair.of(postSenderLimit, toResidualCapacity);
    }

    private boolean isSecondAttempt(DelayerPaperDelivery n) {
        return Integer.parseInt(n.getAttempt()) == 2;
    }

    private boolean isRS(DelayerPaperDelivery n) {
        return n.getProductType().equalsIgnoreCase("RS");
    }

    private List<DelayerPaperDelivery> applyDriverCapacity(List<DelayerPaperDelivery> toEvaluate, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        // 1. Raggruppa per driver
        Map<String, List<DelayerPaperDelivery>> byDriverKey = groupByDriver(toEvaluate);
        Map<String, Integer> driverResidualCapacity = new HashMap<>();

        // 2. Calcola capacità residua per ogni driver (inizialmente tutta disponibile)
        for (String driverKey : byDriverKey.keySet()) {
            int capacity = getDriverCapacity(driverKey);
            driverResidualCapacity.put(driverKey, capacity);
        }

        List<DelayerPaperDelivery> assigned = new ArrayList<>();
        List<DelayerPaperDelivery> toFreeze = new ArrayList<>();

        // 3. Processa PRIMA i mittenti censiti
        List<DelayerPaperDelivery> censiti = toEvaluate.stream()
                .filter(n -> isMittenteCensito(getSenderKey(n)))
                .toList();

        for (DelayerPaperDelivery n : sortByPriority(censiti)) {
            assigned.add(n);
            String driverKey = getDriverKey(n);
            int remaining = driverResidualCapacity.getOrDefault(driverKey, 0);
            driverResidualCapacity.put(driverKey, Math.max(0, remaining - 1));
        }

        // 4. Poi processa i mittenti NON censiti
        List<DelayerPaperDelivery> nonCensiti = toEvaluate.stream()
                .filter(n -> !isMittenteCensito(getSenderKey(n)))
                .toList();

        for (DelayerPaperDelivery n : sortByPriority(nonCensiti)) {
            String driverKey = getDriverKey(n);
            int available = driverResidualCapacity.getOrDefault(driverKey, 0);

            if (available > 0) {
                assigned.add(n);
                driverResidualCapacity.put(driverKey, available - 1);
            } else {
                toFreeze.add(n);
            }
        }

        // 5. Aggiorna grouped e frozen con le notifiche elaborate
        groupedByStep.get(WorkflowStep.EVALUATE_DRIVER_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(assigned, WorkflowStep.EVALUATE_DRIVER_CAPACITY, this.expectedDeliveryDate));

        freezeNotifications(toFreeze, WorkflowStep.EVALUATE_DRIVER_CAPACITY, frozenByStep);

        return assigned;
    }

    private List<DelayerPaperDelivery> applyPrintCapacity(List<DelayerPaperDelivery> input, Map<String, List<DelayerPaperDelivery>> groupedByStep, Map<String, List<DelayerPaperDelivery>> frozenByStep) {

        List<DelayerPaperDelivery> ordered = sortByPriority(input);

        int effectiveLimit = Math.min(printCapacity, ordered.size());
        List<DelayerPaperDelivery> accepted = ordered.subList(0, effectiveLimit);
        List<DelayerPaperDelivery> frozen = ordered.subList(effectiveLimit, ordered.size());

        groupedByStep.get(WorkflowStep.EVALUATE_PRINT_CAPACITY.name())
                .addAll(deepCopyAndUpdateKeys(accepted, WorkflowStep.EVALUATE_PRINT_CAPACITY, this.expectedDeliveryDate));

        freezeNotifications(frozen, WorkflowStep.EVALUATE_PRINT_CAPACITY, frozenByStep);
        return accepted;
    }

    private String calculateNotificationSk(WorkflowStep workflowStep, DelayerPaperDelivery n) {
        String requestId = n.getRequestId();

        switch (workflowStep) {

            case EVALUATE_SENDER_LIMIT -> {
                // Usa notificationSentAt se RS o secondo tentativo, altrimenti prepareRequestDate
                String product = n.getProductType();
                boolean isRsOrSecondAttempt =  "RS".equalsIgnoreCase(product) || (Integer.parseInt(n.attempt) > 0);

                String date = isRsOrSecondAttempt
                        ? n.getNotificationSentAt()
                        : n.getPrepareRequestDate();

                String province = n.getProvince();

                return String.join("~", province, date, requestId);
            }

            case EVALUATE_DRIVER_CAPACITY, EVALUATE_RESIDUAL_CAPACITY -> {
                String driver = n.getUnifiedDeliveryDriver();
                String date = expectedDeliveryDate;
                String province = n.getProvince();
                String priority = calculatePriority(n);

                return String.join("~", driver, province, priority, date, requestId);
            }

            case EVALUATE_PRINT_CAPACITY -> {
                String priority = calculatePriority(n);
                String date =expectedDeliveryDate;

                return String.join("~", priority, date, requestId);
            }

            case SENT_TO_PREPARE_PHASE_2 -> {
                String date = expectedDeliveryDate;

                return String.join("~", date, requestId);
            }

            default -> throw new IllegalArgumentException("Unsupported workflowStep: " + workflowStep);
        }
    }

    private String calculatePriority(DelayerPaperDelivery n) {
        String key = String.format("PRODUCT_%s.ATTEMPT_%d", n.getProductType(), Integer.parseInt(n.getAttempt()));

        for (Map.Entry<String, List<String>> entry : priorityConfigMap.entrySet()) {
            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException(String.format(
                "Priorità non trovata per la chiave: %s. Controlla la configurazione dei parametri.", key));
    }

    private String calculateNotificationPk(WorkflowStep workflowStep, String expectedDeliveryDate) {
        if (workflowStep == null || expectedDeliveryDate == null || expectedDeliveryDate.isEmpty())
            throw new RuntimeException("Errore nel calcolo della pk della notifica");

        return String.join("~", expectedDeliveryDate, workflowStep.name());
    }

    private void byDriverKey(List<DelayerPaperDelivery> postSenderLimit, Map<String, List<DelayerPaperDelivery>> byDriverKey) {
        for (DelayerPaperDelivery n : postSenderLimit) {
            String driverKey = getDriverKey(n);
            byDriverKey.computeIfAbsent(driverKey, k -> new ArrayList<>()).add(n);
        }
    }

    private Integer getSenderLimit(Map.Entry<String, List<DelayerPaperDelivery>> entry) {
        Integer senderLimit = this.senderLimitMap.get(entry.getKey());
        if (senderLimit == null)
            throw new RuntimeException("Sender limit not found");

        return senderLimit;
    }

    private Integer getDriverCapacity(String driverId) {
        if (driverId == null || driverId.split("~")[0].equalsIgnoreCase("null"))
            return this.actualCsv.size();

        Integer capacity = this.driverCapacityMap.get(driverId);
        if (capacity == null) throw new RuntimeException("DriverId missing: " + driverId);

        return capacity;
    }

    private String getDriverKey(DelayerPaperDelivery n) {
        return String.join("~", n.getUnifiedDeliveryDriver(), n.getProvince());
    }

    private String getSenderKey(DelayerPaperDelivery n) {
        return String.join("~", n.getSenderPaId(), n.getProductType(), n.getProvince());
    }

    private Map<String, List<DelayerPaperDelivery>> groupBySender(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> bySenderKey = new HashMap<>();
        for (DelayerPaperDelivery notification : notifications) {
            String senderKey = getSenderKey(notification); // Metodo esistente
            bySenderKey.computeIfAbsent(senderKey, k -> new ArrayList<>()).add(notification);
        }
        return bySenderKey;
    }

    private Map<String, List<DelayerPaperDelivery>> groupByDriver(List<DelayerPaperDelivery> notifications) {
        Map<String, List<DelayerPaperDelivery>> byDriverKey = new HashMap<>();
        for (DelayerPaperDelivery notification : notifications) {
            String driverKey = getDriverKey(notification); // Usa già "unifiedDeliveryDriver~provincia"
            byDriverKey.computeIfAbsent(driverKey, k -> new ArrayList<>()).add(notification);
        }
        return byDriverKey;
    }

    private List<DelayerPaperDelivery> collectAllFrozen(Map<String, List<DelayerPaperDelivery>> frozenByStep) {
        return frozenByStep.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<DelayerPaperDelivery> sortByPriority(List<DelayerPaperDelivery> notifiche) {
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

    private List<DelayerPaperDelivery> findByWorkflowStep(List<DelayerPaperDelivery> toFind, WorkflowStep workflowStep, String deliveryDate) throws Exception {

        final int pollingFrequency = 3000;
        final int internalRequestFrequency = 500;
        final int notificheDaTrovare = toFind.size();
        final int desiredTotalMillis = 25 * 60 * 1000; // 25 minuti
        final int estimatedTimePerAttempt = notificheDaTrovare * internalRequestFrequency + pollingFrequency;

        // Garantisce almeno 25 minuti di polling anche nel caso peggiore
        final int maxAttempts = Math.max(1, desiredTotalMillis / estimatedTimePerAttempt + 1);

        Set<String> requestIdsDaTrovare = toFind.stream()
                .map(DelayerPaperDelivery::getRequestId)
                .collect(Collectors.toSet());

        Set<DelayerPaperDelivery> notificheTrovate = new LinkedHashSet<>();
        String stepKey = workflowStep.name();

        int attempt = 1;
        while (!requestIdsDaTrovare.isEmpty() && notificheTrovate.size() < notificheDaTrovare && attempt <= maxAttempts) {
            log.info("Tentativo {}/{} - RequestId rimanenti: {}", attempt, maxAttempts, requestIdsDaTrovare);

            Iterator<String> iterator = requestIdsDaTrovare.iterator();
            while (iterator.hasNext()) {
                String requestId = iterator.next();

                List<DelayerPaperDelivery> risultati = pollNotificheByRequestId(requestId, 1, internalRequestFrequency);
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

            if (notificheTrovate.size() < notificheDaTrovare) {
                Thread.sleep(pollingFrequency);
            }

            attempt++;
        }

        int trovate = notificheTrovate.size();
        Assertions.assertEquals(
                notificheDaTrovare,
                trovate,
                String.format("Numero di notifiche trovate al workflow step '%s' diverso da quello atteso. Trovate: %d, Attese: %d",
                        stepKey, trovate, notificheDaTrovare)
        );

        return notificheTrovate.stream().toList();
    }

    private List<DelayerPaperDelivery> findCongelate(List<DelayerPaperDelivery> toFind) throws Exception {
        LocalDate lunedi = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        String deliveryDate = lunedi.toString();

        return findByWorkflowStep(toFind, WorkflowStep.EVALUATE_SENDER_LIMIT, deliveryDate);
    }

    private int calculateLimitByComparativo(String compare, int limit) {
        return switch (compare) {
            case "almeno", "esattamente" -> limit;
            case "inferiore" -> limit - 1;
            default -> throw new IllegalArgumentException("Il comparativo non è valido: " + compare);
        };
    }

    private void readCsv(String csvFileName, int expectedCount) {
        List<List<String>> rawCsv = FileUtils.readCsvSafe(String.join("/", CSV_PATH, csvFileName), ";", false);
        List<String> header = rawCsv.get(0);
        int actualCount = rawCsv.size() - 1;

        this.numeroNotifiche = actualCount;

        if (actualCount != expectedCount) {
            throw new RuntimeException("Declared notification count (" + expectedCount +
                    ") does not match the number of rows read from CSV (" + actualCount + ")");
        }

        for (int i = 1; i <= actualCount; i++) {
            this.actualCsv.add(new DelayerPaperDelivery(header, rawCsv.get(i)));
        }

        if (this.actualCsv.size() != this.numeroNotifiche) {
            throw new RuntimeException("Loaded " + this.actualCsv.size() +
                    " notifications, expected " + this.numeroNotifiche + " from CSV");
        }
    }

    private void initializeWorkflowStepMap() {
        for (WorkflowStep step : WorkflowStep.values()) {
            groupedByPkSubstring.put(step.name(), new ArrayList<>());
        }
    }

    private void initializeExpectedDeliveryDate() {
        if (expectedDeliveryDate == null) {
            LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            expectedDeliveryDate = monday.toString();
        }
    }

    private void initializeSenderAndDriverMaps() {
        for (DelayerPaperDelivery delivery : actualCsv) {
            String senderKey = getSenderKey(delivery);
            String driverKey = getDriverKey(delivery);

            senderLimitMap.putIfAbsent(senderKey, 0);
            driverCapacityMap.putIfAbsent(driverKey, 0);
        }
    }

    private void initializeSeedMaps(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String seed = row.get("seed");
            String quantityStr = row.get("quantita");

            if (seed == null || quantityStr == null) {
                throw new IllegalArgumentException("Incomplete row in data table: " + row);
            }

            int expectedCount;
            try {
                expectedCount = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity for seed '" + seed + "': " + quantityStr, e);
            }

            List<DelayerPaperDelivery> matchingDeliveries = actualCsv.stream()
                    .filter(d -> d.requestId != null && d.requestId.contains(seed))
                    .collect(Collectors.toList());

            if (matchingDeliveries.size() != expectedCount) {
                throw new IllegalStateException(String.format(
                        "Seed '%s': found %d items, expected %d",
                        seed, matchingDeliveries.size(), expectedCount));
            }

            Map<String, List<DelayerPaperDelivery>> maps = new HashMap<>();
            for (WorkflowStep step : WorkflowStep.values()) {
                maps.put(step.name(), new ArrayList<>());
            }

            groupedBySeed.put(seed, matchingDeliveries);
            expectedPianification.put(seed, maps);
            actualPianification.put(seed, maps);
        }
    }

    private void initializePriorityConfigMap() {
        priorityConfigMap = Map.of(
                "1", List.of("PRODUCT_RS.ATTEMPT_0"),
                "2", List.of("PRODUCT_AR.ATTEMPT_1", "PRODUCT_890.ATTEMPT_1"),
                "3", List.of("PRODUCT_AR.ATTEMPT_0", "PRODUCT_890.ATTEMPT_0")
        );
    }

}
