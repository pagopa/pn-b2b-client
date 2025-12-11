package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

public interface IFileValidator {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ValidatorStrategySeed {
        ArchivedFile file;
        TokenResolver tokenResolver;
    }

    void validate(ValidatorStrategySeed seed) throws IOException;
}
