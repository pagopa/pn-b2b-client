package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;

import java.util.List;

public record FileInfoDefinition(
        InteropFile type,
        IFileTokenSource required,
        IFileTokenSource optional,
        List<LocationDefinition> locations
) {}

