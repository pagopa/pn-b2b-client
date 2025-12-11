package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.InputStream;
import java.time.Instant;

@Getter
@Builder
@ToString
public class ArchivedFile {
    private S3BucketInfo bucketInfo;
    private FileType type;
    private long contentLength;
    private String contentType;
    private Instant creationDate;
    private String retentionMode;
    private Instant retainUntilDate;
    private Boolean legalHoldEnabled;
    private InputStream content;
    private String filename;
}
