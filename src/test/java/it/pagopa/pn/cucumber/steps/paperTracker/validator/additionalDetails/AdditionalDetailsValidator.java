package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Paper-Tracker: Interfaccia per la validazione dei campi presenti in additionalDetails.
 */
public interface AdditionalDetailsValidator {

    void validate(JsonNode actualNode, JsonNode expectedNode);

}