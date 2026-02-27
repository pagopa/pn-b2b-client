package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;

import java.util.function.Function;
import java.util.stream.Stream;

public interface IListedFileTokenSource extends IFileTokenSource {
    Stream<FileToken> tokens();
    IListedFileTokenSource map(Function<FileToken, FileToken> mapper);
}
