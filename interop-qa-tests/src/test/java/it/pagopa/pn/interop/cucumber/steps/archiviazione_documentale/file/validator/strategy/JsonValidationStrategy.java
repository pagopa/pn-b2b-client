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
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model.ValidationResult;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;

import java.util.HashSet;
import java.util.Set;

public class JsonValidationStrategy implements IValidationStrategy {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean supports(ContentType contentType) {
        return contentType == ContentType.JSON;
    }

    @Override
    public ValidationResult validate(ProcessedFile file, IFileTokenSource required, IFileTokenSource optional) {

        // JSON → devono essere keyed
        if (!(required instanceof IKeyedFileTokenSource req)) {
            throw new IllegalArgumentException("Required tokens must be keyed for JSON validation");
        }
        if (!(optional instanceof IKeyedFileTokenSource opt)) {
            throw new IllegalArgumentException("Optional tokens must be keyed for JSON validation");
        }

        Set<String> missingRequired = new HashSet<>();
        Set<String> missingOptional = new HashSet<>();

        try {
            // UNA SOLA LETTURA DELLO STREAM
            JsonNode root = MAPPER.readTree(file.content());

            // REQUIRED
            for (KeyedFileTokenEntry entry : req.entries().toList()) {
                String path = entry.key();
                FileToken token = entry.fileToken();

                JsonNode node = FileUtils.getNodeByPath(root, path);
                if (!token.validate(node)) {
                    missingRequired.add(path);
                }
            }

            // OPTIONAL
            for (KeyedFileTokenEntry entry : opt.entries().toList()) {
                String path = entry.key();
                FileToken token = entry.fileToken();

                JsonNode node = FileUtils.getNodeByPath(root, path);
                if (!token.validate(node)) {
                    missingOptional.add(path);
                }
            }

            return new ValidationResult(missingRequired, missingOptional);

        } catch (Exception e) {
            throw new RuntimeException("Errore nella validazione JSON", e);
        }
    }
}
