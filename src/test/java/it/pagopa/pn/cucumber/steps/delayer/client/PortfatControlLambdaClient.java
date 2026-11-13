package it.pagopa.pn.cucumber.steps.delayer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.cucumber.steps.delayer.utils.SenderOrderCheckResponse;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfatControlLambdaClient {

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper;

    @Value("${pn.delayer.portfat.check.lambda.name}")
    private String lambdaName;

    public SenderOrderCheckResponse executeSenderOrderCheck() {
        return executeSenderOrderCheck(null);
    }

    public SenderOrderCheckResponse executeSenderOrderCheck(String currentMonth) {

        try {
            Map<String, Object> payload = new HashMap<>();

            if (currentMonth != null) {
                payload.put("currentMonth", currentMonth);
            }

            return lambdaInvoker.invokeMyLambda(
                    lambdaName,
                    objectMapper.writeValueAsString(payload),
                    SenderOrderCheckResponse.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Errore durante invocazione lambda sender-order-check", e);
        }
    }
}
