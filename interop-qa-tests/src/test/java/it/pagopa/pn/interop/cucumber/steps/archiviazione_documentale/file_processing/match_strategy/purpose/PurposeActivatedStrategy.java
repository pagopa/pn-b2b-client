package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.purpose;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileMatcher;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.S3Utils;

import java.io.IOException;
import java.util.List;

public class PurposeActivatedStrategy implements IFileMatcher {
    @Override
    public boolean match(MatchingStrategySeed seed) throws IOException {
        TokenResolver tokenResolver = new TokenResolver(seed.getSharedStepsContext());
        List<String> ids = tokenResolver.resolve(List.of("Analisi del rischio"));

        return FileUtils.pdfContainsAllWords(S3Utils.getFileStream(seed.getS3(), seed.getBucketName()), ids);
    }
}
