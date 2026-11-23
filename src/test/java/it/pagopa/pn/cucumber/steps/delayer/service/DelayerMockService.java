package it.pagopa.pn.cucumber.steps.delayer.service;

import it.pagopa.common.polling.PollingConfig;
import it.pagopa.pn.client.b2b.pa.service.DynamoDbService;
import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerTable;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.steps.delayer.utils.DelayerProbeAwaiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.pn.cucumber.steps.delayer.utils.DelayerPaperDeliveryUtils.getPreviousMondayFromDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayerMockService {

    private static final PollingConfig MOCK_TABLES_POLLING = PollingConfig.of(Duration.ofMinutes(5), Duration.ofSeconds(10));

    /**
     * Le sei tabelle mock eliminate e ricreate da DELETE_MOCK_TABLES.
     */
    public static final List<DelayerTable> MOCK_TABLES = List.of(
            DelayerTable.PaperDeliverySenderLimitMock,
            DelayerTable.PaperDeliveryCountersMock,
            DelayerTable.PaperDeliveryDriverCapacitiesMock,
            DelayerTable.DelayerPaperDeliveryMock,
            DelayerTable.PaperDeliveryDriverUsedCapacitiesMock,
            DelayerTable.PaperDeliveryUsedSenderLimitMock
    );

    private final DelayerLambdaClient lambdaClient;
    private final DynamoDbService dynamoDbService;

    public void deleteMockTables() {
        lambdaClient.deleteMockTables();
        awaitMockTablesReady();
    }

    // DELETE_MOCK_TABLES ricrea le tabelle in modo asincrono: si attende che una Scan vada a buon fine su ciascuna delle sei tabelle mock
    // (durante delete/create la Scan fallisce con ResourceNotFoundException/ResourceInUseException e viene ritentata)
    private void awaitMockTablesReady() {
        Map<DelayerTable, Runnable> probes = new LinkedHashMap<>();
        MOCK_TABLES.forEach(table -> probes.put(table, () -> isMockTableEmpty(table)));
        DelayerProbeAwaiter.awaitAllSucceed("DELETE_MOCK_TABLES", probes, MOCK_TABLES_POLLING);
    }

    /**
     * Verifica sull'intera tabella (Scan Limit=1, costo fisso) che non contenga alcun record.
     */
    public boolean isMockTableEmpty(DelayerTable table) {
        return dynamoDbService.isEmpty(table.tableName);
    }

    public void deleteMockTables(String table) {
        lambdaClient.deleteMockTables(table);
    }

    public Integer getMockPaperDeliverySize(String deliveryDate) {
        return getPaperDeliverySize(DelayerTable.DelayerPaperDeliveryMock, deliveryDate);
    }

    public Integer getMockDeclaredCapacitySize(String province, String deliveryDate) {
        return getDeclaredCapacitySize(DelayerTable.PaperDeliveryDriverCapacitiesMock, province, deliveryDate);
    }

    public Integer getMockUsedCapacitySize(String unifiedDeliveryDriver, String geoKey, String deliveryDate) {
        return getUsedCapacitySize(DelayerTable.PaperDeliveryDriverUsedCapacitiesMock, unifiedDeliveryDriver, geoKey, deliveryDate);
    }

    public Integer getMockUsedSenderLimitSize(String deliveryDate, String province) {
        return getUsedSenderLimitSize(DelayerTable.PaperDeliveryUsedSenderLimitMock, deliveryDate, province);
    }

    /**
     * Conteggia i record presenti nelle sei tabelle reali speculari a quelle coinvolte da DELETE_MOCK_TABLES.
     * I limiti mittente utilizzati sono letti alla settimana precedente la deliveryDate (cfr. checkSenderLimitForPA).
     */
    public Map<DelayerTable, Integer> countRealTables(String deliveryDate, String province, String product, String unifiedDeliveryDriver) {
        Map<DelayerTable, Integer> counts = new LinkedHashMap<>();
        counts.put(DelayerTable.PaperDeliverySenderLimit, getSenderLimitsSize(DelayerTable.PaperDeliverySenderLimit, deliveryDate, province));
        counts.put(DelayerTable.PaperDeliveryCounters, getCountersSumEstimatesSize(deliveryDate, province, product, false));
        counts.put(DelayerTable.PaperDeliveryDriverCapacities, getDeclaredCapacitySize(DelayerTable.PaperDeliveryDriverCapacities, province, deliveryDate));
        counts.put(DelayerTable.DelayerPaperDelivery, getPaperDeliverySize(DelayerTable.DelayerPaperDelivery, deliveryDate));
        counts.put(DelayerTable.PaperDeliveryDriverUsedCapacities, getUsedCapacitySize(DelayerTable.PaperDeliveryDriverUsedCapacities, unifiedDeliveryDriver, province, deliveryDate));
        counts.put(DelayerTable.PaperDeliveryUsedSenderLimit, getUsedSenderLimitSize(DelayerTable.PaperDeliveryUsedSenderLimit, getPreviousMondayFromDate(deliveryDate, 1), province));
        return counts;
    }

    private Integer getSenderLimitsSize(DelayerTable table, String deliveryDate, String province) {
        var response = lambdaClient.getSenderLimitByProvinceWithTable(table.tableName, deliveryDate, province);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_SENDER_LIMIT returned no items list for table " + table.tableName);
        return response.getItems().size();
    }

    private Integer getCountersSumEstimatesSize(String deliveryDate, String province, String product, boolean fromMock) {
        var response = lambdaClient.getCountersSumEstimates(deliveryDate, province, product, fromMock);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_COUNTERS returned no items list for the " + (fromMock ? "mock" : "real") + " table");
        return response.getItems().size();
    }

    private Integer getPaperDeliverySize(DelayerTable table, String deliveryDate) {
        return Arrays.stream(WorkflowSteps.values())
                .mapToInt(ws -> {
                    var response = lambdaClient.getPaperDelivery(table, deliveryDate, ws, null);
                    if (response == null || response.getItems() == null)
                        throw new IllegalStateException("GET_PAPER_DELIVERY returned no items list for table " + table.tableName);
                    return response.getItems().size();
                })
                .sum();
    }

    private Integer getDeclaredCapacitySize(DelayerTable table, String province, String deliveryDate) {
        var response = lambdaClient.getDeclaredCapacity(table, province, deliveryDate);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_DECLARED_CAPACITY returned no items list for table " + table.tableName);
        return response.getItems().size();
    }

    private Integer getUsedCapacitySize(DelayerTable table, String unifiedDeliveryDriver, String geoKey, String deliveryDate) {
        return lambdaClient.getUsedCapacity(table, unifiedDeliveryDriver, geoKey, deliveryDate)
                .map(usedCapacity -> 1)
                .orElse(0);
    }

    private Integer getUsedSenderLimitSize(DelayerTable table, String deliveryDate, String province) {
        var response = lambdaClient.getUsedSenderLimitByProvince(table, deliveryDate, province);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_USED_SENDER_LIMIT returned no items list for table " + table.tableName);
        return response.getItems().size();
    }

    public void importDataMock(String csvName, String deliveryWeek) {
        lambdaClient.importDataMock(csvName, deliveryWeek);
    }

    public String runBatchWorkflowStateMachineMock(Integer printCapacity, String deliveryWeek) {
        return lambdaClient.runAlgorithmMock(printCapacity, deliveryWeek).getExecutionArn();
    }

    public void insertMockSenderLimit(String filename) {
        try {
            lambdaClient.insertMockSenderLimits(filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void insertMockCapacities(String filename) {
        lambdaClient.insertMockCapacities(filename);
    }
}
