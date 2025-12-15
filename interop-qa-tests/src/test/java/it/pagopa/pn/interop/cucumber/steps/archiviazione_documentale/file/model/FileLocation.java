package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;

public record FileLocation(
        BucketRole bucketRole,
        BucketUrl bucketUrl,
        FilenameFormat filenameFormat
) {
}
