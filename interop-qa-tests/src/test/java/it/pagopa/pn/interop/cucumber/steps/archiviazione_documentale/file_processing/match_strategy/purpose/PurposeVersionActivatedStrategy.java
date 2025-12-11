package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.purpose;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileMatcher;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;

import java.io.IOException;

public class PurposeVersionActivatedStrategy implements IFileMatcher {
    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        //TODO: trovare gli id da leggere all'interno del file
        return FileUtils.pdfContainsAllWords(S3Utils.getFileStream(seed.getS3(), seed.getBucketName()), null);
    }
}
