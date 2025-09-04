package it.pagopa.pn.cucumber.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class LambdaInvoker {

    private LambdaClient lambdaClient;
    @Value("${spring.profiles.active}") private String activeProfile;

    private String getUserRole(){
        if(activeProfile.equals("dev")){
            return "ROLE_dev_core";
        } else if(activeProfile.equals("test")){
            return "ROLE_test_core";
        }

        throw new RuntimeException("Invalid profile active");
    }

    private LambdaClient getLambdaClient() {
        if (lambdaClient == null) {
            lambdaClient = LambdaClient.builder()
                    .httpClient(ApacheHttpClient.builder()
                            .maxConnections(50)
                            .connectionTimeout(Duration.ofSeconds(10))
                            .socketTimeout(Duration.ofSeconds(30))
                            .build())
                    .credentialsProvider(ProfileCredentialsProvider.create(getUserRole())) // in locale
                    //.credentialsProvider(DefaultCredentialsProvider.create()) // codebuild
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
}
