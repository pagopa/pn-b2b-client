package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;

import java.util.stream.Stream;

public interface IKeyedFileTokenSource extends IFileTokenSource {
    Stream<KeyedFileTokenEntry> entries();

    @Override
    default Stream<FileToken> tokens() {
        return entries().map(KeyedFileTokenEntry::fileToken);
    }
}

