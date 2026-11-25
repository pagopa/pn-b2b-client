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
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerMockService.RealTableSnapshot;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@ScenarioScope
public class DelayerMockSteps {

    private static final PollingConfig TABLES_POLLING = PollingConfig.of(Duration.ofMinutes(5), Duration.ofSeconds(10));

    private final DelayerContext context;
    private final DelayerMockService mockService;
    private final DelayerSevice service;

    private Map<DelayerTable, RealTableSnapshot> realTablesSnapshot;

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

    // Sola lettura sulle tabelle reali (DescribeTable + Scan keys-only con Limit): nessun polling, i dati reali sono già presenti o non lo sono
    @Given("viene salvato un campione di record dalle tabelle reali del Delayer")
    public void snapshotRealTables() {
        realTablesSnapshot = new LinkedHashMap<>();
        DelayerMockService.REAL_TABLES.forEach(table -> {
            RealTableSnapshot snapshot = mockService.snapshotRealTable(table);
            realTablesSnapshot.put(table, snapshot);
            log.info("Tabella reale {} prima di DELETE_MOCK_TABLES: {} record campione, creationDateTime {}",
                    table.tableName, snapshot.sampleKeys().size(), snapshot.creationDateTime());
        });
    }

    @Then("le tabelle reali del Delayer non sono state ricreate e contengono ancora i record campione")
    public void verifyRealTablesUnchanged() {
        Assertions.assertThat(realTablesSnapshot)
                .as("Campione delle tabelle reali non salvato prima di DELETE_MOCK_TABLES")
                .isNotNull();

        SoftAssertions softly = new SoftAssertions();
        realTablesSnapshot.forEach((table, before) ->
                softly.assertThat(mockService.verifyRealTableUnchanged(before))
                        .as("%s modificata da DELETE_MOCK_TABLES", table.tableName)
                        .isEmpty());
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
