package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import it.pagopa.pn.interop.cucumber.utility.model.PdfWordMatchResult;

import java.util.List;
import java.util.Set;

public class PdfValidationStrategy implements IValidationStrategy {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.PDF;
    }

    @Override
    public ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional) {

        List<String> requiredWords = required.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        List<String> optionalWords = optional.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        Set<String> allWords = new java.util.HashSet<>();
        allWords.addAll(requiredWords);
        allWords.addAll(optionalWords);

        if (allWords.isEmpty()) {
            return new ValidationResult(Set.of(), Set.of());
        }

        PdfWordMatchResult match =
                FileUtils.pdfMatchWords(file.content(), allWords.stream().toList());

        Set<String> missingRequired = requiredWords.stream()
                .filter(w -> match.getMissing().contains(w))
                .collect(java.util.stream.Collectors.toSet());

        Set<String> missingOptional = optionalWords.stream()
                .filter(w -> match.getMissing().contains(w))
                .collect(java.util.stream.Collectors.toSet());

        return new ValidationResult(missingRequired, missingOptional);
    }

}
