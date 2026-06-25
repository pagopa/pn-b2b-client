package it.pagopa.interop.tracing.client;

import it.pagopa.interop.tracing.client.polling.S3Polling;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Slf4j
public class TracingS3Client {

    @Data
    @Builder
    public static class PollingSpecification {

        @Builder.Default
        private long timeoutMs = 10_000;

        @Builder.Default
        private long pollIntervalMs = 1_000;
    }

    public boolean isFileExistingInS3Bucket(PollingSpecification spec, String bucketName, String filePathKey) {

        AtomicReference<Boolean> foundFileInBucket = new AtomicReference<>();
        foundFileInBucket.set(null);

        S3Polling polling = new S3Polling(Region.EU_SOUTH_1, s3 -> {
            try {
                HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePathKey)
                        .build();

                s3.headObject(headObjectRequest);
                foundFileInBucket.set(true);
                return true;

            } catch (NoSuchKeyException e) {
                foundFileInBucket.set(false);
                return false;

            } catch (S3Exception e) {
                // Not possible to check if the file exists in the bucket
                throw e;
            }
        });

        polling.executePolling(
                maxAttempts(spec),
                spec.getPollIntervalMs()
        );

        return foundFileInBucket.get();
    }

    public String getTextualFileContentFromS3Bucket(PollingSpecification spec, String bucketName, String filePathKey) {

        AtomicReference<String> fileContent = new AtomicReference<>();

        S3Polling polling = new S3Polling(Region.EU_SOUTH_1, s3 -> {

            try {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(filePathKey)
                        .build();

                ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
                fileContent.set(objectBytes.asString(StandardCharsets.UTF_8));
                return true;

            } catch (S3Exception e) {
                // Not possible to check if the file exists in the bucket
                throw e;
            }
        });

        polling.executePolling(
                maxAttempts(spec),
                spec.getPollIntervalMs()
        );

        return fileContent.get();
    }

    private int maxAttempts(PollingSpecification spec) {return (int) ((spec.getTimeoutMs() / spec.getPollIntervalMs()) + 1);}

}
