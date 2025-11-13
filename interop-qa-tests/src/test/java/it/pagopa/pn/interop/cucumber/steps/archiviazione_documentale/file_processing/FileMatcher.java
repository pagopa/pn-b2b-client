package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMatcher {
    private final Map<FileType, FileMatchingStrategy> strategies = new HashMap<>();
    private final SharedStepsContext sharedStepsContext;

    public FileMatcher(SharedStepsContext sharedStepsContext) {
        this.sharedStepsContext = sharedStepsContext;
        strategies.put(null, null);
    }

    public boolean match(FileMatchingStrategy.MatchingStrategySeed seed) throws IOException {
        FileMatchingStrategy strategy = strategies.get(seed.getFileType());
        if(strategy == null) throw new RuntimeException("Unknown file type " + seed.getFileType());

        return strategy.match(seed);
    }
}

