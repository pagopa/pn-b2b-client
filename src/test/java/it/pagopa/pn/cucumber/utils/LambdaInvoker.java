package it.pagopa.pn.cucumber.utils;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;

@Component
public class LambdaInvoker {

    private LambdaClient lambdaClient;

    private LambdaClient getLambdaClient() {
        // Lazy init per evitare carico anticipato
        if (lambdaClient == null) {
            lambdaClient = LambdaClient.builder().region(Region.EU_SOUTH_1).build();
        }
        return lambdaClient;
    }

    public String invokeMyLambda(String functionName, String payload) {
        InvokeRequest request = InvokeRequest.builder()
                .functionName("arn:aws:lambda:eu-south-1:151559006927:function:pn-ioAuthorizerLambda")
                .payload(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
                .build();

        InvokeResponse response = getLambdaClient().invoke(request);
        return response.payload().asUtf8String();
    }
}
