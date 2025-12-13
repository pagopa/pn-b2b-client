package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.entry.KeyedFileTokenEntry;

import java.util.stream.Stream;

public interface IKeyedFileTokenSource extends IFileTokenSource {
    Stream<KeyedFileTokenEntry> entries();
}

