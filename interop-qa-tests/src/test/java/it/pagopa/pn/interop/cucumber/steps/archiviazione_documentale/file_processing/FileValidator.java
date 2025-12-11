package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.validation_strategy.ConsumerDelegationApprovedEventValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType.CONSUMER_DELEGATION_APPROVED_EVENT;

public class FileValidator implements IFileValidator {

    private final Map<FileType, IFileValidator> strategies = new HashMap<>();

    public FileValidator() {
        strategies.put(CONSUMER_DELEGATION_APPROVED_EVENT, new ConsumerDelegationApprovedEventValidator());
    }

    @Override
    public void validate(ValidatorStrategySeed seed) throws IOException {
        IFileValidator validator = strategies.get(seed.getFile().getType());
        if (validator == null) throw new RuntimeException("Unknown file type " + seed.getFile().getType());

        validator.validate(seed);
    }
}
