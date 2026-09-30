package it.pagopa.pn.cucumber.utils.validator;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Interfaccia per la validazione condizionale dei campi in base al contenuto delle OpenAPI response.
 * Implementata da classi specifiche che eseguono validazioni personalizzate.
 */
public interface CustomConditionalValidator {

    List<String> validate(JsonNode trackingNode);
}
