package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ListFileTokenSource implements IListedFileTokenSource {

    private final List<FileToken> tokens;

    public static ListFileTokenSource of(String... tokens) {
        return new ListFileTokenSource(Arrays.stream(tokens).map(FileToken::ofValue).toList());
    }

    public static ListFileTokenSource of(List<String> tokens) {
        return new ListFileTokenSource(tokens.stream().map(FileToken::ofValue).toList());
    }

    @Override
    public Stream<FileToken> tokens() {
        return tokens.stream();
    }

    @Override
    public IListedFileTokenSource map(Function<FileToken, FileToken> mapper) {
        return new ListFileTokenSource(
                tokens.stream().map(mapper).toList()
        );
    }
}

