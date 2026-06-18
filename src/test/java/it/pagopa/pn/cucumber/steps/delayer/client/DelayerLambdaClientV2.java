package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.cucumber.steps.delayer.model.*;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerCounterType;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerOperation;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.DelayerTable;
import it.pagopa.pn.cucumber.steps.delayer.model.enums.WorkflowSteps;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DelayerLambdaClientV2 {
    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper;
    @Value("${pn.delayer.lambda.arn}")
    private String lambdaName;

    public void importData(String filename, String deliveryWeek) {
        var params = paramsOf(
                DelayerTable.DelayerPaperDelivery,
                DelayerTable.PaperDeliveryCounters, // da codice esistente
                // DelayerTable.PaperDeliveryDriverCapacities, // da documentazione
                mandatory("filename", filename),
                deliveryWeek
        );
        invoke(DelayerOperation.IMPORT_DATA, Void.class, params);
    }

    public void importData(String filename) {
        importData(filename, null);
    }

    public void deleteData(String filename) {
        var params = paramsOf(
                DelayerTable.DelayerPaperDelivery,
                DelayerTable.PaperDeliveryDriverUsedCapacities,
                DelayerTable.PaperDeliveryUsedSenderLimit,
                DelayerTable.PaperDeliveryCounters,
                filename
        );

        invoke(DelayerOperation.DELETE_DATA, Void.class, params);
    }

    public void deleteData() {
        deleteData(null);
    }

    public Optional<DelayerUsedCapacity> getUsedCapacity(String unifiedDeliveryDriver,
                                                         String geoKey,
                                                         String deliveryDate) {
        var params = paramsOf(
                DelayerTable.PaperDeliveryDriverUsedCapacities,
                mandatory("unifiedDeliveryDriver", unifiedDeliveryDriver),
                mandatory("geokey", geoKey),
                mandatory("deliveryDate", deliveryDate)
        );

        var response = invoke(DelayerOperation.GET_USED_CAPACITY, JsonNode.class, params);

        if (response.has("message") && "Item not found".equalsIgnoreCase(response.path("message").asText())) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.convertValue(response, DelayerUsedCapacity.class));
    }

    public List<DelayerPaperDelivery> getByRequestId(String requestId) {
        var params = paramsOf(
                mandatory("requestId", requestId)
        );
        return invoke(DelayerOperation.GET_BY_REQUEST_ID, new TypeReference<>() {
        }, params);
    }

    public DelayerPayload runAlgorithm(Integer printCapacity, String deliveryWeek) {
        var params = paramsOf(
                DelayerTable.DelayerPaperDelivery,
                DelayerTable.PaperDeliveryDriverCapacities,
                DelayerTable.PaperDeliveryDriverUsedCapacities,
                DelayerTable.PaperDeliverySenderLimit,
                DelayerTable.PaperDeliveryUsedSenderLimit,
                DelayerTable.PaperDeliveryCounters,
                mandatory("printCapacity", printCapacity),
                mandatory("deliveryWeek", deliveryWeek)
        );

        return invoke(DelayerOperation.RUN_ALGORITHM, FirstStepFunctionResponseWrapper.class, params).getBody();

    }

    public DelayerPayload delayerToPaperChannel() {
        var params = paramsOf(
                DelayerTable.DelayerPaperDelivery,
                DelayerTable.PaperDeliveryCounters
        );
        return invoke(DelayerOperation.DELAYER_TO_PAPER_CHANNEL, DelayerPayload.class, params);
    }

    public DelayerStatusExecution getStatusExecution(String executionArn) {
        var params = paramsOf(
                mandatory("executionArn", executionArn)
        );
        return invoke(DelayerOperation.GET_STATUS_EXECUTION, DelayerStatusExecution.class, params);
    }

    public DelayerPaperDeliverys getPaperDelivery(String deliveryDate, WorkflowSteps workFlowStep, String lastEvaluatedKey) {
        var params = paramsOf(
                DelayerTable.DelayerPaperDelivery,
                mandatory("deliveryDate", deliveryDate),
                mandatory("workFlowStep", workFlowStep),
                lastEvaluatedKey
        );
        return invoke(DelayerOperation.GET_PAPER_DELIVERY, DelayerPaperDeliverys.class, params);
    }

    public DelayerPaperDeliverys getPaperDelivery(String deliveryDate, WorkflowSteps workFlowStep) {
        return getPaperDelivery(deliveryDate, workFlowStep, null);
    }

    public DelayerSenderLimits getSenderLimitByProvince(String deliveryDate, String province) {
        var params = mapOf(
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("province", province)
        );
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerSenderLimits getSenderLimitByProvince(String deliveryDate, String province, String lastEvaluatedKey) {
        var params = mapOf(
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("province", province),
                entry("lastEvaluatedKey", lastEvaluatedKey)
        );
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerSenderLimits getSenderLimitByPk(String deliveryDate, String pk) {
        var params = mapOf(
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("pk", pk)
        );
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerUsedSenderLimit getUsedSenderLimitByProvince(String deliveryDate, String province) {
        var params = mapOf(
                entry("table", DelayerTable.PaperDeliveryUsedSenderLimit),
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("province", province)
        );
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedSenderLimit.class, params);
    }

    public DelayerUsedSenderLimit getUsedSenderLimitByProvince(String deliveryDate, String province, String lastEvaluatedKey) {
        var params = mapOf(
                entry("table", DelayerTable.PaperDeliveryUsedSenderLimit),
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("province", province),
                entry("lastEvaluatedKey", lastEvaluatedKey)
        );
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedSenderLimit.class, params);
    }

    public DelayerUsedSenderLimit getUsedSenderLimitByPk(String deliveryDate, String pk) {
        var params = mapOf(
                entry("table", DelayerTable.PaperDeliveryUsedSenderLimit),
                mandatoryEntry("deliveryDate", deliveryDate),
                mandatoryEntry("pk", pk)
        );
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedSenderLimit.class, params);
    }

    public DelayerPresigneUrlUpload getPresignedUrlUpload(String filename, String checksumSha256B64) {
        var params = mapOf(
                mandatoryEntry("filename", filename),
                mandatoryEntry("checksumSha256B64", checksumSha256B64),
                entry("presignedUrlType", "UPLOAD")
        );
        return invoke(DelayerOperation.GET_PRESIGNED_URL, DelayerPresigneUrlUpload.class, params);
    }

    public DelayerPresigneUrlDownload getPresignedUrlDownload(String filename) {
        var params = mapOf(
                mandatoryEntry("filename", filename),
                entry("presignedUrlType", "DOWNLOAD")
        );
        return invoke(DelayerOperation.GET_PRESIGNED_URL, DelayerPresigneUrlDownload.class, params);
    }

    public DelayerDeclaredCapacity getDeclaredCapacity(String province, String deliveryDate) {
        var params = paramsOf(
                //DelayerTable.PaperDeliveryDriverCapacitiesMock,
                DelayerTable.PaperDeliveryDriverCapacities,
                mandatory("province", province),
                mandatory("deliveryDate", deliveryDate)
        );
        return invoke(DelayerOperation.GET_DECLARED_CAPACITY, DelayerDeclaredCapacity.class, params);
    }

    public void insertMockCapacities(String filename) {
        var params = paramsOf(
                mandatory("filename", filename)
        );
        invoke(DelayerOperation.INSERT_MOCK_CAPACITIES, Void.class, params);
    }

    private <T> T getCounters(DelayerCounterType counterType,
                              String deliveryDate,
                              Map<String, String> parameters,
                              Class<T> responseType) {

        var params = mapOf(
                entry("table", DelayerTable.PaperDeliveryCounters),
                mandatoryEntry("counterType", counterType),
                mandatoryEntry("deliveryDate", deliveryDate),
                entryMap(parameters)
        );

        return invoke(DelayerOperation.GET_COUNTERS, responseType, params);
    }

    public DelayerCountersPrint getCountersPrint(String deliveryDate) {
        return getCounters(
                DelayerCounterType.PRINT,
                deliveryDate,
                null,
                DelayerCountersPrint.class
        );
    }

    public DelayerCountersSumEstimates getCountersSumEstimates(String deliveryDate, String province,
                                                               String productType) {
        return getCountersSumEstimates(deliveryDate, province, productType, null);
    }

    public DelayerCountersSumEstimates getCountersSumEstimates(String deliveryDate, String province,
                                                               String productType,
                                                               String lastEvaluatedKey) {
        var params = mapOf(
                entry("province", province),
                entry("productType", productType),
                entry("lastEvaluatedKey", lastEvaluatedKey)
        );

        return getCounters(
                DelayerCounterType.SUM_ESTIMATES,
                deliveryDate,
                params,
                DelayerCountersSumEstimates.class
        );
    }

    public DelayerCountersExclude getCountersExclude(String deliveryDate, String province,
                                                     String productType) {
        return getCountersExclude(deliveryDate, province, productType, null);
    }

    public DelayerCountersExclude getCountersExclude(String deliveryDate, String province,
                                                     String productType,
                                                     String lastEvaluatedKey) {
        var params = mapOf(
                entry("province", province),
                entry("productType", productType),
                entry("lastEvaluatedKey", lastEvaluatedKey)
        );

        return getCounters(
                DelayerCounterType.EXCLUDE,
                deliveryDate,
                params,
                DelayerCountersExclude.class
        );
    }

    public DelayerResidualPapers getResidualPapers(String deliveryDate, String executionDate) {
        var params = paramsOf(
                DelayerTable.DelayerPaperDeliveryJsonView,
                mandatory("deliveryDate", deliveryDate),
                executionDate
        );
        return invoke(DelayerOperation.GET_RESIDUAL_PAPERS, DelayerResidualPapers.class, params);
    }

    public DelayerResidualPapers getResidualPapers(String deliveryDate) {
        return getResidualPapers(deliveryDate, null);
    }

    private <T> T invoke(DelayerOperation operationType, TypeReference<T> responseType, Object parameters) {
        try {
            String payload = objectMapper.writeValueAsString(
                    Map.of("operationType", operationType, "parameters", parameters)
            );
            return lambdaInvoker.invokeMyLambda(lambdaName, payload, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke lambda operation " + operationType, e);
        }
    }

    private <T> T invoke(DelayerOperation operationType, Class<T> responseType, Object parameters) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of("operationType", operationType, "parameters", parameters));
            return lambdaInvoker.invokeMyLambda(lambdaName, payload, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON payload", e);
        }
    }

    private static final String MAP_ENTRY_KEY = "__MAP__";

    private List<String> paramsOf(Object... values) {
        return Arrays.stream(values)
                .filter(value -> !isBlank(value))
                .map(Object::toString)
                .toList();
    }

    private Object mandatory(String name, Object value) {
        if (isBlank(value)) {
            log.warn("Parametro mancante: {}, stai testando un edge case", name);
        }
        return value;
    }

    private boolean isBlank(Object value) {
        return value == null || value.toString().isBlank();
    }

    private Map<String, String> mapOf(ParamEntry... entries) {
        Map<String, String> result = new HashMap<>();

        for (ParamEntry e : entries) {
            if (MAP_ENTRY_KEY.equals(e.key()) && e.value() instanceof Map<?, ?> m) {
                m.forEach((k, v) -> {
                    if (!isBlank(v)) {
                        result.put(k.toString(), v.toString());
                    }
                });
            } else if (!isBlank(e.value())) {
                result.put(e.key(), e.value().toString());
            }
        }

        return result;
    }

    private ParamEntry entry(Object key, Object value) {
        return new ParamEntry(key.toString(), value);
    }

    private ParamEntry mandatoryEntry(Object key, Object value) {
        mandatory(key.toString(), value);
        return entry(key, value);
    }

    private ParamEntry entryMap(Map<String, String> map) {
        return new ParamEntry(MAP_ENTRY_KEY, map);
    }

    private record ParamEntry(String key, Object value) {
    }

}
