package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@RequiredArgsConstructor
public class GzFileChecker implements IFileChecker {
    private final TokenResolver tokenResolver;

    @Override
    public boolean hasToken(ArchivedFile file, IFileTokenSource source) {
        if (!(source instanceof IKeyedFileTokenSource keyedSource)) {
            throw new IllegalArgumentException(
                    "GzFileChecker requires a keyed token source"
            );
        }

        Map<String, String> resolvedTokenMap = keyedSource.entries().collect(
                Collectors.toMap(KeyedFileTokenEntry::key, e -> tokenResolver.resolve(e.fileToken().token())));


        try (GZIPInputStream gis = new GZIPInputStream(file.getContent())) {
            return FileUtils.ndjsonContainsAll(gis, resolvedTokenMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
