package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.pn.cucumber.steps.censimentoStimeMittenti.interfaces.SenderLimitCondition;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPayload;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSenderLimit;
import it.pagopa.pn.cucumber.steps.delayer.model.ExecutionStatusResponse;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DelayerLambdaClient {

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String lambdaName;

    @Data
    public static class SenderLimitResult {
        private final List<DelayerSenderLimit> items;
        private final String lastEvaluatedKey;
    }

    public DelayerLambdaClient(LambdaInvoker lambdaInvoker,@Value("${pn.delayer.lambda.arn}") String lambdaName) {
        this.lambdaInvoker = lambdaInvoker;
        this.lambdaName = lambdaName;
    }

    public String invoke(String operationType, String... parameters) throws Exception {
        String payload = buildPayload(operationType, parameters);
        String rawResult = lambdaInvoker.invokeMyLambda(lambdaName, payload);
        checkLambdaResponse(rawResult, operationType);
        return rawResult;
    }

    public String invokePortfatLambda(String operationType, String portfatLambdaName, String downloadUrl) throws Exception {
        String payload = buildFileReadyEventJson(downloadUrl);
        String rawResult = lambdaInvoker.invokeMyLambda(portfatLambdaName, payload);
        checkLambdaResponse(rawResult, operationType);
        return rawResult;
    }

    public String invoke(String operationType, Map<String, String> parameters) throws Exception {
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
//            String[] params = (lastEvaluatedKey != null && !lastEvaluatedKey.isBlank())
//                    ? new String[]{deliveryDate, province, lastEvaluatedKey}
//                    : new String[]{deliveryDate, province};

            Map<String, String> paramsMap = Map.of(
                    "deliveryDate", deliveryDate,
                    "province", province
            );

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

    private String buildFileReadyEventJson(String downloadUrl) {
        try {
            // body node
            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("downloadUrl", downloadUrl);
            bodyNode.put("fileVersion", "1.0.0");

            // root node
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("httpMethod", "POST");
            rootNode.put("resource", "/file-ready-event");

            // body must be a STRING containing JSON
            rootNode.put("body", bodyNode.toString());

            return rootNode.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build file-ready-event JSON", e);
        }
    }


    private String buildPayload(String operationType, Map<String, String> parameters) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("operationType", operationType);

            ObjectNode paramsNode = objectMapper.createObjectNode();
            parameters.forEach(paramsNode::put);

            root.set("parameters", paramsNode);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON payload", e);
        }
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

            throw new RuntimeException("Lambda [%s] failed: %s".formatted(operationType, message));
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
}