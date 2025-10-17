package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@FunctionalInterface
public interface FileMatchingStrategy {
    boolean match(S3Client s3, String bucketName, String key) throws IOException;
}