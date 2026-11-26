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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    /**
     * Le sei tabelle reali speculari a quelle mock, nello stesso ordine di {@link #MOCK_TABLES}.
     */
    public static final List<DelayerTable> REAL_TABLES = List.of(
            DelayerTable.PaperDeliverySenderLimit,
            DelayerTable.PaperDeliveryCounters,
            DelayerTable.PaperDeliveryDriverCapacities,
            DelayerTable.DelayerPaperDelivery,
            DelayerTable.PaperDeliveryDriverUsedCapacities,
            DelayerTable.PaperDeliveryUsedSenderLimit
    );

    // Chiavi primarie (partition key, sort key) delle tabelle reali, necessarie per la GetItem dei record campione
    private static final Map<DelayerTable, List<String>> REAL_TABLE_KEYS = Map.of(
            DelayerTable.PaperDeliverySenderLimit, List.of("pk", "deliveryDate"),
            DelayerTable.PaperDeliveryCounters, List.of("pk", "sk"),
            DelayerTable.PaperDeliveryDriverCapacities, List.of("pk", "activationDateFrom"),
            DelayerTable.DelayerPaperDelivery, List.of("pk", "sk"),
            DelayerTable.PaperDeliveryDriverUsedCapacities, List.of("unifiedDeliveryDriverGeokey", "deliveryDate"),
            DelayerTable.PaperDeliveryUsedSenderLimit, List.of("pk", "deliveryDate")
    );

    private static final int REAL_TABLE_SAMPLE_SIZE = 5;

    /**
     * Stato di una tabella reale prima di DELETE_MOCK_TABLES: data di creazione e ARN (cambiano se la tabella viene
     * eliminata e ricreata) e le chiavi di un campione di record esistenti.
     */
    public record RealTableSnapshot(DelayerTable table, Instant creationDateTime, String tableArn,
                                    List<Map<String, AttributeValue>> sampleKeys) {
    }

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
     * Salva lo stato di una tabella reale senza scriverci nulla: DescribeTable (deve essere ACTIVE) e Scan keys-only
     * di al massimo {@value #REAL_TABLE_SAMPLE_SIZE} record.
     */
    public RealTableSnapshot snapshotRealTable(DelayerTable table) {
        TableDescription description = dynamoDbService.describeTable(table.tableName);
        if (description.tableStatus() != TableStatus.ACTIVE)
            throw new IllegalStateException("La tabella reale %s non è ACTIVE: %s".formatted(table.tableName, description.tableStatusAsString()));
        List<Map<String, AttributeValue>> sampleKeys = dynamoDbService.scanKeys(table.tableName, REAL_TABLE_KEYS.get(table), REAL_TABLE_SAMPLE_SIZE);
        if (sampleKeys.isEmpty())
            log.warn("Tabella reale {} vuota: verificato solo stato/creazione", table.tableName);
        return new RealTableSnapshot(table, description.creationDateTime(), description.tableArn(), sampleKeys);
    }

    /**
     * Confronta lo stato attuale della tabella reale con lo snapshot: deve essere ACTIVE, con stessi creationDateTime
     * e ARN (se fosse stata eliminata e ricreata la data di creazione cambierebbe) e deve contenere ancora i record campione.
     *
     * @return le discrepanze rilevate (lista vuota se la tabella è invariata)
     */
    public List<String> verifyRealTableUnchanged(RealTableSnapshot before) {
        String tableName = before.table().tableName;
        TableDescription after = dynamoDbService.describeTable(tableName);
        List<String> differences = new ArrayList<>();
        if (after.tableStatus() != TableStatus.ACTIVE)
            differences.add("%s: stato %s invece di ACTIVE".formatted(tableName, after.tableStatusAsString()));
        if (!Objects.equals(before.creationDateTime(), after.creationDateTime()))
            differences.add("%s: creationDateTime prima %s, dopo %s".formatted(tableName, before.creationDateTime(), after.creationDateTime()));
        if (!Objects.equals(before.tableArn(), after.tableArn()))
            differences.add("%s: tableArn prima %s, dopo %s".formatted(tableName, before.tableArn(), after.tableArn()));
        before.sampleKeys().stream()
                .filter(key -> dynamoDbService.getItem(tableName, key).isEmpty())
                .forEach(key -> differences.add("%s: record con chiave %s non più presente".formatted(tableName, key)));
        return differences;
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

    /**
     * Pulizia record-level tramite DELETE_DATA sulle tabelle mock coinvolte (spedizioni, capacità utilizzate,
     * limiti mittente utilizzati, contatori), a partire dal CSV presente su S3.
     */
    public void deleteDataMock(String csvName) {
        lambdaClient.deleteDataMock(csvName);
    }

    public Integer getMockCountersPrintSize(String deliveryDate) {
        var response = lambdaClient.getCountersPrint(deliveryDate, true);
        if (response == null || response.getItems() == null)
            throw new IllegalStateException("GET_COUNTERS returned no items list for table " + DelayerTable.PaperDeliveryCountersMock.tableName);
        return response.getItems().size();
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
