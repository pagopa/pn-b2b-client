package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.InputStream;
import java.time.Instant;

@Getter
@Setter
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

    public static ArchivedFile copyMetadataOf(ArchivedFile other) {
        return ArchivedFile.builder()
                .bucketInfo(other.bucketInfo)
                .type(other.type)
                .contentLength(other.contentLength)
                .contentType(other.contentType)
                .creationDate(other.creationDate)
                .retentionMode(other.retentionMode)
                .retainUntilDate(other.retainUntilDate)
                .legalHoldEnabled(other.legalHoldEnabled)
                .filename(other.filename)
                .build(); // content NON impostato
    }

}
