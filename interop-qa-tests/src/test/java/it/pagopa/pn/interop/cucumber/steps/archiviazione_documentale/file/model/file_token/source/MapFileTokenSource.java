package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MapFileTokenSource implements IKeyedFileTokenSource {

    private final Map<String, FileToken> tokens;

    public static MapFileTokenSource of(Map<String, FileToken> tokens) {
        return new MapFileTokenSource(
                tokens.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public static MapFileTokenSource of(Object... keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("Expected even number of arguments (key, value)");
        }

        @SuppressWarnings("unchecked")
        Map.Entry<String, FileToken>[] entries =
                new Map.Entry[keyValuePairs.length / 2];

        for (int i = 0, e = 0; i < keyValuePairs.length; i += 2, e++) {
            Object key = keyValuePairs[i];
            Object value = keyValuePairs[i + 1];

            String k = (String) key; // ClassCastException = JDK-style failure

            FileToken token;
            if (value instanceof FileToken ft) {
                token = ft;
            } else if (value instanceof String s) {
                token = FileToken.ofValue(s);
            } else {
                throw new IllegalArgumentException(
                        "Value for key '" + k + "' must be String or FileToken, got: " +
                                (value == null ? "null" : value.getClass().getName())
                );
            }

            entries[e] = Map.entry(k, token); // null + duplicate check delegati al JDK
        }

        return new MapFileTokenSource(Map.ofEntries(entries));
    }

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


