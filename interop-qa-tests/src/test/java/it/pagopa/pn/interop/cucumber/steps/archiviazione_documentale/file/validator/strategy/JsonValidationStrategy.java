package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.ContentType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.FileToken;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.entry.KeyedFileTokenEntry;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token.source.IKeyedFileTokenSource;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.processor.model.ProcessedFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.IValidationStrategy;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

public class JsonValidationStrategy implements IValidationStrategy {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.JSON;
    }

    @Override
    public boolean validate(ProcessedFile file, IFileTokenSource resolvedTokens) {

        if (!(resolvedTokens instanceof IKeyedFileTokenSource keyedSource)) {
            return true; // nessuna regola → valido
        }

        try {
            JsonNode root = MAPPER.readTree(file.content());

            for (KeyedFileTokenEntry entry : keyedSource.entries().toList()) {

                String path = entry.key();
                FileToken token = entry.fileToken();

                JsonNode node = FileUtils.getNodeByPath(root, path);

                if (!token.validate(node)) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Errore nella validazione JSON", e);
        }
    }
}
