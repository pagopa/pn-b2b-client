package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.delayer.loader.DelayerCsvLoader;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerCountersPrintItem;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.ParallelScenarioPhase;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.planner.DelayerPlanner;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import it.pagopa.pn.cucumber.steps.delayer.validator.DelayerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import io.cucumber.spring.ScenarioScope;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext.GATE_TIMEOUT;
import static it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps.*;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.*;

@Slf4j
@RequiredArgsConstructor
@ScenarioScope
public class DelayerSteps {

    private final DelayerContext context;
    private final DelayerSuiteContext suiteContext;
    private final DelayerCsvLoader csvLoader;
    private final DelayerPlanner planner;
    private final DelayerSevice service;
    private final DelayerValidator validator;
    private final DelayerPaperDeliveryUtils utils;
    private final Map<String, Integer> availableCapacityByDriver = new HashMap<>();

    private String parallelScenarioId;

    @Before("@delayerParallel")
    public void bindParallelScenario(Scenario scenario) {
        parallelScenarioId = suiteContext.extractScenarioId(scenario.getName());
        context.resetContext();
        availableCapacityByDriver.clear();
    }

    @Given("il CSV {string} contiene {int} notifiche distribuite tra i seguenti test case:")
    public void initParams(String csv, Integer expectedNotificationCount, DataTable dataTable) {
        csvLoader.readCsv(csv, expectedNotificationCount);
        csvLoader.initializeExpectedDeliveryDate(dataTable);
        csvLoader.initializeLimits();
        csvLoader.initializeSeeds(dataTable);
    }

    @Given("il CSV {string} è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test")
    public void populateTargetTable(String csvName) {
        service.importData(csvName, context.expectedDeliveryDate);
    }

    @Given("vengono puliti i dati dalle tabelle target")
    public void deleteDataFormTargetTable() {
        service.deleteDataAll();
    }

