package it.pagopa.pn.cucumber.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LambdaInvoker {

    private final ObjectMapper objectMapper;
    private LambdaClient lambdaClient;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private String getUserRole() {
        return switch (activeProfile) {
            case "dev" -> "ROLE_dev_core";
            case "test" -> "ROLE_test_core";
            case "uat" -> "ROLE_uat_core";
            case "hotfix" -> "ROLE_hotfix_core";
            default -> throw new RuntimeException("Invalid profile active");
        };


    }

    private LambdaClient getLambdaClient() {
        if (lambdaClient == null) {
            lambdaClient = LambdaClient.builder()
                    .httpClient(ApacheHttpClient.builder()
                            .maxConnections(50)
                            .connectionTimeout(Duration.ofSeconds(20))      // connessione iniziale
                            .socketTimeout(Duration.ofSeconds(180))         // attesa risposta
                            .connectionAcquisitionTimeout(Duration.ofSeconds(60))
                            .connectionMaxIdleTime(Duration.ofSeconds(300)) // evita chiusura a 60s
                            .tcpKeepAlive(true)
                            .build())
                    .overrideConfiguration(c -> c
                            .apiCallAttemptTimeout(Duration.ofSeconds(180))
                            .apiCallTimeout(Duration.ofMinutes(6)))
//                    .credentialsProvider(ProfileCredentialsProvider.create(getUserRole())) // locale
                    .credentialsProvider(DefaultCredentialsProvider.create()) // codebuild
                    .region(Region.EU_SOUTH_1)
                    .build();
        }
        return lambdaClient;
    }


    public String invokeMyLambda(String functionName, String payload) {
        InvokeRequest request = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
                .build();

        InvokeResponse response = getLambdaClient().invoke(request);
        return response.payload().asUtf8String();
    }

    public <T> T invokeMyLambda(String functionName, Object payload, Class<T> bodyClass) {
        try {
            String payloadJson = payload instanceof String s
                    ? s
                    : objectMapper.writeValueAsString(payload);

            String rawResponse = invokeMyLambda(functionName, payloadJson);

            JsonNode root = objectMapper.readTree(rawResponse);
            int statusCode = root.path("statusCode").asInt(-1);

            JsonNode body = root.path("body");

            if (statusCode != 200) {
                String bodyText = body.isTextual() ? body.asText() : body.toString();
                throw new RuntimeException("Lambda failed: " + bodyText);
            }

            if (body.isTextual()) {
                return objectMapper.readValue(body.asText(), bodyClass);
            }

            return objectMapper.treeToValue(body, bodyClass);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante invocazione Lambda [%s] e conversione body in [%s]"
                            .formatted(functionName, bodyClass.getSimpleName()),
                    e
            );
        }
    }

    public <T> T invokeMyLambda(String functionName, Object payload, TypeReference<T> bodyType) {
        try {
            String payloadJson = payload instanceof String s
                    ? s
                    : objectMapper.writeValueAsString(payload);

            String rawResponse = invokeMyLambda(functionName, payloadJson);

            JsonNode root = objectMapper.readTree(rawResponse);
            int statusCode = root.path("statusCode").asInt(-1);

            JsonNode body = root.path("body");

            if (statusCode != 200) {
                String bodyText = body.isTextual() ? body.asText() : body.toString();
                throw new RuntimeException("Lambda failed: " + bodyText);
            }

            if (body.isTextual()) {
                return objectMapper.readValue(body.asText(), bodyType);
            }

            return objectMapper.convertValue(body, bodyType);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante invocazione Lambda [%s] e conversione body"
                            .formatted(functionName),
                    e
            );
        }
    }
}
