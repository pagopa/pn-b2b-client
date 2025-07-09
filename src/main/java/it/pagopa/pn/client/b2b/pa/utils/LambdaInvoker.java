package it.pagopa.pn.client.b2b.pa.utils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;

public class LambdaInvoker {

    private final LambdaClient lambdaClient = LambdaClient.create();

    public String invokeMyLambda(String payload) {
        InvokeRequest request = InvokeRequest.builder()
                .functionName("nome-lambda")
                .payload(SdkBytes.fromString(payload, StandardCharsets.UTF_8))
                .build();

        InvokeResponse response = lambdaClient.invoke(request);
        return response.payload().asUtf8String();
    }
}