    @And("si presuppone che il limite mittente settimanale \\(paId-product_type-province) sia:")
    public void initSenderLimit(DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String idKey = "senderId";
            String entityId = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            int calculatedLimit = calculateLimitByComparativo(comparative, rawLimit);

            if (!context.senderLimitMap.containsKey(entityId)) {
                log.warn("{} non presente nel CSV: {}", idKey, entityId);
            }

            context.senderLimitMap.put(entityId, calculatedLimit);
        }
    }

    @And("si presume che il limite settimanale dei recapitisti \\(unifiedDeliveryDriver-geoKey) sia:")
    public void initDriverLimit(DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String idKey = "unifiedDeliveryDriverId";
            String driverKey = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            int calculatedLimit = calculateLimitByComparativo(comparative, rawLimit);

            if (!utils.hasDriver(driverKey)) {
                log.warn("{} non presente nel CSV: {}", idKey, driverKey);
            }

            utils.setInitialDriverCapacity(driverKey, calculatedLimit);
        }

        initAvailableDriverCapacity();
    }

    private void initAvailableDriverCapacity() {
        // Per ogni provincia (driver~provincia) inizializza le capacità disponibili
        context.driverCapacityMap.forEach((provinceDriverKey, capMap) -> {
            String[] parts = provinceDriverKey.split("~");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato driverId non valido. Atteso 'driver~location': " + provinceDriverKey);
            }

            final String driver = parts[0];
            final String location = parts[1];

            // 1) Riporto tutte le capacità in avaiable
            capMap.forEach(utils::setAvailableDriverCapacity);

            // 2) Modifico Available per PROVINCIA = somma dei CAP / mittenti distinti su (driver, provincia)
            if (utils.isValidProvince(location)) {
                int totalProvinceCapacity = capMap.keySet().stream()
                        .filter(k -> !k.equals(provinceDriverKey))
                        .mapToInt(capMap::get)
                        .sum();

                if (!capMap.get(provinceDriverKey).equals(totalProvinceCapacity))
                    throw new RuntimeException("Driver province capacity " + provinceDriverKey + " is wrong");

                long distinctSenders = context.actualCsv.stream()
                        .filter(d -> driver.equals(d.getUnifiedDeliveryDriver()))
                        .filter(d -> location.equalsIgnoreCase(d.getProvince()))
                        .map(DelayerPaperDelivery::getSenderPaId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();

                int divisor = (int) Math.max(1, distinctSenders);
                int perSenderProvinceCapacity = totalProvinceCapacity / divisor;

                Map<String, Integer> inner = context.usedDriverCapacityMap.get(provinceDriverKey);
                if (inner == null) {
                    throw new IllegalStateException("Mappa capacità mancante per: " + provinceDriverKey);
                }

                inner.replaceAll((k, v) -> k.equals(provinceDriverKey) ? v : perSenderProvinceCapacity);

            } else throw new RuntimeException("Driver province capacity " + provinceDriverKey + " is wrong");

        });
    }

    @And("si verifica che il limite settimanale utilizzato dai recapitisti \\(unifiedDeliveryDriver-geoKey) sia:")
    @And("si verifica che la capacità disponibile settimanale dei recapitisti \\(unifiedDeliveryDriver-geoKey) sia:")
    public void checkDriverAvailableCapacity(DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String idKey = "unifiedDeliveryDriverId";
            String entityId = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            int actual = service.getAvailableCapacity(entityId, context.expectedDeliveryDate);
            availableCapacityByDriver.put(entityId, actual);
            // TODO verficare perché nella prima fase del test la capacità non è disponibile e viene mockata con quella passata dal test stesso
            if (actual == -1) actual = rawLimit;

            switch (comparative.toLowerCase()) {
                case "almeno" -> {
                    if (actual < rawLimit) {
                        throw new AssertionError("Capacità di " + entityId + " inferiore ad " + rawLimit + ", trovata: " + actual);
                    }
                }
                case "esattamente" -> {
                    if (actual != rawLimit) {
                        throw new AssertionError("Capacità di " + entityId + " diversa da " + rawLimit + ", trovata: " + actual);
                    }
                }
                case "inferiore" -> {
                    if (actual > rawLimit) {
                        throw new AssertionError("Capacità di " + entityId + " superiore a " + rawLimit + ", trovata: " + actual);
                    }
                }
                default -> throw new IllegalArgumentException("Comparatore non valido: " + comparative);
            }

        }
    }

    @And("viene verificata che la capacità disponibile per i seguenti driver sia decrementata di: {int}")
    public void assertCapacityDecremented(int difference, DataTable dataTable) {
        assertCapacity(dataTable,
                (driver, province) -> service.getAvailableCapacity(driver, province, context.expectedDeliveryDate),
                entityId -> availableCapacityByDriver.get(entityId) - difference);
    }

    @And("viene verificata che la capacità utilizzata per i seguenti driver sia uguale a: {int}")
    public void assertCapacityEqualsTo(int expected, DataTable dataTable) {
        assertCapacity(dataTable,
                (driver, province) -> service.getUsedCapacity(driver, province, context.expectedDeliveryDate),
                entityId -> expected);
    }

    private void assertCapacity(DataTable dataTable,
                                BiFunction<String, String, Integer> actualCalculator,
                                Function<String, Integer> expectedCalculator) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String entityId = row.get("unifiedDeliveryDriverId");
            String[] parts = entityId.split("~");
            String driver = parts[0];
            String province = parts[1];
            int actual = actualCalculator.apply(driver, province);
            int expected = expectedCalculator.apply(entityId);
            Assertions.assertThat(actual)
                    .as("Capacità residua per driver %s", entityId)
                    .isEqualTo(expected);
        }
    }

    @And("si presuppone che la capacità di stampa giornaliera sia {word} {int}")
    public void initPrintCapacity(String compare, int limit) {
        context.setPrintCapacity(calculateLimitByComparativo(compare, limit));
        if (context.printCapacity < 0) throw new IllegalArgumentException("Capacità di stampa non valida");
    }

    @And("viene impostata la capacità di stampa settimanale in modo che sia {word} {int}")
    public void setWeeklyPrintCapacity(String compare, int limit) {
        int weeklyPrintCapacity = calculateLimitByComparativo(compare, limit);
        if (weeklyPrintCapacity < 0) throw new IllegalArgumentException("Capacità di stampa non valida");
        context.setWeeklyPrintCapacity(weeklyPrintCapacity);
    }

    @And("viene impostato il limite massimo di {int} spedizioni in SENT_TO_PREPARE_PHASE_2 per ogni esecuzione di DelayerToPaperChannelStateMachine")
    public void setMaxToPhase2(int maxToPhase2) {
        context.setMaxDeliveryToPhase2ForExecution(maxToPhase2);
    }

    @And("vengono simulate internamente le operazioni di BatchWorkflowStateMachine")
    public void runSimulation() {
        context.expectedPianification.replaceAll((seed, oldStepMap) ->
                planner.simulateAlgorithm(SENT_TO_PREPARE_PHASE_2, seed)
        );
    }

    @And("vengono simulate internamente le operazioni di DelayerToPaperChannelStateMachine")
    public void runSimulation2() {
        planner.simulateAlgorithm2(context.expectedPianification);
    }

    @When("viene avviata la step function BatchWorkflowStateMachine con deliveryDate: {string}")
    public void runFirstStepFunctionWithFixedDeliveryDate(String deliveryWeek) throws Exception {
        suiteContext.advance(parallelScenarioId, ParallelScenarioPhase.BATCH_REQUESTED);
        suiteContext.awaitAllAtLeast(ParallelScenarioPhase.BATCH_REQUESTED, GATE_TIMEOUT);

        synchronized (suiteContext) {
            if (suiteContext.batchExecutionArn == null) {
                suiteContext.batchExecutionArn =
                        service.runBatchWorkflowStateMachine(context.printCapacity, deliveryWeek);
            }
            context.currentExecutionArn = suiteContext.batchExecutionArn;
        }

        service.waitUntilStepFunctionEnd(context);
        suiteContext.advance(parallelScenarioId, ParallelScenarioPhase.BATCH_DONE);
    }

    @When("viene avviata la step function BatchWorkflowStateMachine con deliveryDate in avanti di {int} settimane")
    public void runFirstStepFunctionWithDeliveryDate(int weeksToAdd) throws Exception {
        String deliveryWeek = getNextMonday(weeksToAdd);
        runFirstStepFunctionWithFixedDeliveryDate(deliveryWeek);
    }

    @When("viene avviata la step function BatchWorkflowStateMachine")
    public void runFirstStepFunction() throws Exception {
        runFirstStepFunctionWithFixedDeliveryDate(getCurrentMonday());
    }

    @When("viene avviata la step function DelayerToPaperChannelStateMachine")
    public void runSecondStepFunction() throws Exception {
        suiteContext.advance(parallelScenarioId, ParallelScenarioPhase.PHASE2_REQUESTED);
        suiteContext.awaitAllAtLeast(ParallelScenarioPhase.PHASE2_REQUESTED, GATE_TIMEOUT);

        synchronized (suiteContext) {
            if (suiteContext.phase2ExecutionArn == null) {
                suiteContext.phase2ExecutionArn = service.runDelayerToPaperChannel().getExecutionArn();
            }
            context.currentExecutionArn = suiteContext.phase2ExecutionArn;
        }

        service.waitUntilStepFunctionEnd(context);
        ++context.currentStepFunction2ExecutionIndex;
        checkPrintCapacityCounter();
        suiteContext.advance(parallelScenarioId, ParallelScenarioPhase.PHASE2_DONE);
    }

    @And("verifica che i parametri in PrintCapacityCounter siano conformi a quelli calcolati internamente")
    public void checkPrintCapacityCounter() {
        DelayerCountersPrintItem tupla = service.getPrintCapacityCounter(context.expectedDeliveryDate);
        Assertions.assertThat(tupla).isNotNull();
        boolean hasDeliveryInEvaluatePrint = !context.getExpectedByWorkflowStep(EVALUATE_PRINT_CAPACITY).isEmpty();

        if (hasDeliveryInEvaluatePrint) {
            Assertions.assertThat(tupla.getDailyExecutionNumber())
                    .as("DailyExecutionNumber deve essere uguale allo STANDARD_DAILY_EXECUTIONS")
                    .isEqualTo(DelayerContext.STANDARD_DAILY_EXECUTIONS);

            Assertions.assertThat(tupla.getDailyExecutionCounter())
                    .as("DailyExecutionCounter deve essere uguale a quello calcolato internamente")
                    .isEqualTo(context.currentStepFunction2ExecutionIndex);
        }
    }

    @When("vengono avviate le {int} esecuzioni della step function DelayerToPaperChannelStateMachine")
    public void runSecondStepFunctionWithLimit(int expectedExecutions) throws Exception {
        context.expectedExecutions = expectedExecutions;
        context.assertPhase2ByExecutionCounter = true;

        while (context.currentStepFunction2ExecutionIndex < context.expectedExecutions) {
            // Avvio la seconda step function
            runSecondStepFunction();

            // Prelevo tutte le notifiche in SENT_TO_PREPARE_PHASE_2
            fetchNotification(SENT_TO_PREPARE_PHASE_2.name());

            // Verifico che siano elaborate le notifiche secondo i limiti e secondo il ranking
            checkRanking(SENT_TO_PREPARE_PHASE_2.name(), null);
        }

        context.assertPhase2ByExecutionCounter = false;
    }

    @Then("vengono recuperate le notifiche al workflow step {string}")
    public void fetchNotification(String ws) throws Exception {
        WorkflowSteps step = valueOf(ws);
        List<DelayerPaperDelivery> expected = context.getExpectedByWorkflowStep(step);

        Set<String> requestIds = expected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet());
        List<DelayerPaperDelivery> actual = service.findByWorkflowStep(requestIds, step.name(), context.expectedDeliveryDate, 1);

        expected.forEach(expectedDelivery ->
                actual.stream()
                        .filter(actualDelivery -> actualDelivery.getRequestId().equals(expectedDelivery.getRequestId()))
                        .findFirst()
                        .ifPresent(actualDelivery ->
                                expectedDelivery.setVirtualNotificationSentAt(actualDelivery.getVirtualNotificationSentAt()))
        );

        expected.forEach(expectedDelivery ->
                expectedDelivery.setSk(utils.calculateSk(WorkflowSteps.valueOf(ws), expectedDelivery))
        );


        actual.forEach(dpd -> {
            String seed = extractSeed(dpd);
            context.actualPianification.get(seed).get(step.name()).add(dpd);
        });

        validator.checkSilentlyAll(step);
    }

    @Then("verifica che non esistano notifiche al workflow step {string} per il seed {string}")
    public void fetchNonExistentNotification(String ws, String seed) throws Exception {
        WorkflowSteps step = valueOf(ws);
        List<DelayerPaperDelivery> notExpected = context.actualCsv.stream().filter(n -> hasSeedInRequestId(seed, n)).toList();

        if (notExpected.isEmpty()) log.warn("Nessuna notifica esistente per il seed: " + seed);

        Set<String> requestIds = notExpected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet());
        List<DelayerPaperDelivery> actual = service.findByWorkflowStep(requestIds, step.name(), context.expectedDeliveryDate, 1);

        validator.checkNotExistSilently(actual, seed, step);
    }

    @Then("verifica che il processo fino al workflow step {string} abbia rispettato i criteri di ranking per almeno un test case:")
    public void checkRanking(String ws, DataTable expectedOrder) {
        WorkflowSteps step = valueOf(ws);
        validator.checkRanking(step, expectedOrder);
    }

    @Then("verifica che le opportune notifiche siano state congelate e ricaricate con workflow step {string} e deliveryDate alla settimana seguente per almeno un test case")
    public void checkFrozen(String ws) throws Exception {
        WorkflowSteps step = valueOf(ws);
        context.frozenExpected = context.expectedPianification.values().stream()
                .flatMap(m -> m.getOrDefault("FROZEN", List.of()).stream())
                .toList();
        validator.checkFrozen(step, context.frozenExpected);
    }

    @Then("verifica la corretta pianificazione di ogni test case")
    public void assertAll() {
        validator.assertPianifications();
    }


    @And("imposto la deliveryWeek in avanti di {int} settimane")
    public void setDeliveryWeek(int nWeeks) {
        context.expectedDeliveryDate = getNextMonday(nWeeks);
    }

    @And("sposto la simulazione in avanti di {int} settimane")
    public void moveForward(int nWeeks) {
        var frozen = new ArrayList<>(context.expectedPianification.values().stream().findAny().map(m->m.get("FROZEN")).orElse(Collections.emptyList()));
        context.resetContext();
        context.expectedDeliveryDate = getNextMonday(nWeeks);
        context.actualCsv.addAll(frozen);
    }

    @Then("non devono esistere record in pn-DelayerPaperDelivery per la deliveryDate {string}")
    public void verifyNoPaperDeliveryForDate(String deliveryDate) {
        SoftAssertions softly = new SoftAssertions();
        var steps = List.of(WorkflowSteps.EVALUATE_DRIVER_CAPACITY,
                WorkflowSteps.EVALUATE_PRINT_CAPACITY,
                WorkflowSteps.SENT_TO_PREPARE_PHASE_2,
                WorkflowSteps.EVALUATE_RESIDUAL_CAPACITY
        );

        steps.forEach(ws -> {
            var paperDeliveryItems = service.getPaperDeliveryItemsSize(deliveryDate, ws);

            softly.assertThat(paperDeliveryItems)
                    .as("workflowStep '%s' → trovati %d record per deliveryDate '%s'", ws, paperDeliveryItems, deliveryDate)
                    .isEqualTo(0);
        });

        softly.assertAll();
    }

    @And("non deve esistere capacità usata alla deliveryDate {string}")
    public void verifyNoUsedCapacity(String deliveryDate, DataTable dataTable) {
        assertCapacity(dataTable,
                (driver, province) -> service.getUsedCapacity(driver, province, deliveryDate),
                entityId -> -1);
    }

    @And("non devono esistere contatori per la deliveryDate {string}")
    public void verifyNoCounters(String deliveryDate) {
        var counters = service.getCountersPrintSize(deliveryDate);
        Assertions.assertThat(counters).isEqualTo(0);
    }

    @And("non devono esistere limiti mittente per la deliveryDate {string} e pk {string}")
    public void verifyNoSenderLimits(String deliveryDate, String pk) {
        var usedSenderLimit = service.getUsedSenderLimitSize(deliveryDate, pk);
        Assertions.assertThat(usedSenderLimit).isEqualTo(0);
    }

    @And("verifica che le spedizioni spostate alla settimana successiva siano lo stesso valore")
    public void verifyResidualPapers() {
        SoftAssertions softly = new SoftAssertions();

        var residualPapers = service.getResidualPapers(context.expectedDeliveryDate).map(DelayerPaperDelivery::getRequestId).toList();

        context.frozenExpected.forEach(expected ->
                softly.assertThat(residualPapers)
                        .as("La spedizione con requestId '%s' non è presente nei residual papers", expected.getRequestId())
                        .contains(expected.getRequestId())
        );

        softly.assertAll();
    }


    @Then("viene verificato il limite garantito per la pa: {string} relativo a provincia: {string}, prodotto: {string} e deliveryDate: {string}")
    public void checkSenderLimitForPA(String paId, String province, String product, String deliveryDate) {

        String pk = new StringBuilder(paId).append("~")
                .append(product).append("~")
                .append(province).toString();

        int sumEstimate = service.getCountersSumEstimates(deliveryDate, province, product).getNumberOfShipments();
        int weeklyEstimate = service.fetchWeeklyEstimateForPA(deliveryDate, pk);
        Set<String> productsWithCapacity = new HashSet<>();
        int sumDeclaredCapacity = service.getDeclaredCapacity(deliveryDate, province, product, productsWithCapacity);

        int toBeExcluded = 0;
        for (String productWithCapacity : productsWithCapacity) {
            toBeExcluded += service.getCountersExclude(deliveryDate, province, productWithCapacity);
        }

        Assertions.assertThat(sumEstimate)
                .as("SUM_ESTIMATES deve essere > 0 per calcolare il limite mittente. paId=%s, province=%s, product=%s, deliveryDate=%s, sumEstimate=%s",
                        paId, province, product, deliveryDate, sumEstimate)
                .isGreaterThan(0);

        double senderLimitPercentage = Math.ceil(((double) weeklyEstimate / (sumEstimate)) * 1000) / 10;

        double expectedSenderLimit = Math.ceil((sumDeclaredCapacity - toBeExcluded) * (senderLimitPercentage / 100.0));

        int actualSenderLimit = service.getUsedSenderLimit(deliveryDate, pk);

        Assertions.assertThat(expectedSenderLimit).as("Confronto di actual ed expected del limite del mittente").isEqualTo(actualSenderLimit);

    }


}
