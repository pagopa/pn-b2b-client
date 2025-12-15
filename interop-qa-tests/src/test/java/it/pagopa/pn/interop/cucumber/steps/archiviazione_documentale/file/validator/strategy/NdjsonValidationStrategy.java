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
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

public class NdjsonValidationStrategy implements IValidationStrategy {

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.NDJSON;
    }

    @Override
    public boolean validate(ProcessedFile file, IFileTokenSource resolvedTokens) {

        if (!(resolvedTokens instanceof IKeyedFileTokenSource keyedSource)) {
            return true; // nessuna regola → valido
        }

        return FileUtils.validateNdjsonAnyMatch(
                file.content(),
                json -> true, // tutte le righe sono candidate
                json -> validateJson(json, keyedSource)
        );
    }

    private JsonValidationResult validateJson(JsonNode json, IKeyedFileTokenSource keyedSource) {

        for (KeyedFileTokenEntry entry : keyedSource.entries().toList()) {

            String jsonPath = entry.key();
            FileToken token = entry.fileToken();

            JsonNode node = FileUtils.getNodeByPath(json, jsonPath);

            if (!token.validate(node)) {
                return JsonValidationResult.invalid(
                        json.toString(),
                        "Validazione fallita su path: " + jsonPath
                );
            }
        }

        return JsonValidationResult.valid(json.toString());
    }
}
