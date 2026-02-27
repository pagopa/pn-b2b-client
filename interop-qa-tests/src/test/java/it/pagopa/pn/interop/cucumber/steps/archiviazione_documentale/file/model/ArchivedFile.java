package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
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
    private BucketUrl bucketInfo;
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
