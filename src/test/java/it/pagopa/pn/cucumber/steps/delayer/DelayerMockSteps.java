package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerTable;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerMockService;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@ScenarioScope
public class DelayerMockSteps {

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
            Awaitility.await()
                    .atMost(Duration.ofMinutes(5))
                    .pollInterval(Duration.ofSeconds(10))
                    .pollDelay(Duration.ZERO)
                    .until(() -> mockService.isMockTableEmpty(table));
        } catch (ConditionTimeoutException e) {
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

    @Given("vengono conteggiati i record delle tabelle reali per la deliveryDate {string}, la provincia {string}, il prodotto {string} e il recapitista {string}")
    public void countRealTables(String deliveryDate, String province, String product, String driver) {
        realTablesSnapshot = mockService.countRealTables(deliveryDate, province, product, driver);
        log.info("Conteggio record tabelle reali prima di DELETE_MOCK_TABLES: {}", realTablesSnapshot);
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
            Awaitility.await()
                    .atMost(Duration.ofMinutes(5))
                    .pollInterval(Duration.ofSeconds(10))
                    .pollDelay(Duration.ZERO)
                    .until(() -> condition.test(sizeSupplier.get()));
        } catch (ConditionTimeoutException e) {
            Assertions.fail(failMessage);
        }
    }
}
