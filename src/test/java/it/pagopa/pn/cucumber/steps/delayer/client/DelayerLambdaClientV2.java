package it.pagopa.pn.cucumber.steps.delayer.client;

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

//    public DelayerLambdaClientV2(LambdaInvoker lambdaInvoker, ObjectMapper objectMapper,   String lambdaName) {
//        this.lambdaInvoker = lambdaInvoker;
//        this.objectMapper = objectMapper;
//        this.lambdaName = lambdaName;
//    }

    public void importData(String filename, String deliveryWeek) {
        var params = new ArrayList<>(List.of(DelayerTable.DelayerPaperDelivery, DelayerTable.PaperDeliveryCounters, filename));
        if (!Objects.isNull(deliveryWeek) && !deliveryWeek.isBlank()) {
            params.add(deliveryWeek);
        }
        invoke(DelayerOperation.IMPORT_DATA, Void.class, params);
    }

    public void importData(String filename) {
        importData(filename, null);
    }

    public void deleteData(String filename) {
        var params = new ArrayList<Object>(List.of(DelayerTable.DelayerPaperDelivery, DelayerTable.PaperDeliveryDriverUsedCapacities, DelayerTable.PaperDeliveryUsedSenderLimit, DelayerTable.PaperDeliveryCounters));
        if (!Objects.isNull(filename) && !filename.isBlank()) {
            params.add(filename);
        }
        invoke(DelayerOperation.DELETE_DATA, Void.class, params);
    }

    public void deleteData() {
        deleteData(null);
    }

    public DelayerUsedCapacity getUsedCapacity(String unifiedDeliveryDriver, String geoKey, String deliveryDate) {
        var params = Map.of(DelayerTable.PaperDeliveryDriverUsedCapacities, unifiedDeliveryDriver, geoKey, deliveryDate);
        return invoke(DelayerOperation.GET_USED_CAPACITY, DelayerUsedCapacity.class, params);
    }

    public DelayerByRequestId getByRequestId(String requestId) {
        var params = List.of(requestId);
        return invoke(DelayerOperation.GET_BY_REQUEST_ID, DelayerByRequestId.class, params);
    }

    public void runAlgorithm(String printCapacity, String deliveryWeek) {
        var params = List.of(DelayerTable.DelayerPaperDelivery, DelayerTable.PaperDeliveryDriverCapacities, DelayerTable.PaperDeliveryDriverUsedCapacities,
                DelayerTable.PaperDeliverySenderLimit, DelayerTable.PaperDeliveryUsedSenderLimit, DelayerTable.PaperDeliveryCounters, printCapacity, deliveryWeek);
        invoke(DelayerOperation.RUN_ALGORITHM, Void.class, params);
    }

    public void delayerToPaperChannel() {
        var params = List.of(DelayerTable.DelayerPaperDelivery, DelayerTable.PaperDeliveryCounters);
        invoke(DelayerOperation.DELAYER_TO_PAPER_CHANNEL, Void.class, params);
    }

    public DelayerStatusExecution getStatusExecution(String executionArn) {
        var params = List.of(executionArn);
        return invoke(DelayerOperation.GET_STATUS_EXECUTION, DelayerStatusExecution.class, params);
    }

    public DelayerPaperDeliverys getPaperDelivery(String deliveryDate, WorkflowSteps workFlowStep, String lastEvaluatedKey) {
        var params = new ArrayList<>(List.of(DelayerTable.DelayerPaperDelivery, deliveryDate, workFlowStep.name()));
        if (!Objects.isNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
            params.add(lastEvaluatedKey);
        }
        return invoke(DelayerOperation.GET_PAPER_DELIVERY, DelayerPaperDeliverys.class, params);
    }

    public DelayerPaperDeliverys getPaperDelivery(String deliveryDate, WorkflowSteps workFlowStep) {
        return getPaperDelivery(deliveryDate, workFlowStep, null);
    }

    public DelayerSenderLimits getSenderLimitByProvince(String deliveryDate, String province) {
        var params = Map.of("deliveryDate", deliveryDate, "province", province);
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerSenderLimits getSenderLimitByProvince(String deliveryDate, String province, String lastEvaluatedKey) {
        var params = Map.of("deliveryDate", deliveryDate, "province", province, "lastEvaluatedKey", lastEvaluatedKey);
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerSenderLimits getSenderLimitByPk(String deliveryDate, String pk) {
        var params = Map.of("deliveryDate", deliveryDate, "pk", pk);
        return invoke(DelayerOperation.GET_SENDER_LIMIT, DelayerSenderLimits.class, params);
    }

    public DelayerUsedCapacity getUsedSenderLimitByProvince(String deliveryDate, String province) {
        var params = Map.of("table", DelayerTable.PaperDeliveryUsedSenderLimit, "deliveryDate", deliveryDate, "province", province);
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedCapacity.class, params);
    }

    public DelayerUsedCapacity getUsedSenderLimitByProvince(String deliveryDate, String province, String lastEvaluatedKey) {
        var params = Map.of("table", DelayerTable.PaperDeliveryUsedSenderLimit, "deliveryDate", deliveryDate, "province", province, "lastEvaluatedKey", lastEvaluatedKey);
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedCapacity.class, params);
    }

    public DelayerUsedCapacity getUsedSenderLimitByPk(String deliveryDate, String pk) {
        var params = Map.of("table", DelayerTable.PaperDeliveryUsedSenderLimit, "deliveryDate", deliveryDate, "pk", pk);
        return invoke(DelayerOperation.GET_USED_SENDER_LIMIT, DelayerUsedCapacity.class, params);
    }

    public DelayerPresigneUrlUpload getPresignedUrlUpload(String filename, String checksumSha256B64) {
        var params = Map.of("filename", filename, "checksumSha256B64", checksumSha256B64, "presignedUrlType", "UPLOAD");
        return invoke(DelayerOperation.GET_PRESIGNED_URL, DelayerPresigneUrlUpload.class, params);
    }

    public DelayerPresigneUrlDownload getPresignedUrlDownload(String filename) {
        var params = Map.of("filename", filename, "presignedUrlType", "DOWNLOAD");
        return invoke(DelayerOperation.GET_PRESIGNED_URL, DelayerPresigneUrlDownload.class, params);
    }

    public DelayerDeclaredCapacity getDeclaredCapacity(String province, String deliveryDate) {
        var params = List.of(DelayerTable.PaperDeliveryDriverCapacitiesMock, province, deliveryDate);
        return invoke(DelayerOperation.GET_DECLARED_CAPACITY, DelayerDeclaredCapacity.class, params);
    }

    public void insertMockCapacities(String filename) {
        var params = List.of(filename);
        invoke(DelayerOperation.INSERT_MOCK_CAPACITIES, Void.class, params);
    }

    private <T> T getCounters(DelayerCounterType counterType, String deliveryDate, Map<String, String> parameters, Class<T> responseType) {
        var paramMap = new HashMap<String, String>();
        paramMap.put("table", "pn-PaperDeliveryCounters");
        paramMap.put("counterType", counterType.name());
        paramMap.put("deliveryDate", deliveryDate);
        if (Objects.nonNull(parameters) && !parameters.isEmpty()) {
            paramMap.putAll(parameters);
        }
        return invoke(DelayerOperation.GET_COUNTERS, responseType, paramMap);
    }

    public DelayerCountersPrint getCountersPrint(String deliveryDate) {
        return getCounters(DelayerCounterType.PRINT, deliveryDate, null, DelayerCountersPrint.class);
    }

    public DelayerCountersSumEstimates getCountersSumEstimates(String province, String productType, String lastEvaluatedKey) {
        var params = new HashMap<String, String>();
        if (Objects.nonNull(province) && !province.isBlank()) {
            params.put("province", province);
        }
        if (Objects.nonNull(productType) && !productType.isBlank()) {
            params.put("productType", productType);
        }
        if (Objects.nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
            params.put("lastEvaluatedKey", lastEvaluatedKey);
        }
        return getCounters(DelayerCounterType.SUM_ESTIMATES, null, params, DelayerCountersSumEstimates.class);
    }

    public DelayerCountersExclude getCountersExclude(String province, String productType, String lastEvaluatedKey) {
        var params = new HashMap<String, String>();
        if (Objects.nonNull(province) && !province.isBlank()) {
            params.put("province", province);
        }
        if (Objects.nonNull(productType) && !productType.isBlank()) {
            params.put("productType", productType);
        }
        if (Objects.nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
            params.put("lastEvaluatedKey", lastEvaluatedKey);
        }
        return getCounters(DelayerCounterType.EXCLUDE, null, params, DelayerCountersExclude.class);
    }

    public DelayerResidualPapers getResidualPapers(String deliveryDate, String executionDate) {
        var params = new ArrayList<>(List.of(DelayerTable.DelayerPaperDeliveryJsonView, deliveryDate));
        if (!Objects.isNull(executionDate) && !executionDate.isBlank()) {
            params.add(executionDate);
        }
        return invoke(DelayerOperation.GET_RESIDUAL_PAPERS, DelayerResidualPapers.class, params);
    }

    public DelayerResidualPapers getResidualPapers(String deliveryDate) {
        return getResidualPapers(deliveryDate, null);
    }


    private <T> T invoke(DelayerOperation operationType, Class<T> responseType, Object parameters) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of("operationType", operationType, "parameters", parameters));
            return lambdaInvoker.invokeMyLambda(lambdaName, payload, responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON payload", e);
        }
    }

}
