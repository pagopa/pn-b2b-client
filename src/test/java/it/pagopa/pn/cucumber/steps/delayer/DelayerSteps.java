package it.pagopa.pn.cucumber.steps.delayer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.loader.DelayerCsvLoader;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPrintCapacityCounter;
import it.pagopa.pn.cucumber.steps.delayer.model.ExecutionStatusResponse;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.planner.DelayerPlanner;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import it.pagopa.pn.cucumber.steps.delayer.validator.DelayerValidator;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps.*;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.*;
import static java.lang.Thread.sleep;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {

    public static final String[] CSV_FILES = new String[]{"tcRankingMerged.csv", "tcSenderUnknow.csv", "tcSplitSender.csv", "tcZeroDriver.csv", "tcProvCapNonCensite.csv","spedizioni_3000.csv"};
    public static final int POLLING_MAX_MINUTES = 90;
    public static final String BATCH_WORKFLOW_STATE_MACHINE = "BatchWorkflowStateMachine";

    private final DelayerContext context;
    private final DelayerCsvLoader csvLoader;
    private final DelayerPlanner planner;
    private final DelayerLambdaClient lambdaClient;
    private final DelayerValidator validator;
    private final DelayerPaperDeliveryUtils utils;

    @Autowired
    public DelayerSteps(LambdaInvoker lambdaInvoker, @Value("${pn.delayer.lambda.arn}") String lambdaName) {

        this.context = new DelayerContext();
        this.csvLoader = new DelayerCsvLoader(context);
        this.planner = new DelayerPlanner(context);

        this.lambdaClient = new DelayerLambdaClient(lambdaInvoker, lambdaName);
        this.utils = new DelayerPaperDeliveryUtils(context);
        this.validator = new DelayerValidator(context, lambdaClient, utils);
    }

    @Given("il CSV {string} contiene {int} notifiche distribuite tra i seguenti test case:")
    public void initParams(String csv, Integer expectedNotificationCount, DataTable dataTable) {
        csvLoader.readCsv(csv, expectedNotificationCount);
        csvLoader.initializeExpectedDeliveryDate();
        csvLoader.initializeLimits();
        csvLoader.initializeSeeds(dataTable);
    }

    @Given("il CSV {string} è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test")
    public void populateTargetTable(String csvName) throws Exception {
        lambdaClient.invoke("IMPORT_DATA", "pn-DelayerPaperDelivery", "pn-PaperDeliveryCounters", csvName);
    }

    @Then("vengono puliti i dati dalle tabelle target")
    public void deleteDataFormTargetTable() {
        Arrays.stream(CSV_FILES).forEach(csv -> {
            try {
                lambdaClient.invoke("DELETE_DATA", "pn-DelayerPaperDelivery", "pn-PaperDeliveryDriverUsedCapacities",
                        "pn-PaperDeliveryUsedSenderLimit", "pn-PaperDeliveryCounters", csv);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
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

            int actual = lambdaClient.getAvailableCapacity(entityId.split("~")[0], entityId.split("~")[1], context.expectedDeliveryDate);
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

    @When("viene avviata la step function BatchWorkflowStateMachine")
    public void runFirstStepFunction() throws Exception {
        context.currentExecutionArn = lambdaClient.runBatchWorkflowStateMachine(context.printCapacity).getExecutionArn();
        waitUntilStepFunctionEnd();
    }

    @When("viene avviata la step function DelayerToPaperChannelStateMachine")
    public void runSecondStepFunction() throws Exception {
        context.currentExecutionArn = lambdaClient.runDelayerToPaperChannel().getExecutionArn();
        waitUntilStepFunctionEnd();
    }

    @And("verifica che i parametri in PrintCapacityCounter siano conformi a quelli calcolati internamente")
    public void checkPrintCapacityCounter(){
        DelayerPrintCapacityCounter tupla = lambdaClient.getPrintCapacityCounter(context.expectedDeliveryDate);
        Assertions.assertThat(tupla).isNotNull();

        Assertions.assertThat(tupla.getDailyExecutionNumber())
                .as("DailyExecutionCounter deve essere uguale allo STANDARD_DAILY_EXECUTIONS")
                .isEqualTo(DelayerContext.STANDARD_DAILY_EXECUTIONS);

        Assertions.assertThat(tupla.getDailyExecutionCounter())
                .as("DailyExecutionNumber deve essere uguale a quello calcolato internamente")
                .isEqualTo(context.currentStepFunction2ExecutionIndex);
    }

    @When("vengono avviate le {int} esecuzioni della step function DelayerToPaperChannelStateMachine")
    public void runSecondStepFunctionWithLimit(int expectedExecutions) throws Exception {
        context.expectedExecutions = expectedExecutions;

        while (context.currentStepFunction2ExecutionIndex < context.expectedExecutions) {
            // Avvio la seconda step function
            runSecondStepFunction();
            checkPrintCapacityCounter();
            ++context.currentStepFunction2ExecutionIndex;

            // Prelevo tutte le notifiche in SENT_TO_PREPARE_PHASE_2
            fetchNotification(SENT_TO_PREPARE_PHASE_2.name());

            // Verifico che siano elaborate le notifiche secondo i limiti e secondo il ranking
            checkRanking(SENT_TO_PREPARE_PHASE_2.name(), null);
        }
    }

    @Then("vengono recuperate le notifiche al workflow step {string}")
    public void fetchNotification(String ws) throws Exception {
        WorkflowSteps step = valueOf(ws);
        List<DelayerPaperDelivery> expected = context.getExpectedByWorkflowStep(step);

        Set<String> requestIds = expected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet());
        List<DelayerPaperDelivery> actual = lambdaClient.findByWorkflowStep(requestIds, step.name(), context.expectedDeliveryDate, POLLING_MAX_MINUTES);

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
        List<DelayerPaperDelivery> actual = lambdaClient.findByWorkflowStep(requestIds, step.name(), context.expectedDeliveryDate, 5);

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
        List<DelayerPaperDelivery> frozenExpected = context.expectedPianification.values().stream()
                .flatMap(m -> m.getOrDefault("FROZEN", List.of()).stream())
                .toList();
        validator.checkFrozen(step, frozenExpected);
    }

    @Given("verifica che la capacità disponibile per ogni tripla (unifiedDeliveryDriver-provincia-deliveryDate) sia {word} {int}")
    public void checkDriverCapacity(String compare, Integer value) {
        validator.checkDriverCapacity(compare, value, EVALUATE_DRIVER_CAPACITY);
    }

    @Then("verifica la corretta pianificazione di ogni test case")
    public void assertAll() {
        validator.assertPianifications();
    }

    private void waitUntilStepFunctionEnd() throws InterruptedException {
        if (context.currentExecutionArn == null) return;

        final String arn = context.currentExecutionArn;

        final long startTime = System.currentTimeMillis();
        final long maxWaitMillis = TimeUnit.MINUTES.toMillis(POLLING_MAX_MINUTES);
        final long pollingIntervalMillis = TimeUnit.MINUTES.toMillis(5);

        log.info("Inizio polling Step Function: {}", arn);

        while (true) {

            ExecutionStatusResponse status;

            try {
                status = lambdaClient.getExecutionStatus(arn);
            } catch (Exception e) {
                if (System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(5))
                    throw new RuntimeException("Timeout durante il recupero dello stato della Step Function: " + arn, e);

                sleep(pollingIntervalMillis);
                continue;
            }

            String state = status.getStatus();
            log.info("Stato StepFunction {} → {}", arn, state);

            // Stato non ricevuto
            if (state == null) {
                log.warn("Stato null dalla Step Function {}, riprovo...", arn);
                sleep(pollingIntervalMillis);
                continue;
            }

            switch (state) {
                case "RUNNING":
                    // continua polling
                    break;

                case "SUCCEEDED":
                    log.info("Step Function {} completata con successo.", arn);
                    return;

                case "FAILED":
                case "TIMED_OUT":
                case "ABORTED":
                    log.error("Step Function {} terminata con errore: {} - cause: {}",
                            arn, status.getError(), status.getCause());
                    throw new RuntimeException(
                            "Step Function TERMINATED WITH ERROR: state=" + state +
                                    ", error=" + status.getError() +
                                    ", cause=" + status.getCause()
                    );

                default:
                    log.warn("Stato Step Function {} sconosciuto: {}", arn, state);
                    break;
            }

            // Controllo timeout
            if (System.currentTimeMillis() - startTime > maxWaitMillis) {
                throw new RuntimeException("Timeout: Step Function non è terminata entro 10 minuti: " + arn);
            }

            // Prossimo polling
            sleep(pollingIntervalMillis);
        }
    }
}
