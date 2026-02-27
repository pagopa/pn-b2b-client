package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ArchivedFile;

public record ArchivedFileMatched(ArchivedFile file, ValidationResult validation) {
    public boolean found() { return file != null; }
}
