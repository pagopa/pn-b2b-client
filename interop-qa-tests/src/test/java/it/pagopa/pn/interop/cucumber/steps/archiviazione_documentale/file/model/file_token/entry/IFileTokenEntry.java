package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;

public sealed interface IFileTokenEntry permits KeyedFileTokenEntry, IndexedFileTokenEntry {
    FileToken fileToken();
}
