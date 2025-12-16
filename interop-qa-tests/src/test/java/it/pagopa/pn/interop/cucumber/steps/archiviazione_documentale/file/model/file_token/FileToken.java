package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.utils.Validations;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class FileToken {

    private final String expectedValue;
    private final Predicate<JsonNode> validator;

    public static FileToken ofValue(String expectedValue) {
        return new FileToken(Objects.requireNonNull(expectedValue, "expectedValue must not be null"), null);
    }

    public static FileToken ofValidator(Predicate<JsonNode> validator) {
        return new FileToken(null, Objects.requireNonNull(validator, "validator must not be null"));
    }

    public boolean isValueToken() { return expectedValue != null; }

    public boolean isValidatorToken() { return validator != null; }

    public String expectedValue() { return expectedValue; }

    public boolean validate(JsonNode node) {
        if (node == null) return false;

        if (isValueToken()) {
            return expectedValue.equals(node.asText());
        }

        // a questo punto deve essere per forza un validator token
        return validator.test(node);
    }

    public String describe() {
        if (isValueToken()) return expectedValue;
        return "<custom-validator>";
    }

    public static FileToken hasValidTimestamp() {
        return ofStringValidator(Validations::isValidTimestamp);
    }

    private static FileToken ofStringValidator(Predicate<String> stringValidator) {
        Objects.requireNonNull(stringValidator, "stringValidator must not be null");
        return ofValidator(node ->
                node != null
                        && node.isTextual()
                        && stringValidator.test(node.asText())
        );
    }
}
