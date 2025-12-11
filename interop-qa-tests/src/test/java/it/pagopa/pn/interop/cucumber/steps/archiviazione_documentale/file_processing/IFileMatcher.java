package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.S3BucketInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@FunctionalInterface
public interface IFileMatcher {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class MatchingStrategySeed{
        S3Client s3;
        FileType fileType;
        S3BucketInfo bucketName;
        SharedStepsContext sharedStepsContext;
        TokenResolver tokenResolver;
    }

    boolean match(MatchingStrategySeed seed) throws IOException;
}