package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IListedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PdfValidationStrategy implements IValidationStrategy {

    private record PdfWordMatchResult(
            Set<String> found,
            Set<String> missing
    ) { }

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.PDF;
    }

    @Override
    public ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional) {
        String pdfText = FileUtils.extractPdfText(file.content());

        PdfWordMatchResult requiredResult =
               validateSource(pdfText, required);

        PdfWordMatchResult optionalResult =
                validateSource(pdfText, optional);

        Set<String> missingRequired = new HashSet<>(requiredResult.missing());
        Set<String> missingOptional = new HashSet<>(optionalResult.missing());

        return new ValidationResult(missingRequired, missingOptional);

    }

    private PdfWordMatchResult validateSource(String pdfText, IFileTokenSource source) {
        if(source instanceof IListedFileTokenSource)
            return validateListedSource(pdfText, (IListedFileTokenSource) source);
        else if(source instanceof IKeyedFileTokenSource)
            return validateKeyedSource(pdfText, (IKeyedFileTokenSource) source);

        throw  new IllegalArgumentException("Unsupported source type");
    }

    private PdfWordMatchResult validateListedSource(String pdfText, IListedFileTokenSource source) {

        List<String> words = source.tokens()
                .filter(FileToken::isValueToken)
                .map(FileToken::expectedValue)
                .toList();

        if (words.isEmpty()) {
            return new PdfWordMatchResult(Set.of(), Set.of());
        }

        return matchWords(pdfText, words);
    }

    private PdfWordMatchResult validateKeyedSource(String pdfText, IKeyedFileTokenSource source) {

        Set<String> found = new HashSet<>();
        Set<String> missing = new HashSet<>();

        for (KeyedFileTokenEntry entry : source.entries().toList()) {
            FileToken token = entry.fileToken();

            String label = entry.key();
            String value = extractValueByLabel(pdfText, label);

            if (token.validate(value)) {
                found.add(label);
            } else {
                missing.add(label);
            }
        }

        return new PdfWordMatchResult(found, missing);
    }

    private String extractValueByLabel(String pdfText, String label) {
        if (pdfText == null || label == null) {
            return null;
        }

        // Escape dell'etichetta per regex
        String escapedLabel = java.util.regex.Pattern.quote(label);

        // Supporta:
        // "Label: valore"
        // "Label valore"
        // "Label    valore"
        String regex = escapedLabel + "\\s*[:]?\\s*(.+)";

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile(regex).matcher(pdfText);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    private PdfWordMatchResult matchWords(String pdfText, List<String> words) {
        if (pdfText == null || words == null || words.isEmpty()) {
            return new PdfWordMatchResult(Set.of(), Set.of());
        }

        Set<String> found = new HashSet<>();
        Set<String> missing = new HashSet<>();

        for (String word : words) {
            if (word == null || word.isBlank()) {
                continue;
            }

            // match semplice, case-sensitive (come pdfMatchWords)
            if (pdfText.contains(word)) {
                found.add(word);
            } else {
                missing.add(word);
            }
        }

        return new PdfWordMatchResult(found, missing);
    }
}
