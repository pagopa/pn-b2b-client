package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;

public interface IValidationStrategy {
    boolean supports(ContentType contentType);
    ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional);
}
