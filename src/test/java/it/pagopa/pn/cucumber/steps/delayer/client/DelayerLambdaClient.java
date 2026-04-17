package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.interfaces.SenderLimitCondition;
import it.pagopa.pn.cucumber.steps.delayer.model.*;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DelayerLambdaClient {

    public static final List<String> PAPER_DELIVERY_WORKFLOWSTEPS = List.of(
            "EVALUATE_SENDER_LIMIT",
            "EVALUATE_DRIVER_CAPACITY",
            "EVALUATE_RESIDUAL_CAPACITY",
            "EVALUATE_PRINT_CAPACITY",
            "SENT_TO_PREPARE_PHASE_2");
    public static final List<String> COUNTER_TYPES = List.of("PRINT", "SUM_ESTIMATES", "EXCLUDE");

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String lambdaName;

    @Data
    public static class SenderLimitResult {
        private final List<DelayerSenderLimit> items;
        private final String lastEvaluatedKey;
    }

    public DelayerLambdaClient(LambdaInvoker lambdaInvoker, String lambdaName) {
        this.lambdaInvoker = lambdaInvoker;
        this.lambdaName = lambdaName;
    }

    public String invoke(String operationType, Map<String, String> parameters) throws Exception {
        String payload = buildPayload(operationType, parameters);
        String rawResult = lambdaInvoker.invokeMyLambda(lambdaName, payload);
        checkLambdaResponse(rawResult, operationType);
        return rawResult;
    }

    public String invoke(String operationType, String... parameters) throws Exception {
        String payload = buildPayload(operationType, parameters);
        String rawResult = lambdaInvoker.invokeMyLambda(lambdaName, payload);
        checkLambdaResponse(rawResult, operationType);
        return rawResult;
    }

    private JsonNode getDriverCapacityNode(String driver, String provincia, String deliveryDate) {
        try {
            String response = invoke("GET_USED_CAPACITY", "pn-PaperDeliveryDriverUsedCapacities", driver, provincia, deliveryDate);
            return extractBody(response);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_USED_CAPACITY per driver %s".formatted(driver), e);
        }
    }

    public JsonNode getCounters(String counterType, String deliveryDate, String province, String productType, String lastEvaluatedKey) {
        try {
            if(Objects.isNull(counterType)) {
                log.warn("counterType è mandatorio, stai testando un egde case?");
            }
            if(!Objects.isNull(counterType) && !COUNTER_TYPES.contains(counterType)) {
                log.warn("counterType [{}] non valido, stai testando un egde case?", counterType);
            }
            if(Objects.isNull(deliveryDate)) {
                log.warn("deliveryDate è mandatorio, stai testando un egde case?");
            }
            var paramMap = new HashMap<String, String>();
            paramMap.put("table", "pn-PaperDeliveryCounters");
            paramMap.put("counterType", counterType);
            paramMap.put("deliveryDate", deliveryDate);
            if(Objects.nonNull(province) && !province.isBlank()) {
                paramMap.put("province", province);
            }
            if(Objects.nonNull(productType) && !productType.isBlank()) {
                paramMap.put("productType", productType);
            }
            if(Objects.nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
                paramMap.put("lastEvaluatedKey", lastEvaluatedKey);
            }

            String response = invoke("GET_COUNTERS", paramMap);
            return extractBody(response);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_COUNTERS", e);
        }
    }

    public JsonNode getPaperDelivery(String deliveryDate, String workFlowStep, String lastEvaluatedKey) {
        try {
            if(Objects.isNull(deliveryDate)) {
                log.warn("deliveryDate è mandatorio, stai testando un egde case?");
            }
            if(Objects.isNull(workFlowStep)) {
                log.warn("workFlowStep è mandatorio, stai testando un egde case?");
            }
            if(!Objects.isNull(workFlowStep) && !PAPER_DELIVERY_WORKFLOWSTEPS.contains(workFlowStep)) {
                log.warn("workFlowStep [{}] non valido, stai testando un egde case?", workFlowStep);
            }
            var paramArray = new String[]{"delayerPaperDeliveryTableName",deliveryDate,workFlowStep};
            if(Objects.nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
                paramArray = Arrays.copyOf(paramArray, paramArray.length + 1);
                paramArray[paramArray.length - 1] = lastEvaluatedKey;
            }

            String response = invoke("GET_PAPER_DELIVERY", paramArray);
            return extractBody(response);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_PAPER_DELIVERY", e);
        }
    }



    public int getUsedCapacity(String driver, String provincia, String deliveryDate) {
        JsonNode body = getDriverCapacityNode(driver, provincia, deliveryDate);
        return body.path("usedCapacity").asInt(-1);
    }

    public int getAvailableCapacity(String driver, String provincia, String deliveryDate) {
        JsonNode body = getDriverCapacityNode(driver, provincia, deliveryDate);
        int declared = body.path("declaredCapacity").asInt(-1);
        int used = body.path("usedCapacity").asInt(-1);
        if (declared == -1 && used == -1) {
            return -1;
        }
        return declared - used;
    }

    public FirstStepFunctionResponseWrapper.Payload runBatchWorkflowStateMachine(int printCapacity, String deliveryWeek) throws Exception {
        String rawResponse = invoke("RUN_ALGORITHM", "pn-DelayerPaperDelivery", "pn-PaperDeliveryDriverCapacities", "pn-PaperDeliveryDriverUsedCapacities",
                "pn-PaperDeliverySenderLimit", "pn-PaperDeliveryUsedSenderLimit", "pn-PaperDeliveryCounters", String.valueOf(printCapacity), deliveryWeek);

        try {
            // ===== LEVEL 1 =====
            FirstStepFunctionResponseWrapper outer =
                    objectMapper.readValue(rawResponse, FirstStepFunctionResponseWrapper.class);

            // ===== LEVEL 2 =====
            FirstStepFunctionResponseWrapper.Inner inner =
                    objectMapper.readValue(outer.getBody(), FirstStepFunctionResponseWrapper.Inner.class);

            // ===== LEVEL 3 ===== (payload finale)
            return objectMapper.readValue(inner.getBody(), FirstStepFunctionResponseWrapper.Payload.class);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante RUN_ALGORITHM", e);
        }
    }

    public SecondStepFunctionResponseWrapper.Payload runDelayerToPaperChannel() throws Exception {
        String rawResponse = invoke("DELAYER_TO_PAPER_CHANNEL", "pn-DelayerPaperDelivery", "pn-PaperDeliveryCounters");

        try {
            SecondStepFunctionResponseWrapper wrapper =
                    objectMapper.readValue(rawResponse, SecondStepFunctionResponseWrapper.class);

            SecondStepFunctionResponseWrapper.Payload payload =
                    objectMapper.readValue(wrapper.getBody(), SecondStepFunctionResponseWrapper.Payload.class);

            return payload;

        } catch (Exception e) {
            throw new RuntimeException("Errore durante RUN_ALGORITHM", e);
        }
    }


    public ExecutionStatusResponse getExecutionStatus(String executionArn) {
        try {
            String response = invoke("GET_STATUS_EXECUTION", executionArn);

            JsonNode body = extractBody(response);

            if (body == null || body.isMissingNode() || body.isNull()) {
                throw new RuntimeException("Body mancante nella risposta GET_STATUS_EXECUTION");
            }

            return objectMapper.treeToValue(body, ExecutionStatusResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_STATUS_EXECUTION per executionArn %s".formatted(executionArn), e);
        }
    }

    public DelayerPrintCapacityCounter getPrintCapacityCounter(String deliveryDate) {
        try {
            String response = invoke("GET_PRINT_CAPACITY_COUNTER", "pn-PaperDeliveryCounters", deliveryDate);
            JsonNode body = extractBody(response);

            if (body.isMissingNode() || body.isNull()) {
                log.warn("Nessun contatore trovato per deliveryDate {}", deliveryDate);
                return null;
            }

            return objectMapper.treeToValue(body, DelayerPrintCapacityCounter.class);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante GET_PRINT_CAPACITY_COUNTER per deliveryDate %s"
                            .formatted(deliveryDate),
                    e
            );
        }
    }

    public List<DelayerPaperDelivery> pollByRequestId(String requestId, int maxAttempts, int sleepMillis) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String response = invoke("GET_BY_REQUEST_ID", requestId);
            JsonNode body = extractBody(response);

            if (body.isArray() && !body.isEmpty()) {
                List<DelayerPaperDelivery> result = new ArrayList<>();
                for (JsonNode node : body) {
                    result.add(new DelayerPaperDelivery(node));
                }
                return result;
            }

            Thread.sleep(sleepMillis);
        }

        log.debug("Polling esaurito per requestId {}", requestId);
        return Collections.emptyList();
    }

    public List<DelayerPaperDelivery> findByWorkflowStep(Set<String> requestIds, String workflowStep, String deliveryDate, int maxMinutes) throws Exception {
        Set<DelayerPaperDelivery> found = new LinkedHashSet<>();
        Set<String> pending = new HashSet<>(requestIds);

        final int retryFrequency = 3000; // ms
        final int pollingFrequency = 200;     // ms per richiesta
        final long totalMinPollingTimeMillis = maxMinutes * 60L * 1000L;

        long startTime = System.currentTimeMillis();
        int attempt = 0;

        do {
            attempt++;

            long elapsedMillis = System.currentTimeMillis() - startTime;
            long remainingMillis = Math.max(0, totalMinPollingTimeMillis - elapsedMillis);
            long elapsedSeconds = elapsedMillis / 1000;
            long remainingSeconds = remainingMillis / 1000;

            log.info("Tentativo {} - RequestId rimanenti: {} | Workflow step: {} | Tempo trascorso: {}s | Tempo rimanente: {}s",
                    attempt, pending.size(), workflowStep, elapsedSeconds, remainingSeconds);

            Iterator<String> iterator = pending.iterator();
            while (iterator.hasNext()) {
                String requestId = iterator.next();
                List<DelayerPaperDelivery> results = pollByRequestId(requestId, 1, pollingFrequency);

                Optional<DelayerPaperDelivery> match = results.stream()
                        .filter(r -> r.getPk().contains(workflowStep) && r.getPk().contains(deliveryDate))
                        .findFirst();

                match.ifPresent(delivery -> {
                    found.add(delivery);
                    iterator.remove();
                });
            }

            if (!pending.isEmpty()) {
                Thread.sleep(retryFrequency);
            }

        } while (!pending.isEmpty() && (System.currentTimeMillis() - startTime < totalMinPollingTimeMillis));

        long elapsedMillis = System.currentTimeMillis() - startTime;
        long elapsedSeconds = elapsedMillis / 1000;

        String reason;
        if (pending.isEmpty()) {
            reason = "Tutte le notifiche trovate correttamente";
        } else if (elapsedMillis >= totalMinPollingTimeMillis) {
            reason = "Timeout raggiunto dopo almeno %d minuti".formatted(maxMinutes);
        } else {
            reason = "Uscita anticipata imprevista (possibile errore nel loop)";
        }

        if (found.size() != requestIds.size()) {
            log.warn("""
                    Trovate %d notifiche su %d attese per step '%s'
                    Tempo totale di polling: %d secondi
                    Motivo dell'uscita: %s
                    """.formatted(found.size(), requestIds.size(), workflowStep, elapsedSeconds, reason));
        } else {
            log.info("""
                    Tutte le %d notifiche trovate correttamente per step '%s'
                    Tempo di polling: %d secondi
                    """.formatted(found.size(), workflowStep, elapsedSeconds));
        }

        return new ArrayList<>(found);
    }

    public SenderLimitResult getSenderLimit(String deliveryDate, String province, String lastEvaluatedKey) {
        try {
            var paramsMap = new HashMap<String, String>();
            paramsMap.put("deliveryDate", deliveryDate);
            paramsMap.put("province", province);
            if(Objects.nonNull(lastEvaluatedKey) && !lastEvaluatedKey.isBlank()) {
                paramsMap.put("lastEvaluatedKey", lastEvaluatedKey);
            }

            String response = invoke("GET_SENDER_LIMIT", paramsMap);
            JsonNode body = extractBody(response);

            List<DelayerSenderLimit> items = new ArrayList<>();
            if (body.has("items") && body.get("items").isArray()) {
                for (JsonNode node : body.get("items")) {
                    items.add(new DelayerSenderLimit(node));
                }
            }

            JsonNode lekNode = body.path("lastEvaluatedKey");
            String lek = lekNode.isMissingNode() || lekNode.isEmpty() ? null : lekNode.toString();

            return new SenderLimitResult(items, lek);

        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_SENDER_LIMIT per provincia %s e data %s"
                    .formatted(province, deliveryDate), e);
        }
    }

    public List<DelayerSenderLimit> pollSenderLimit(String deliveryDate, String province, String lastEvaluatedKey, int maxAttempts, int sleepMillis) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            SenderLimitResult result = getSenderLimit(deliveryDate, province, lastEvaluatedKey);

            if (!result.getItems().isEmpty()) {
                return result.getItems();
            }

            Thread.sleep(sleepMillis);
        }

        log.debug("Polling esaurito per GET_SENDER_LIMIT su provincia {} e data {}", province, deliveryDate);
        return Collections.emptyList();
    }

    public void pollSenderLimitUntilCondition(String deliveryDate, String province, String lastEvaluatedKey, int maxAttempts, int sleepMillis, SenderLimitCondition condition) throws Exception {

        long startTime = System.currentTimeMillis();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            List<DelayerSenderLimit> items = getSenderLimit(deliveryDate, province, lastEvaluatedKey).getItems();

            log.info("Tentativo {} - Recuperati {} senderLimit per provincia {} e data {}",
                    attempt, items.size(), province, deliveryDate);

            if (condition.test(items)) {
                long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                log.info("Condizione soddisfatta dopo {} tentativi ({} secondi)", attempt, elapsedSeconds);
                return;
            }

            if (attempt < maxAttempts) {
                Thread.sleep(sleepMillis);
            }
        }

        throw new NoSuchElementException("Condizione non soddisfatta dopo %d tentativi (sleep=%d ms)"
                .formatted(maxAttempts, sleepMillis));
    }

    private String buildPayload(String operationType, String... parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"operationType\": \"").append(operationType).append("\", \"parameters\": [");

        for (int i = 0; i < parameters.length; i++) {
            sb.append("\"").append(parameters[i]).append("\"");
            if (i < parameters.length - 1) sb.append(", ");
        }

        sb.append("] }");
        return sb.toString();
    }

    private void checkLambdaResponse(String rawJson, String operationType) throws Exception {
        if (rawJson == null) {
            throw new RuntimeException("Lambda ha restituito null per " + operationType);
        }

        JsonNode root = objectMapper.readTree(rawJson);
        int statusCode = root.path("statusCode").asInt(-1);
        String bodyText = root.path("body").asText();

        if (statusCode != 200) {
            String message = bodyText.startsWith("{")
                    ? objectMapper.readTree(bodyText).path("message").asText("Errore sconosciuto")
                    : bodyText;

            throw new RuntimeException("Lambda [%s](%s) failed: %s".formatted(operationType, rawJson, message));
        }
    }

    private JsonNode extractBody(String rawJson) throws Exception {
        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode body = root.path("body");
        if (body.isTextual()) {
            return objectMapper.readTree(body.asText());
        }
        return body;
    }

    private String buildPayload(String operationType, Map<String, String> parameters) {
        try {
            return objectMapper.writeValueAsString(Map.of("operationType", operationType,"parameters", parameters ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON payload", e);
        }
    }
}
