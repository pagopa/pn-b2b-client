package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paper-Tracker: Validatore per il campo missingStatusCodes presente in additionalDetails.
 * Verifica che il campo missingStatusCodes sia presente e, se è un array, confronta ogni elemento con l'expected value.
 */
public class MissingStatusCodeValidator implements AdditionalDetailsValidator{

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode missingStatusCodes = actualNode.get("missingStatusCodes");
        if (missingStatusCodes != null) {
            for (int i = 0; i < missingStatusCodes.size(); i++) {
                String actualStatusCode = missingStatusCodes.get(i).asText();
                String expectedStatusCode = expectedNode.get("missingStatusCodes").get(i).asText();

                // valida missingStatusCodes
                assertThat(actualStatusCode).isEqualTo(expectedStatusCode)
                        .as("missingStatusCodes[" + i + "] non corrisponde all'expected value");
            }
        }
        else {
            throw new IllegalStateException("missingStatusCodes non presente o non è un array!");
        }
    }
}
