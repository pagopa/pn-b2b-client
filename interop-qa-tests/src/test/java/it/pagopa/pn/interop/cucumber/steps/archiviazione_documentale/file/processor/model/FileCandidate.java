package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;

import java.io.InputStream;

public record FileCandidate(
        InputStream content,
        String filename,
        ContentType contentType
) {}
