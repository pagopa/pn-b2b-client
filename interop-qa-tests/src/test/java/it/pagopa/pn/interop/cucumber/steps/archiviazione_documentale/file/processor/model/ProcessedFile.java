package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;

import java.io.InputStream;

public record ProcessedFile(InputStream content, ContentType contentType) {
}

