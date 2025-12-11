package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.delegation;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileMatcher;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class ConsumerDelegationApprovedEventStrategy implements IFileMatcher {

    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        InputStream s3Stream = S3Utils.getFileStream(seed.getS3(), seed.getBucketName());

        try (GZIPInputStream gis = new GZIPInputStream(s3Stream)) {
            return FileUtils.ndjsonContainsAll(gis, buildConditions(seed));
        }
    }

    private Map<String, String> buildConditions(MatchingStrategySeed seed) {
        return Map.of(
                "event_name", "ConsumerDelegationApproved",
                "id", seed.getTokenResolver().resolve(":consumerDelegationId"),
                "state", "Active"
        );
    }
}
