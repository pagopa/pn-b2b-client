package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.context;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FileContext {
    private final Map<String, String> userMetadata = new HashMap<>();
    private long contentLength;
    private String contentType;
    private String retentionMode;
    private Instant retainUntilDate;
    private Instant creationDate;
    private boolean legalHoldEnabled;

    public void addUserMetadata(String key, String value) {
        userMetadata.put(key, value);
    }
}
