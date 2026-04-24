package it.pagopa.interop.tracing.client.polling;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Paths;
import java.util.function.Predicate;


public class S3Polling extends AbstractPolling<S3Client> {

    public S3Polling(Region region, Predicate<S3Client> condition) {
        super(S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create("extra-qa"))
                .build(),
                condition
        );
    }
}
