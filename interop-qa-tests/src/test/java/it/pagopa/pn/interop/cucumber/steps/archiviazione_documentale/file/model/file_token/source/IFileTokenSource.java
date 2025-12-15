package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;

import java.util.function.Function;
import java.util.stream.Stream;

public interface IFileTokenSource {
    Stream<FileToken> tokens();

    /**
     * Trasforma i token mantenendo il contratto di IFileTokenSource.
     * Utile per risoluzioni, normalizzazioni, mapping funzionale.
     */
    default IFileTokenSource map(Function<FileToken, FileToken> mapper) {
        return () -> tokens().map(mapper);
    }
}
