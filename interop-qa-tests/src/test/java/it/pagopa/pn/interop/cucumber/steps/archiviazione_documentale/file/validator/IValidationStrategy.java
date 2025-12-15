package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;

public interface IValidationStrategy {
    boolean supports(ContentType contentType);

    boolean validate(ProcessedFile file, IFileTokenSource resolvedTokens);
}
