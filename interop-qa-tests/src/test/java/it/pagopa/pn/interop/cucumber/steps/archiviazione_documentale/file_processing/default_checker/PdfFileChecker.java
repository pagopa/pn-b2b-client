package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.IFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PdfFileChecker implements IFileChecker {
    private final TokenResolver tokenResolver;

    @Override
    public boolean hasToken(ArchivedFile file, IFileTokenSource fileTokenSource) {
        List<String> tokens = fileTokenSource.tokens().map(FileToken::token).toList();
        List<String> resolvedToken = tokenResolver.resolve(tokens);
        return FileUtils.pdfContainsAllWords(file.getContent(), resolvedToken);
    }
}
