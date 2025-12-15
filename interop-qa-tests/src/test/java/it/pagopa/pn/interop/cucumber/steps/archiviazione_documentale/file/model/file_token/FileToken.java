package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class FileToken {

    private final String expectedValue;
    private final Predicate<JsonNode> validator;

    public static FileToken ofValue(String expectedValue) {
        return new FileToken(expectedValue, null);
    }

    public static FileToken ofValidator(Predicate<JsonNode> validator) {
        return new FileToken(null, validator);
    }

    public boolean isValueToken() {
        return expectedValue != null;
    }

    public boolean isValidatorToken() {
        return validator != null;
    }

    /**
     * Valore atteso (solo per token di tipo value).
     * Ritorna null se è un validator token.
     */
    public String expectedValue() {
        return expectedValue;
    }

    public boolean validate(JsonNode node) {
        if (node == null) return false;

        if (isValueToken()) {
            return expectedValue.equals(node.asText());
        }

        return validator.test(node);
    }

    public String describe() {
        return isValueToken() ? expectedValue : "<custom-validator>";
    }
}
