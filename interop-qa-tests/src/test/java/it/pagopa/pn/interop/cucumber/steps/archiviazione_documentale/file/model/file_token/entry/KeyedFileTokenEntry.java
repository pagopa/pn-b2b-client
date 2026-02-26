package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;

public record KeyedFileTokenEntry(String key, FileToken fileToken) implements IFileTokenEntry {
    @Override
    public FileToken fileToken() {
        return fileToken;
    }

    public static KeyedFileTokenEntry of(String key, FileToken fileToken) {
        return new KeyedFileTokenEntry(key, fileToken);
    }

    public String value() {
        return fileToken.expectedValue();
    }
}
