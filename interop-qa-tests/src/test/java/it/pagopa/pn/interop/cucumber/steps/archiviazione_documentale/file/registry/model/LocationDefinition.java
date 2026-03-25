package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FilenameFormat;

public record LocationDefinition(
        BucketRole role,
        String bucketBase,
        FilenameFormat format
) {}

