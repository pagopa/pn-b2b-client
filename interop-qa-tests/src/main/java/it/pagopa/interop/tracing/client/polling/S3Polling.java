package it.pagopa.interop.tracing.client.polling;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Predicate;


public class S3Polling extends AbstractPolling<S3Client> {

    public S3Polling(Region region, Predicate<S3Client> condition) {
        super(S3Client.builder()
                .region(region)
                // S3 per Tracing feature si raggiunge nell'ambiente Extra QA
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build(),
                condition
        );
    }
}
