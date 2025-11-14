package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.strategy.purpose;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.strategy.FileMatchingStrategy;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;

import java.io.IOException;

public class PurposeTemplatePublishedStrategy implements FileMatchingStrategy {
    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        //TODO: trovare gli id da leggere all'interno del file
        return FileUtils.pdfContainsAllWords(S3Utils.getFileStream(seed.getS3(), seed.getBucketName()), null);
    }
}
