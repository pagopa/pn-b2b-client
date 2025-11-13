package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@FunctionalInterface
public interface FileMatchingStrategy {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MatchingStrategySeed{
        S3Client s3;
        FileType fileType;
        S3BucketInfo bucketName;
    }

    boolean match(MatchingStrategySeed seed) throws IOException;
}