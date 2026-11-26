package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Objects;

/**
 * Utility per il recupero dei file S3 e dei relativi metadati.
 */
@Slf4j
public class S3Utils {

    private static final String NO_OBJECT_LOCK_CONFIGURATION = "NoSuchObjectLockConfiguration";
    private static final String INVALID_REQUEST = "InvalidRequest";
    private static final String BUCKET_MISSING_OBJECT_LOCK = "Bucket is missing Object Lock Configuration";

    public static ResponseInputStream<GetObjectResponse> getFileStream(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketInfo.base())
                .key(bucketInfo.key())
                .build();

        return s3.getObject(req);
    }

    /**
     * Recupera la retention, oppure restituisce null se Object Lock non e configurato.
     * Una risposta valida puo non contenere retention. Gli altri errori vengono propagati.
     *
     * @param s3 client S3
     * @param bucketInfo bucket e chiave dell'oggetto
     * @return risposta S3, o null se la configurazione non e presente
     * @throws SdkException se la lettura della retention fallisce per altri motivi
     */
    public static GetObjectRetentionResponse getRetentionInfo(S3Client s3, BucketUrl bucketInfo) {
        validateInput(s3, bucketInfo);

        try {
            GetObjectRetentionRequest retentionReq = GetObjectRetentionRequest.builder()
                    .bucket(bucketInfo.base())
                    .key(bucketInfo.key())
                    .build();

            return s3.getObjectRetention(retentionReq);

        } catch (S3Exception e) {
            AwsErrorDetails details = e.awsErrorDetails();
            if (details != null && (NO_OBJECT_LOCK_CONFIGURATION.equals(details.errorCode())
                    || (INVALID_REQUEST.equals(details.errorCode())
                    && BUCKET_MISSING_OBJECT_LOCK.equals(details.errorMessage())))) {
                log.info("Retention non configurata per bucket {} e key {}: {}",
                        bucketInfo.base(), bucketInfo.key(), details.errorCode());
                return null;
            }
            log.error("Errore durante la lettura della retention per bucket {} e key {}",
                    bucketInfo.base(), bucketInfo.key(), e);
            throw e;
        } catch (SdkException e) {
            log.error("Errore durante la lettura della retention per bucket {} e key {}",
                    bucketInfo.base(), bucketInfo.key(), e);
            throw e;
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
