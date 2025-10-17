package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context.FileContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileTypes;
import lombok.Getter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMatcher {
    private final Map<FileTypes, FileMatchingStrategy> strategies = new HashMap<>();
    @Getter private final FileContext context = new FileContext();

    public FileMatcher() {
        strategies.put(FileTypes.ZIP, new ZipMatchingStrategy());
        strategies.put(FileTypes.PDF, new PdfMatchingStrategy());
    }

    public boolean match(S3Client s3, String bucketName, String key, String regex) throws IOException {
        FileTypes ext = FileTypes.valueOf(key.substring(key.lastIndexOf('.') + 1).toLowerCase());
        FileMatchingStrategy strategy = strategies.get(ext);

        if(strategy == null) throw new RuntimeException("Unknown file type " + ext);

        boolean match = strategy.match(s3, bucketName, key);
        if(match) extractMetadata(s3, bucketName, key);

        return match;
    }

    private void extractMetadata(S3Client s3, String bucketName, String key) {

        // Recupero i metadati classici via HeadObject
        HeadObjectRequest headReq = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        HeadObjectResponse headResp = s3.headObject(headReq);

        context.setContentLength(headResp.contentLength());
        context.setContentType(headResp.contentType());
        context.setCreationDate(headResp.lastModified());

        headResp.metadata().forEach(context::addUserMetadata);

        // Recupero retention info (se Object Lock attivo)
        try {
            GetObjectRetentionRequest retentionReq = GetObjectRetentionRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            GetObjectRetentionResponse retentionResp = s3.getObjectRetention(retentionReq);

            if (retentionResp.retention() != null) {
                context.setRetentionMode(retentionResp.retention().modeAsString());
                context.setRetainUntilDate(retentionResp.retention().retainUntilDate());
            }
        } catch (S3Exception e) {
            // Non tutti i bucket hanno Object Lock → gestiamo gracefully
            System.out.println("Retention non disponibile: " + e.awsErrorDetails().errorMessage());
        }

        // Recupero legal hold (se presente)
        try {
            GetObjectLegalHoldRequest holdReq = GetObjectLegalHoldRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            GetObjectLegalHoldResponse holdResp = s3.getObjectLegalHold(holdReq);

            if (holdResp.legalHold() != null) {
                context.setLegalHoldEnabled(holdResp.legalHold().statusAsString().equals("ON"));
            }
        } catch (S3Exception e) {
            System.out.println("Legal hold non disponibile: " + e.awsErrorDetails().errorMessage());
        }
    }
}

