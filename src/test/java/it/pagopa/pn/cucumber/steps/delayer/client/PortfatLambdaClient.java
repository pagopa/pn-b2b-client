package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfatLambdaClient {
    @Value("${pn.delayer.portfat.lambda.name}")
    private String portfatLambdaName;

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper;

    /**
     * Invoca la lambda Portfat con evento file-ready (downloadUrl del file zip elaborato).
     */
    public void invokePortfatFileReady(String downloadUrl) {
        try {
            log.info("Invoking Portfat Lambda file-ready-event");
            String rawResult = lambdaInvoker.invokeMyLambda(portfatLambdaName, buildFileReadyEventJson(downloadUrl));
            JsonNode root = objectMapper.readTree(rawResult);
            int statusCode = root.path("statusCode").asInt(-1);
            if (statusCode != 200 && statusCode != -1) {
                throw new RuntimeException("Portfat lambda failed: " + root.path("body").asText());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke portfat file-ready lambda", e);
        }
    }

    private String buildFileReadyEventJson(String downloadUrl) {
        try {
            ObjectNode bodyNode = objectMapper.createObjectNode();
            bodyNode.put("downloadUrl", downloadUrl);
            bodyNode.put("fileVersion", "1.0.0");

            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("httpMethod", "POST");
            rootNode.put("resource", "/file-ready-event");
            rootNode.put("body", bodyNode.toString());

            return rootNode.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build file-ready-event JSON", e);
        }
    }
}
