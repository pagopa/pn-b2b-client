package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.entry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.FileToken;

public record KeyedFileTokenEntry(String key, FileToken token) implements IFileTokenEntry {
    @Override
    public FileToken fileToken() {
        return null;
    }
}
