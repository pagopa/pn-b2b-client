package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.cucumber.steps.delayer.model.DelayerPaperDelivery;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.*;

@Slf4j
public class DelayerLambdaClient {

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String lambdaName;

    public DelayerLambdaClient(LambdaInvoker lambdaInvoker, String lambdaName) {
        this.lambdaInvoker = lambdaInvoker;
        this.lambdaName = lambdaName;
    }

    public String invoke(String operationType, String... parameters) throws Exception {
        String payload = buildPayload(operationType, parameters);
        String rawResult = lambdaInvoker.invokeMyLambda(lambdaName, payload);
        checkLambdaResponse(rawResult, operationType);
        return rawResult;
    }

    public int getAvailableCapacity(String driver, String provincia, String deliveryDate) {
        try {
            String response = invoke("GET_USED_CAPACITY", driver, provincia, deliveryDate);
            JsonNode body = extractBody(response);
            int declared = body.path("declaredCapacity").asInt(-1);
            int used = body.path("usedCapacity").asInt(-1);
            return declared - used;
        } catch (Exception e) {
            throw new RuntimeException("Errore durante GET_USED_CAPACITY per driver %s".formatted(driver), e);
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

        log.warn("Polling esaurito per requestId {}", requestId);
        return Collections.emptyList();
    }

    public List<DelayerPaperDelivery> findByWorkflowStep(Set<String> requestIds, String workflowStep, String deliveryDate, int maxMinutes) throws Exception {
        Set<DelayerPaperDelivery> found = new LinkedHashSet<>();
        int pollingFrequency = 3000;
        int internalSleep = 500;
        int maxAttempts = Math.max(1, (maxMinutes * 60 * 1000) / (requestIds.size() * internalSleep + pollingFrequency));

        Set<String> pending = new HashSet<>(requestIds);

        for (int attempt = 1; attempt <= maxAttempts && !pending.isEmpty(); attempt++) {
            log.info("Tentativo {}/{} - RequestId rimanenti: {}", attempt, maxAttempts, pending.size());

            Iterator<String> iterator = pending.iterator();
            while (iterator.hasNext()) {
                String requestId = iterator.next();
                List<DelayerPaperDelivery> results = pollByRequestId(requestId, 1, internalSleep);

                Optional<DelayerPaperDelivery> match = results.stream()
                        .filter(r -> r.getPk().contains(workflowStep) && r.getPk().contains(deliveryDate))
                        .findFirst();

                match.ifPresent(delivery -> {
                    found.add(delivery);
                    iterator.remove();
                });
            }

            if (!pending.isEmpty()) Thread.sleep(pollingFrequency);
        }

        Assertions.assertEquals(requestIds.size(), found.size(),
                "Trovate %d notifiche su %d attese per step %s"
                        .formatted(found.size(), requestIds.size(), workflowStep));

        return new ArrayList<>(found);
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
