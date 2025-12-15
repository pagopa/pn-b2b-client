package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.List;

public class PdfValidationStrategy implements IValidationStrategy {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.PDF;
    }

    @Override
    public boolean validate(ProcessedFile file, IFileTokenSource resolvedTokens) {

        List<String> words = resolvedTokens.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        if (words.isEmpty()) {
            return true; // nessuna parola richiesta
        }

        return FileUtils.pdfContainsAllWords(
                file.content(),
                words
        );
    }
}
