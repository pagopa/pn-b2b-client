package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;

public interface IFileProcessor {

    boolean supports(ContentType contentType);

    ProcessedFile process(ProcessedFile input);
}

