package it.pagopa.interop.tracing.client.polling;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.Predicate;


public class S3Polling extends AbstractPolling<S3Client> {

    public S3Polling(Region region, Predicate<S3Client> condition) {
        // usa il default credentials provider chain
        super(S3Client.builder().region(region).build(), condition);
    }
}
