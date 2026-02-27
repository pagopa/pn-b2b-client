package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Objects;

public class S3Utils {

    public static ResponseInputStream<GetObjectResponse> getFileStream(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketInfo.base())
                .key(bucketInfo.key())
                .build();

        return s3.getObject(req);
    }

    public static GetObjectRetentionResponse getRetentionInfo(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        try {
            GetObjectRetentionRequest retentionReq = GetObjectRetentionRequest.builder()
                    .bucket(bucketInfo.base())
                    .key(bucketInfo.key())
                    .build();

            return s3.getObjectRetention(retentionReq);

        } catch (S3Exception e) {
            System.out.println("Retention info non disponibile per " +
                    bucketInfo.fullPath() +
                    ": " + e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static GetObjectLegalHoldResponse getLegalHoldInfo(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        try {
            GetObjectLegalHoldRequest holdReq = GetObjectLegalHoldRequest.builder()
                    .bucket(bucketInfo.base())
                    .key(bucketInfo.key())
                    .build();

            return s3.getObjectLegalHold(holdReq);

        } catch (S3Exception e) {
            System.out.println("Legal hold non disponibile per " +
                    bucketInfo.base() + "/" + bucketInfo.key() +
                    ": " + e.awsErrorDetails().errorMessage());
            return null;
        }
    }

    public static HeadObjectResponse getHeader(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        HeadObjectRequest headReq = HeadObjectRequest.builder()
                .bucket(bucketInfo.base())
                .key(bucketInfo.key())
                .build();

        return s3.headObject(headReq);
    }

    private static void validateInput(S3Client s3, BucketUrl bucketInfo) {
        Objects.requireNonNull(s3, "S3 client must not be null");
        Objects.requireNonNull(bucketInfo, "BucketInfo must not be null");

        if (bucketInfo.base() == null || bucketInfo.base().isBlank()) {
            throw new IllegalArgumentException("Bucket name must not be null or empty");
        }

        if (bucketInfo.key() == null || bucketInfo.key().isBlank()) {
            throw new IllegalArgumentException("Object key must not be null or empty");
        }
    }

}
