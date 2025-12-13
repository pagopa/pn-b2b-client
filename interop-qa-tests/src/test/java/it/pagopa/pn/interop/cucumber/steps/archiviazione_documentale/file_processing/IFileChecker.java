package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;

public interface IFileChecker {
    boolean hasToken(ArchivedFile file, IFileTokenSource fileTokenSource);
}
