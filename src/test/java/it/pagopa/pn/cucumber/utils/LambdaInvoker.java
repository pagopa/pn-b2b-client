package it.pagopa.pn.cucumber.utils;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
        if (lambdaClient == null) {
            lambdaClient = LambdaClient.builder()
                    // SOLO IN LOCALE
                     .credentialsProvider(ProfileCredentialsProvider.create("ROLE_dev_core"))
                    //.credentialsProvider(DefaultCredentialsProvider.create())
                    .region(Region.EU_SOUTH_1).build();
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
}
