package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NdjsonValidationStrategy implements IValidationStrategy {

    private record NdjsonMatchResult(
            Set<String> found,
            Set<String> missing,
            JsonNode raw
    ) {}

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.NDJSON || contentType == ContentType.JSON;
    }

    @Override
    public ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional) {

        List<JsonNode> lines = readAllNdjsonLines(file);

        NdjsonMatchResult requiredResult =
                validateSource(lines, required);

        JsonNode targetRow = requiredResult.raw();

        NdjsonMatchResult optionalResult =
                validateSource(targetRow != null ? List.of(targetRow) : List.of(), optional);

        Set<String> missingRequired = new HashSet<>(requiredResult.missing());
        Set<String> missingOptional = new HashSet<>(optionalResult.missing());

        return new ValidationResult(missingRequired, missingOptional);
    }

    private NdjsonMatchResult validateSource(List<JsonNode> lines, IFileTokenSource source) {
        if (source instanceof IKeyedFileTokenSource keyed) {
            return validateKeyedSource(lines, keyed);
        }

        throw new IllegalArgumentException("Unsupported source type for NDJSON");
    }

    private NdjsonMatchResult validateKeyedSource(List<JsonNode> lines, IKeyedFileTokenSource source) {
        NdjsonMatchResult bestResult = null;

        for (JsonNode json : lines) {

            Set<String> found = new HashSet<>();
            Set<String> missing = new HashSet<>();

            for (KeyedFileTokenEntry entry : source.entries().toList()) {
                JsonNode node = FileUtils.getNodeByPath(json, entry.key());

                if (entry.fileToken().validate(node != null ? node.asText() : null)) {
                    found.add(entry.key());
                } else {
                    missing.add(entry.key());
                }
            }

            NdjsonMatchResult current =
                    new NdjsonMatchResult(found, missing, json);

            // scegli la riga "migliore"
            if (bestResult == null || current.found().size() > bestResult.found().size()) {
                bestResult = current;
            }

            // shortcut: riga perfetta
            if (missing.isEmpty()) {
                return current;
            }
        }

        // nessuna riga → tutto missing
        if (bestResult == null) {
            Set<String> allMissing = new HashSet<>();
            source.entries()
                    .map(KeyedFileTokenEntry::key)
                    .forEach(allMissing::add);

            return new NdjsonMatchResult(Set.of(), allMissing, null);
        }

        return bestResult;
    }

    private List<JsonNode> readAllNdjsonLines(ProcessedFile file) {
        return FileUtils.readNdjsonLines(file.content());
    }

}
