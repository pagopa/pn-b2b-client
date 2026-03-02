package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.file_token;

import com.fasterxml.jackson.databind.JsonNode;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.utils.Validations;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class FileToken {

    private final String expectedValue;
    private final Predicate<Object> validator;

    public static FileToken ofValue(String expectedValue) {
        return new FileToken(Objects.requireNonNull(expectedValue, "expectedValue must not be null"), null);
    }

    public static FileToken ofValidator(Predicate<Object> validator) {
        return new FileToken(null, Objects.requireNonNull(validator, "validator must not be null"));
    }

    public boolean isValueToken() { return expectedValue != null; }

    public boolean isValidatorToken() { return validator != null; }

    public String expectedValue() { return expectedValue; }

    public boolean validate(Object obj) {
        if (obj == null) return false;

        if (isValueToken()) {
            String actualValue = (String)obj;
            return expectedValue.equals(actualValue);
        }

        // a questo punto deve essere per forza un validator token
        return validator.test(obj);
    }

    public static FileToken hasValidTimestamp() {
        return ofTypedValidator(
                String.class,
                Validations::isValidTimestamp
        );
    }

    private static <T> FileToken ofTypedValidator(
            Class<T> type,
            Predicate<T> typedValidator
    ) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(typedValidator, "typedValidator must not be null");

        return ofValidator(obj ->
                type.isInstance(obj) && typedValidator.test(type.cast(obj))
        );
    }

}
