package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class S3BucketInfo {
    private final String bucket;
    private final String prefix;
    private final String key;
}
