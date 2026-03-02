package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;

import java.util.function.Function;
import java.util.stream.Stream;

public interface IFileTokenSource {
    Stream<FileToken> tokens();
}
