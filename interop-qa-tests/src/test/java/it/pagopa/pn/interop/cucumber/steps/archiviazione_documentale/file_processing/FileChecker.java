package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker.GzFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker.PdfFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.default_checker.Pm7GzFileChecker;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model.ArchivedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;

import java.util.HashMap;
import java.util.Map;

public class FileChecker implements IFileChecker{
    private final Map<FileType, IFileChecker> overrideStrategies = new HashMap<>();

    private final PdfFileChecker pdfFileChecker;
    private final GzFileChecker gzFileChecker;
    private final Pm7GzFileChecker pm7GzFileChecker;

    public FileChecker(TokenResolver tokenResolver) {
        this.pdfFileChecker = new PdfFileChecker(tokenResolver);
        this.gzFileChecker = new GzFileChecker(tokenResolver);
        this.pm7GzFileChecker = new Pm7GzFileChecker(gzFileChecker);
    }

    @Override
    public boolean hasToken(ArchivedFile file, IFileTokenSource fileTokenSource) {
        FileType fileType = file.getType();
        String fileExtension = fileType.getExtension();

        if(overrideStrategies.containsKey(fileType)){
            return overrideStrategies.get(fileType).hasToken(file, fileTokenSource);
        }

        return switch (fileExtension) {
            case "pdf" -> pdfFileChecker.hasToken(file, fileTokenSource);
            case "gz" -> gzFileChecker.hasToken(file, fileTokenSource);
            case "pm7" -> pm7GzFileChecker.hasToken(file, fileTokenSource);

            default -> throw new IllegalArgumentException("Invalid file type");
        };
    }
}
