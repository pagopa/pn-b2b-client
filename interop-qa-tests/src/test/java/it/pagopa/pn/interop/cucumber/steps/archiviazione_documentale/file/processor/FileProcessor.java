package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.FileCandidate;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.process.GzipFileProcessor;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.process.P7mFileProcessor;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.process.ZipFileProcessor;

import java.util.List;

public class FileProcessor {
    private final List<IFileProcessor> processors =  List.of(
            new GzipFileProcessor(),
            new P7mFileProcessor(),
            new ZipFileProcessor()
    );

    public ProcessedFile normalize(FileCandidate candidate) {
        ProcessedFile current = new ProcessedFile(candidate.content(), candidate.contentType());
        boolean processed;

        do {
            processed = false;

            for (IFileProcessor processor : processors) {
                if (processor.supports(current.contentType())) {
                    current = processor.process(current);
                    processed = true;
                    break;
                }
            }

        } while (processed);

        return current;
    }
}

