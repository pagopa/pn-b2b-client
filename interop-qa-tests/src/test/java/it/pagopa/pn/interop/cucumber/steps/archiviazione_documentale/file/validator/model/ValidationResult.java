package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.model;

import java.util.Set;

public record ValidationResult(
        Set<String> missingRequired,
        Set<String> missingOptional
) {

    /**
     * Il file è valido se NON mancano token required.
     */
    public boolean isValid() {
        return missingRequired.isEmpty();
    }

    /**
     * Tutti i token required sono presenti.
     */
    public boolean hasAllRequired() {
        return missingRequired.isEmpty();
    }

    /**
     * Tutti i token optional sono presenti.
     */
    public boolean hasAllOptional() {
        return missingOptional.isEmpty();
    }

    /**
     * Il file è completamente valido (required + optional).
     */
    public boolean isFullyValid() {
        return hasAllRequired() && hasAllOptional();
    }
}
