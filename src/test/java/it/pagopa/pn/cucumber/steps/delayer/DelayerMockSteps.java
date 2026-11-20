package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerTable;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerMockService;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@ScenarioScope
public class DelayerMockSteps {

    private static final PollingConfig TABLES_POLLING = PollingConfig.of(Duration.ofMinutes(5), Duration.ofSeconds(10));

    private final DelayerContext context;
    private final DelayerMockService mockService;
    private final DelayerSevice service;

    private Map<DelayerTable, Integer> realTablesSnapshot;

    @When("vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES")
    public void deleteMockTables() {
        mockService.deleteMockTables();
    }

    // Verifica sull'intera tabella, non su una singola deliveryDate/provincia: DELETE_MOCK_TABLES deve svuotarla completamente.
    // La disponibilità delle tabelle mock è garantita dallo step DELETE_MOCK_TABLES: qui un errore di lettura fa fallire il test
    @Then("la tabella {string} non contiene alcun record")
    public void verifyMockTableEmpty(String tableName) {
        DelayerTable table = DelayerMockService.MOCK_TABLES.stream()
                .filter(mockTable -> mockTable.tableName.equals(tableName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'%s' non è una delle tabelle mock gestite da DELETE_MOCK_TABLES: %s".formatted(tableName, DelayerMockService.MOCK_TABLES)));
        try {
            Poller.pollUntil(() -> mockService.isMockTableEmpty(table), Boolean::booleanValue, TABLES_POLLING);
        } catch (PollingTimeoutException e) {
            Assertions.fail("%s contiene ancora record".formatted(tableName));
        }
    }

    @Given("vengono caricate le capacità dei recapitisti mock tramite il file {string}")
    public void insertMockCapacities(String filename) {
        mockService.insertMockCapacities(filename);
    }

    @Given("il CSV {string} è importato da S3 nella pn-DelayerPaperDeliveryMock tramite lambda di test con deliveryWeek {string}")
    public void populateMockTargetTable(String csvName, String deliveryWeek) {
        mockService.importDataMock(csvName, deliveryWeek);
    }

    // Esecuzione isolata sulle tabelle mock: non partecipa ai gate della suite parallela @delayer1-5
    @When("viene avviata la step function BatchWorkflowStateMachine sulle tabelle mock con deliveryDate {string} e capacità di stampa giornaliera {int}")
    public void runFirstStepFunctionOnMockTables(String deliveryWeek, int printCapacity) throws Exception {
        context.currentExecutionArn = mockService.runBatchWorkflowStateMachineMock(printCapacity, deliveryWeek);
        service.waitUntilStepFunctionEnd(context);
    }

    @Then("devono esistere spedizioni mock in pn-DelayerPaperDeliveryMock per la deliveryDate {string}")
    public void verifyMockPaperDeliveries(String deliveryDate) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockPaperDeliverySize(deliveryDate),
                "pn-DelayerPaperDeliveryMock non contiene record per deliveryDate '%s'".formatted(deliveryDate));
    }

    @Then("devono esistere capacità dichiarate mock per la deliveryDate {string} e la provincia {string}")
    public void verifyMockDeclaredCapacities(String deliveryDate, String province) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockDeclaredCapacitySize(province, deliveryDate),
                "pn-PaperDeliveryDriverCapacitiesMock non contiene record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    @Then("devono esistere capacità utilizzate mock per la deliveryDate {string}, il recapitista {string} e la geokey {string}")
    public void verifyMockUsedCapacities(String deliveryDate, String driver, String geoKey) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockUsedCapacitySize(driver, geoKey, deliveryDate),
                "pn-PaperDeliveryDriverUsedCapacitiesMock non contiene record per deliveryDate '%s', recapitista '%s' e geokey '%s'".formatted(deliveryDate, driver, geoKey));
    }

    @Then("devono esistere limiti mittente utilizzati mock per la deliveryDate {string} e la provincia {string}")
    public void verifyMockUsedSenderLimits(String deliveryDate, String province) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockUsedSenderLimitSize(deliveryDate, province),
                "pn-PaperDeliveryUsedSenderLimitMock non contiene record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    // Ogni tabella reale deve contenere almeno un record: altrimenti il confronto "uguale o maggiore" dopo DELETE_MOCK_TABLES passerebbe banalmente (0 >= 0)
    @Given("vengono conteggiati i record delle tabelle reali per la deliveryDate {string}, la provincia {string}, il prodotto {string} e il recapitista {string} e ciascuna contiene almeno un record")
    public void countRealTables(String deliveryDate, String province, String product, String driver) {
        Predicate<Map<DelayerTable, Integer>> allTablesNotEmpty = counts -> counts.values().stream().allMatch(count -> count > 0);
        try {
            realTablesSnapshot = Poller.pollUntil(() -> {
                        realTablesSnapshot = mockService.countRealTables(deliveryDate, province, product, driver);
                        return realTablesSnapshot;
                    },
                    allTablesNotEmpty,
                    TABLES_POLLING);
        } catch (PollingTimeoutException e) {
            Assertions.fail("Tabelle reali senza record per deliveryDate '%s', provincia '%s', prodotto '%s' e recapitista '%s': %s"
                    .formatted(deliveryDate, province, product, driver, emptyTables(realTablesSnapshot)));
        }
        log.info("Conteggio record tabelle reali prima di DELETE_MOCK_TABLES: {}", realTablesSnapshot);
    }

    private static List<String> emptyTables(Map<DelayerTable, Integer> counts) {
        if (counts == null) return List.of();
        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue() <= 0)
                .map(entry -> entry.getKey().tableName)
                .toList();
    }

    @Then("il numero di record delle tabelle reali per la deliveryDate {string}, la provincia {string}, il prodotto {string} e il recapitista {string} è uguale o maggiore a quello conteggiato prima di DELETE_MOCK_TABLES")
    public void verifyRealTablesNotReduced(String deliveryDate, String province, String product, String driver) {
        Assertions.assertThat(realTablesSnapshot)
                .as("Conteggio delle tabelle reali non effettuato prima di DELETE_MOCK_TABLES")
                .isNotNull();

        Map<DelayerTable, Integer> actual = mockService.countRealTables(deliveryDate, province, product, driver);
        log.info("Conteggio record tabelle reali dopo DELETE_MOCK_TABLES: {}", actual);

        SoftAssertions softly = new SoftAssertions();
        realTablesSnapshot.forEach((table, before) ->
                softly.assertThat(actual.get(table))
                        .as("%s: record prima di DELETE_MOCK_TABLES %d, dopo %d", table.tableName, before, actual.get(table))
                        .isGreaterThanOrEqualTo(before));
        softly.assertAll();
    }

    private void awaitNotEmptyMockTable(Supplier<Integer> sizeSupplier, String failMessage) {
        awaitMockTableSize(sizeSupplier, size -> size > 0, failMessage);
    }

    private void awaitMockTableSize(Supplier<Integer> sizeSupplier, IntPredicate condition, String failMessage) {
        try {
            Poller.pollUntil(sizeSupplier::get, condition::test, TABLES_POLLING);
        } catch (PollingTimeoutException e) {
            Assertions.fail(failMessage);
        }
    }
}
