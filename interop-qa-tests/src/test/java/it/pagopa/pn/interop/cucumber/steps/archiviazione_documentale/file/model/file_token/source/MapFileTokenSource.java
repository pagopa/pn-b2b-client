package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MapFileTokenSource implements IKeyedFileTokenSource {

    private final Map<String, FileToken> tokens;

    @Override
    public Stream<FileToken> tokens() {
        return tokens.values().stream();
    }

    @Override
    public Stream<KeyedFileTokenEntry> entries() {
        return tokens.entrySet().stream()
                .map(e -> new KeyedFileTokenEntry(e.getKey(), e.getValue()));
    }
}

