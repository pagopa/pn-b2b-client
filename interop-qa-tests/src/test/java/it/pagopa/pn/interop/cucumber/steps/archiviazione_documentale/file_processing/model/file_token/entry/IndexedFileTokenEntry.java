package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.entry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.FileToken;

public record IndexedFileTokenEntry(int index, FileToken fileToken) implements IFileTokenEntry {
}
