package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.loader.DelayerCsvLoader;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.planner.DelayerPlanner;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils;
import it.pagopa.pn.cucumber.steps.delayer.validator.DelayerValidator;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps.*;
import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.calculateLimitByComparativo;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {

    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";

    private final DelayerContext context;
    private final DelayerCsvLoader csvLoader;
    private final DelayerPlanner planner;
    private final DelayerLambdaClient lambdaClient;
    private final DelayerValidator validator;
    private final DelayerPaperDeliveryUtils utils;

    @Autowired
    public DelayerSteps(LambdaInvoker lambdaInvoker) {
        this.context = new DelayerContext();
        this.csvLoader = new DelayerCsvLoader(context);
        this.planner = new DelayerPlanner(context);
        this.lambdaClient = new DelayerLambdaClient(lambdaInvoker, LAMBDA_NAME);
        this.validator = new DelayerValidator(context, lambdaClient);
        this.utils = new DelayerPaperDeliveryUtils(context);
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
        lambdaClient.invoke("IMPORT_DATA", csvName);
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
            String entityId = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            int calculatedLimit = calculateLimitByComparativo(comparative, rawLimit);

            if (!utils.hasDriver(entityId)) {
                log.warn("{} non presente nel CSV: {}", idKey, entityId);
            }

            utils.setDriverCapacity(entityId, calculatedLimit);
        }
    }

    @And("si verifica che il limite settimanale utilizzato dai recapitisti \\(unifiedDeliveryDriver-geoKey) sia:")
    public void checkDriverAvailableCapacity(DataTable dataTable) {

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String idKey = "unifiedDeliveryDriverId";
            String entityId = row.get(idKey);
            String comparative = row.get("comparative");
            int rawLimit = Integer.parseInt(row.get("limit"));

            int actual = lambdaClient.getAvailableCapacity(entityId.split("~")[0], entityId.split("~")[1], context.expectedDeliveryDate);

            switch (comparative.toLowerCase()) {
                case "almeno","inferiore" -> {
                    if (actual <= rawLimit) {
                        throw new AssertionError("Capacità di " + entityId + " inferiore ad almeno " + rawLimit + ", trovata: " + actual);
                    }
                }
                case "esattamente" -> {
                    if (actual != rawLimit) {
                        throw new AssertionError("Capacità di " + entityId + " diversa da " + rawLimit + ", trovata: " + actual);
                    }
                }
                default -> throw new IllegalArgumentException("Comparatore non valido: " + comparative);
            }

        }
    }

    @And("si presuppone che la capacità di stampa giornaliera sia {word} {int}")
    public void initPrintCapacity(String compare, int limit) {
        context.printCapacity = calculateLimitByComparativo(compare, limit);
        if (context.printCapacity < 0) throw new IllegalArgumentException("Capacità di stampa non valida");
    }

    @And("viene simulato internamente l'algoritmo di pianificazione")
    public void runSimulation() {
        context.expectedPianification.replaceAll((seed, oldStepMap) ->
                planner.simulateAlgorithm(SENT_TO_PREPARE_PHASE_2, seed)
        );
    }

    @When("viene avviato l'algoritmo tramite lambda")
    public void runAlgorithm() throws Exception {
        lambdaClient.invoke("RUN_ALGORITHM");
    }

    @Then("vengono recuperate le notifiche al workflow step {string}")
    public void fetchNotification(String ws) throws Exception {
        WorkflowSteps step = valueOf(ws);
        List<DelayerPaperDelivery> expected = context.expectedPianification.values().stream()
                .flatMap(m -> m.getOrDefault(step.name(), List.of()).stream())
                .toList();

        Set<String> requestIds = expected.stream().map(DelayerPaperDelivery::getRequestId).collect(Collectors.toSet());
        List<DelayerPaperDelivery> actual = lambdaClient.findByWorkflowStep(requestIds, step.name(), context.expectedDeliveryDate, 18);

        actual.forEach(dpd -> {
            String seed = context.groupedBySeed.keySet().stream()
                    .filter(dpd.getRequestId()::contains)
                    .findFirst()
                    .orElseThrow();
            context.actualPianification.get(seed).get(step.name()).add(dpd);
        });
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


}
