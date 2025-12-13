package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.entry.IFileTokenEntry;

import java.util.stream.Stream;

public interface IFileTokenSource {
    Stream<FileToken> tokens();
}
