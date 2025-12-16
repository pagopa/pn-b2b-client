package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PdfValidationStrategy implements IValidationStrategy {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.PDF;
    }

    @Override
    public ValidationResult validate(
            ProcessedFile file,
            IFileTokenSource required,
            IFileTokenSource optional
    ) {

        // per i PDF consideriamo solo i value token
        List<String> requiredWords = required.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        List<String> optionalWords = optional.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        Set<String> missingRequired = new HashSet<>();
        Set<String> missingOptional = new HashSet<>();

        // nessuna parola richiesta → valido
        if (requiredWords.isEmpty() && optionalWords.isEmpty()) {
            return new ValidationResult(Set.of(), Set.of());
        }

        // UNA SOLA LETTURA DEL PDF
        // (FileUtils deve leggere lo stream una sola volta)
        Set<String> foundWords = FileUtils.pdfExtractWords(file.content());

        // REQUIRED
        for (String word : requiredWords) {
            if (!foundWords.contains(word)) {
                missingRequired.add(word);
            }
        }

        // OPTIONAL
        for (String word : optionalWords) {
            if (!foundWords.contains(word)) {
                missingOptional.add(word);
            }
        }

        return new ValidationResult(missingRequired, missingOptional);
    }
}
