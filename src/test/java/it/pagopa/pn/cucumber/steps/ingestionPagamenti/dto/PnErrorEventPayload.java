package it.pagopa.pn.cucumber.steps.ingestionPagamenti.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Body dei messaggi scritti nella DLQ dei pagamenti per gli eventi malformati.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PnErrorEventPayload(String originalMessage, String description, String timestamp) {
    public static final String MALFORMED_DESCRIPTION = "Error: message malformed.";
}
