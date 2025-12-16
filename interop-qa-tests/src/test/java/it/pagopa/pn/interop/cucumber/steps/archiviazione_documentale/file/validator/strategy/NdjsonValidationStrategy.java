package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
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
    public ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional) {

        if (!(required instanceof IKeyedFileTokenSource req)) {
            throw new IllegalArgumentException("Required tokens must be keyed for NDJSON");
        }

        if (!(optional instanceof IKeyedFileTokenSource opt)) {
            throw new IllegalArgumentException("Optional tokens must be keyed for NDJSON");
        }

        Set<String> missingRequired = new HashSet<>();
        Set<String> missingOptional = new HashSet<>();

        boolean anyValidLine = FileUtils.validateNdjsonAnyMatch(
                file.content(),
                json -> true, // tutte le righe candidate
                json -> validateLine(json, req, opt, missingRequired, missingOptional)
        );

        // se non c'è nemmeno una riga valida, i required sono tutti mancanti
        if (!anyValidLine) {
            missingRequired.addAll(req.entries().map(KeyedFileTokenEntry::key).toList());
        }

        return new ValidationResult(missingRequired, missingOptional);
    }

    private JsonValidationResult validateLine(
            JsonNode json,
            IKeyedFileTokenSource required,
            IKeyedFileTokenSource optional,
            Set<String> missingRequired,
            Set<String> missingOptional
    ) {

        // REQUIRED
        for (KeyedFileTokenEntry entry : required.entries().toList()) {
            String path = entry.key();
            FileToken token = entry.fileToken();

            JsonNode node = FileUtils.getNodeByPath(json, path);
            if (!token.validate(node)) {
                missingRequired.add(path);
                return JsonValidationResult.invalid(
                        json.toString(),
                        "Missing or invalid required token at path: " + path
                );
            }
        }

        // OPTIONAL
        for (KeyedFileTokenEntry entry : optional.entries().toList()) {
            String path = entry.key();
            FileToken token = entry.fileToken();

            JsonNode node = FileUtils.getNodeByPath(json, path);
            if (!token.validate(node)) {
                missingOptional.add(path);
            }
        }

        return JsonValidationResult.valid(json.toString());
    }

}
