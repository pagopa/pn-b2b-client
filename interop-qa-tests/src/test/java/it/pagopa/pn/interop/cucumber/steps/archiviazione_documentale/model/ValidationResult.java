package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.model;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors, String rawJson) {}

