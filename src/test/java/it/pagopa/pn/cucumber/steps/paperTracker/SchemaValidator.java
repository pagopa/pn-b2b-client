package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;

@Component
public class SchemaValidator {

    public void validate(JsonNode jsonNode, String schemaPath) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema JSON non trovato!");
            }

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaStream);

            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (!errors.isEmpty()) {
                throw new IllegalStateException("Oggetto non valido: " + errors);
            }

            System.out.println("Oggetto valido!");
        }
}
