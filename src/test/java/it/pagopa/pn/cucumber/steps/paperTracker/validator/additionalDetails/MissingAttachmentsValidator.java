package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paper-Tracker: Validatore per il campo missingAttachments presente in additionalDetails.
 * Verifica che il campo missingAttachments sia presente e, se è un array, confronta ogni elemento con l'expected value.
 */
public class MissingAttachmentsValidator implements AdditionalDetailsValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode missingAttachments = actualNode.get("missingAttachments");
        if (missingAttachments != null) {
            if (!missingAttachments.isArray()) {
                assertThat(missingAttachments.asText()).isEqualTo(expectedNode.get("missingAttachments").asText())
                        .as("missingAttachments non corrisponde all'expected value");
            } else {
                    for (int i = 0; i < missingAttachments.size(); i++) {
                        String actualAttachment = missingAttachments.get(i).asText();
                        String expectedAttachment = missingAttachments.get(i).asText();

                        // valida missingAttachments
                        assertThat(actualAttachment).isEqualTo(expectedAttachment)
                                .as("missingAttachments[" + i + "] non corrisponde all'expected value");
                    }
                }
        }
        else {
            throw new IllegalStateException("missingAttachments non presente!");
        }
    }
}
