package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.time.Instant;

@Getter
@Builder
public class ArchivedDocument {
    private S3BucketInfo bucketInfo;
    private DocumentType type;
    private long contentLength;
    private String contentType;
    private Instant creationDate;
    private String retentionMode;
    private Instant retainUntilDate;
    private Boolean legalHoldEnabled;
    private InputStream content;
}
