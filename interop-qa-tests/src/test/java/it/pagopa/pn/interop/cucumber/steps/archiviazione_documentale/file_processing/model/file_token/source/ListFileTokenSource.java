package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.FileToken;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ListFileTokenSource implements IFileTokenSource {
    private final List<FileToken> tokens;

    @Override
    public Stream<FileToken> tokens() {
        return tokens.stream();
    }
}
