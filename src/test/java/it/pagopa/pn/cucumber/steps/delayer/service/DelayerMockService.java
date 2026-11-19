package it.pagopa.pn.cucumber.steps.delayer.service;

import it.pagopa.pn.cucumber.steps.delayer.client.DelayerLambdaClient;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerTable;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DelayerMockService {

    private final DelayerLambdaClient lambdaClient;

    public void deleteMockTables(String deliveryDate, String province) {
        lambdaClient.deleteMockTables();
        awaitMockTablesReady(deliveryDate, province);
    }

    // DELETE_MOCK_TABLES ricrea le tabelle in modo asincrono: si attende che una lettura vada a buon fine su ciascuna delle sei tabelle mock
    private void awaitMockTablesReady(String deliveryDate, String province) {
        Map<DelayerTable, Runnable> probes = new LinkedHashMap<>();
        probes.put(DelayerTable.PaperDeliverySenderLimitMock, () -> getMockSenderLimitsSize(deliveryDate, province));
        probes.put(DelayerTable.PaperDeliveryCountersMock, () -> getMockCountersSumEstimatesSize(deliveryDate, province, "890"));
        probes.put(DelayerTable.PaperDeliveryUsedSenderLimitMock, () -> lambdaClient.getUsedSenderLimitByProvince(DelayerTable.PaperDeliveryUsedSenderLimitMock, deliveryDate, province));
        probes.put(DelayerTable.PaperDeliveryDriverUsedCapacitiesMock, () -> lambdaClient.getUsedCapacity(DelayerTable.PaperDeliveryDriverUsedCapacitiesMock, "PROBE", province, deliveryDate));
        probes.put(DelayerTable.PaperDeliveryDriverCapacitiesMock, () -> lambdaClient.getDeclaredCapacity(DelayerTable.PaperDeliveryDriverCapacitiesMock, province, deliveryDate));
        probes.put(DelayerTable.DelayerPaperDeliveryMock, () -> lambdaClient.getPaperDelivery(DelayerTable.DelayerPaperDeliveryMock, deliveryDate, WorkflowSteps.EVALUATE_SENDER_LIMIT, null));

        Set<DelayerTable> notReady = new LinkedHashSet<>(probes.keySet());
        try {
            Awaitility.await()
                    .atMost(Duration.ofMinutes(5))
                    .pollInterval(Duration.ofSeconds(10))
                    .pollDelay(Duration.ZERO)
                    .until(() -> {
                        notReady.removeIf(table -> {
                            try {
                                probes.get(table).run();
                                return true;
                            } catch (Exception e) {
                                log.info("[DELETE_MOCK_TABLES] Tabella {} non ancora disponibile: {}", table, e.getMessage());
                                return false;
                            }
                        });
                        return notReady.isEmpty();
                    });
        } catch (ConditionTimeoutException e) {
            throw new IllegalStateException("Tabelle mock non disponibili dopo DELETE_MOCK_TABLES: " + notReady, e);
        }
    }

    public void deleteMockTables(String table) {
        lambdaClient.deleteMockTables(table);
    }

    public Integer getMockSenderLimitsSize(String deliveryDate, String province) {
        var response = lambdaClient.getSenderLimitByProvinceWithTable(DelayerTable.PaperDeliverySenderLimitMock.tableName, deliveryDate, province);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_SENDER_LIMIT returned no items list for the mock table");
        return response.getItems().size();
    }

    public Integer getMockCountersSumEstimatesSize(String deliveryDate, String province, String product) {
        var response = lambdaClient.getCountersSumEstimates(deliveryDate, province, product, true);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_COUNTERS returned no items list for the mock table");
        return response.getItems().size();
    }

    public Integer getMockPaperDeliverySize(String deliveryDate) {
        return Arrays.stream(WorkflowSteps.values())
                .mapToInt(ws -> {
                    var response = lambdaClient.getPaperDelivery(DelayerTable.DelayerPaperDeliveryMock, deliveryDate, ws, null);
                    if (response == null || response.getItems() == null)
                        throw new IllegalStateException("GET_PAPER_DELIVERY returned no items list for the mock table");
                    return response.getItems().size();
                })
                .sum();
    }

    public Integer getMockDeclaredCapacitySize(String province, String deliveryDate) {
        var response = lambdaClient.getDeclaredCapacity(DelayerTable.PaperDeliveryDriverCapacitiesMock, province, deliveryDate);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_DECLARED_CAPACITY returned no items list for the mock table");
        return response.getItems().size();
    }

    public Integer getMockUsedCapacitySize(String unifiedDeliveryDriver, String geoKey, String deliveryDate) {
        return lambdaClient.getUsedCapacity(DelayerTable.PaperDeliveryDriverUsedCapacitiesMock, unifiedDeliveryDriver, geoKey, deliveryDate)
                .map(usedCapacity -> 1)
                .orElse(0);
    }

    public Integer getMockUsedSenderLimitSize(String deliveryDate, String province) {
        var response = lambdaClient.getUsedSenderLimitByProvince(DelayerTable.PaperDeliveryUsedSenderLimitMock, deliveryDate, province);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_USED_SENDER_LIMIT returned no items list for the mock table");
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
