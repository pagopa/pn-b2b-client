package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.JsonValidationResult;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.HashSet;
import java.util.Set;

public class NdjsonValidationStrategy implements IValidationStrategy {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.NDJSON;
    }

    @Override
    public ValidationResult validate(
            ProcessedFile file,
            IFileTokenSource required,
            IFileTokenSource optional
    ) {

        if (!(required instanceof IKeyedFileTokenSource req)) {
            throw new IllegalArgumentException("Required tokens must be keyed for NDJSON");
        }

        if (!(optional instanceof IKeyedFileTokenSource opt)) {
            throw new IllegalArgumentException("Optional tokens must be keyed for NDJSON");
        }

        Set<String> missingOptional = new HashSet<>();
        Set<String> missingRequired = new HashSet<>();

        boolean anyValidLine = FileUtils.validateNdjsonAnyMatch(
                file.content(),
                json -> true,
                json -> {

                    // se la riga è valida → interrompe la scansione
                    return validateLine(json, req, opt, missingOptional);
                }
        );

        if (!anyValidLine) {
            missingRequired.addAll(
                    req.entries().map(KeyedFileTokenEntry::key).toList()
            );
        }

        return new ValidationResult(missingRequired, missingOptional);
    }

    private JsonValidationResult validateLine(
            JsonNode json,
            IKeyedFileTokenSource required,
            IKeyedFileTokenSource optional,
            Set<String> missingOptional
    ) {
        // REQUIRED
        for (KeyedFileTokenEntry entry : required.entries().toList()) {
            JsonNode node = FileUtils.getNodeByPath(json, entry.key());
            if (!entry.fileToken().validate(node)) {
                return JsonValidationResult.invalid(
                        json.toString(),
                        "Missing or invalid required token at path: " + entry.key()
                );
            }
        }

        // OPTIONAL (solo sulla riga valida)
        for (KeyedFileTokenEntry entry : optional.entries().toList()) {
            JsonNode node = FileUtils.getNodeByPath(json, entry.key());
            if (!entry.fileToken().validate(node)) {
                missingOptional.add(entry.key());
            }
        }

        return JsonValidationResult.valid(json.toString());
    }


}
