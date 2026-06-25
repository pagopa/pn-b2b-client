package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paper-Tracker: Validatore per i campi flat presenti in additionalDetails.
 * Gestisce campi come statusCode, statusTimestamp, recrn005aTimestamp, etc.
 *
 * Validazione:
 * - Campi timestamp: valida il formato senza confrontare il valore esatto
 * - Campi normali: confronta il valore atteso con quello effettivo
 */
@Slf4j
public class FlatAdditionalDetailsValidator implements AdditionalDetailsValidator {

    private static final String TIMESTAMP_PATTERN = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";

    @Override
    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        Iterator<String> fieldNames = expectedNode.fieldNames();

        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode expectedValue = expectedNode.get(fieldName);
            JsonNode actualValue = actualNode.get(fieldName);

            // Valida che il campo sia presente
            assertThat(actualValue)
                    .as("Campo mancante in additionalDetails: " + fieldName)
                    .isNotNull();

            // Differenzia la validazione in base al tipo di campo
            if (isTimestampField(fieldName)) {
                validateTimestampField(fieldName, actualValue);
            } else {
                validateStandardField(fieldName, actualValue, expectedValue);
            }
        }

        log.info("Validazione additionalDetails flat completata con successo");
    }

    /**
     * Valida un campo timestamp verificando il formato del timestamp
     * @param fieldName nome del campo
     * @param actual valore effettivo
     */
    private void validateTimestampField(String fieldName, JsonNode actual) {
        String timestamp = actual.asText();

        // Valida formato regex
        assertThat(timestamp)
                .as("Timestamp invalido per " + fieldName + ": non rispetta il formato atteso (YYYY-MM-DDTHH:MM:SSZ), ma trovato '" + timestamp + "'")
                .matches(TIMESTAMP_PATTERN);

        // Valida che sia parseable come OffsetDateTime
        try {
            OffsetDateTime.parse(timestamp);
            log.debug("Timestamp validato per {}: {}", fieldName, timestamp);
        } catch (Exception e) {
            throw new AssertionError("Timestamp non valido per " + fieldName + ": " + timestamp, e);
        }
    }

    /**
     * Valida un campo standard confrontando il valore atteso con quello effettivo
     * @param fieldName nome del campo
     * @param actual valore effettivo
     * @param expected valore atteso
     */
    private void validateStandardField(String fieldName, JsonNode actual, JsonNode expected) {
        String actualValue = actual.asText();
        String expectedValue = expected.asText();

        assertThat(actualValue)
                .as("Valore diverso per il campo " + fieldName + ": atteso '" + expectedValue + "', ma trovato '" + actualValue + "'")
                .isEqualTo(expectedValue);

        log.debug("Campo {} validato correttamente: {}", fieldName, actualValue);
    }

    /**
     * Determina se un campo è di tipo timestamp
     * @param fieldName nome del campo
     * @return true se il campo contiene "timestamp" nel nome
     */
    private boolean isTimestampField(String fieldName) {
        return fieldName.toLowerCase().contains("timestamp");
    }
}