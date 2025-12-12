package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.agreement;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileMatcher;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class AgreementActivatedEventSignedStrategy implements IFileMatcher {

    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        InputStream s3Stream = S3Utils.getFileStream(seed.getS3(), seed.getBucketName());

        try {
            // Estrai il contenuto originale dal file .p7m
            CMSSignedData signedData = new CMSSignedData(s3Stream);
            CMSProcessable signedContent = signedData.getSignedContent();
            byte[] originalBytes = (byte[]) signedContent.getContent();

            // Convertilo in InputStream (è un .gz)
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(originalBytes))) {
                return FileUtils.ndjsonContainsAll(gis, buildConditions(seed));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Map<String, String> buildConditions(MatchingStrategySeed seed) {
        return Map.of(
                "event_name", "AgreementActivated",
                "id", seed.getTokenResolver().resolve(":agreementId"),
                "state", "Active"
        );
    }
}
