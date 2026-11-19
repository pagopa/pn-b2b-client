package it.pagopa.pn.cucumber.steps.delayer;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerContext;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerMockService;
import it.pagopa.pn.cucumber.steps.delayer.service.DelayerSevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.time.Duration;
import java.util.function.IntPredicate;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@ScenarioScope
public class DelayerMockSteps {

    private final DelayerContext context;
    private final DelayerMockService mockService;
    private final DelayerSevice service;

    @When("vengono eliminate e ricreate le tabelle mock tramite DELETE_MOCK_TABLES per la deliveryDate {string} e la provincia {string}")
    public void deleteMockTables(String deliveryDate, String province) {
        mockService.deleteMockTables(deliveryDate, province);
    }

    // La disponibilità delle tabelle mock è garantita dallo step DELETE_MOCK_TABLES: qui un errore di lettura fa fallire il test
    @Then("non devono esistere limiti mittente mock per la deliveryDate {string} e la provincia {string}")
    public void verifyNoMockSenderLimits(String deliveryDate, String province) {
        awaitEmptyMockTable(
                () -> mockService.getMockSenderLimitsSize(deliveryDate, province),
                "pn-PaperDeliverySenderLimitMock contiene ancora record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    @Then("non devono esistere contatori mock per la deliveryDate {string}, la provincia {string} e il prodotto {string}")
    public void verifyNoMockCounters(String deliveryDate, String province, String product) {
        awaitEmptyMockTable(
                () -> mockService.getMockCountersSumEstimatesSize(deliveryDate, province, product),
                "pn-PaperDeliveryCountersMock contiene ancora record per deliveryDate '%s', provincia '%s' e prodotto '%s'".formatted(deliveryDate, province, product));
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

    @Then("non devono esistere spedizioni mock in pn-DelayerPaperDeliveryMock per la deliveryDate {string}")
    public void verifyNoMockPaperDeliveries(String deliveryDate) {
        awaitEmptyMockTable(
                () -> mockService.getMockPaperDeliverySize(deliveryDate),
                "pn-DelayerPaperDeliveryMock contiene ancora record per deliveryDate '%s'".formatted(deliveryDate));
    }

    @Then("devono esistere capacità dichiarate mock per la deliveryDate {string} e la provincia {string}")
    public void verifyMockDeclaredCapacities(String deliveryDate, String province) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockDeclaredCapacitySize(province, deliveryDate),
                "pn-PaperDeliveryDriverCapacitiesMock non contiene record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    @Then("non devono esistere capacità dichiarate mock per la deliveryDate {string} e la provincia {string}")
    public void verifyNoMockDeclaredCapacities(String deliveryDate, String province) {
        awaitEmptyMockTable(
                () -> mockService.getMockDeclaredCapacitySize(province, deliveryDate),
                "pn-PaperDeliveryDriverCapacitiesMock contiene ancora record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    @Then("devono esistere capacità utilizzate mock per la deliveryDate {string}, il recapitista {string} e la geokey {string}")
    public void verifyMockUsedCapacities(String deliveryDate, String driver, String geoKey) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockUsedCapacitySize(driver, geoKey, deliveryDate),
                "pn-PaperDeliveryDriverUsedCapacitiesMock non contiene record per deliveryDate '%s', recapitista '%s' e geokey '%s'".formatted(deliveryDate, driver, geoKey));
    }

    @Then("non devono esistere capacità utilizzate mock per la deliveryDate {string}, il recapitista {string} e la geokey {string}")
    public void verifyNoMockUsedCapacities(String deliveryDate, String driver, String geoKey) {
        awaitEmptyMockTable(
                () -> mockService.getMockUsedCapacitySize(driver, geoKey, deliveryDate),
                "pn-PaperDeliveryDriverUsedCapacitiesMock contiene ancora record per deliveryDate '%s', recapitista '%s' e geokey '%s'".formatted(deliveryDate, driver, geoKey));
    }

    @Then("devono esistere limiti mittente utilizzati mock per la deliveryDate {string} e la provincia {string}")
    public void verifyMockUsedSenderLimits(String deliveryDate, String province) {
        awaitNotEmptyMockTable(
                () -> mockService.getMockUsedSenderLimitSize(deliveryDate, province),
                "pn-PaperDeliveryUsedSenderLimitMock non contiene record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    @Then("non devono esistere limiti mittente utilizzati mock per la deliveryDate {string} e la provincia {string}")
    public void verifyNoMockUsedSenderLimits(String deliveryDate, String province) {
        awaitEmptyMockTable(
                () -> mockService.getMockUsedSenderLimitSize(deliveryDate, province),
                "pn-PaperDeliveryUsedSenderLimitMock contiene ancora record per deliveryDate '%s' e provincia '%s'".formatted(deliveryDate, province));
    }

    private void awaitEmptyMockTable(Supplier<Integer> sizeSupplier, String failMessage) {
        awaitMockTableSize(sizeSupplier, size -> size == 0, failMessage);
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
