package it.pagopa.pn.cucumber.utils.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SchemaValidator {
    private List<CustomConditionalValidator> customValidators;

    public SchemaValidator(CustomConditionalValidator... validators) {
        this.customValidators = List.of(validators);
    }

    public void addValidators(CustomConditionalValidator... validators) {
        this.customValidators.addAll(List.of(validators));
    }

    public void validate(JsonNode jsonNode, String schemaPath) {
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema JSON non trovato!");
            }

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaStream);

            // Valida il JSON node
            Set<ValidationMessage> errors = new HashSet<>(schema.validate(jsonNode));

            for (CustomConditionalValidator customConditionalValidator : customValidators) {
                List<String> customErrors = customConditionalValidator.validate(jsonNode);
                customErrors.forEach(errorMsg -> errors.add(new ValidationMessage.Builder()
                        .message(errorMsg)
                        .instanceNode(jsonNode)
                        .schemaNode(null)
                        .build()));
            }

            if (!errors.isEmpty()) {
                throw new IllegalStateException("Oggetto non valido: " + errors);
            }
        }
}
