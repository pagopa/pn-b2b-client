package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.function.BiFunction;

public class S3Polling extends AbstractS3Polling {

    private final BiFunction<S3Client, String, Boolean> condition;

    public S3Polling(String bucketName, Region region,
                           BiFunction<S3Client, String, Boolean> condition) {
        super(bucketName, region);
        this.condition = condition;
    }

    @Override
    protected boolean checkCondition(S3Client s3, String bucketName) {
        return false;
    }
}
