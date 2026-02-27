package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model;

import lombok.Getter;

import java.util.List;

public class JsonValidationResult {

    @Getter
    private final boolean valid;
    private final String rawJson;
    private final List<String> errors;

    private JsonValidationResult(boolean valid, String rawJson, List<String> errors) {
        this.valid = valid;
        this.rawJson = rawJson;
        this.errors = errors;
    }

    public static JsonValidationResult valid(String rawJson) {
        return new JsonValidationResult(true, rawJson, List.of());
    }

    public static JsonValidationResult invalid(String rawJson, String error) {
        return new JsonValidationResult(false, rawJson, List.of(error));
    }

    public String rawJson() {
        return rawJson;
    }

    public List<String> errors() {
        return errors;
    }
}
