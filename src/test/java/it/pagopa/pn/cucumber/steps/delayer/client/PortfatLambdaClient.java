package it.pagopa.pn.cucumber.steps.delayer.client;

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


    public void invokePortfatLambda(String downloadUrl) {
        String payload = buildFileReadyEventJson(downloadUrl);
        try {
            log.info("Invoking Portfat Lambda with payload: {}", payload);
            lambdaInvoker.invokeMyLambda(portfatLambdaName, payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke Portfat Lambda", e);
        }
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


}
