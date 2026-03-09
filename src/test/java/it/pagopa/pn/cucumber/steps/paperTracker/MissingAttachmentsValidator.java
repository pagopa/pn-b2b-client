package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

public class MissingAttachmentsValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode missingAttachments = actualNode.get("missingAttachments");
        if (missingAttachments != null) {
            if (!missingAttachments.isArray()) {
                assertThat(missingAttachments.asText()).isEqualTo(expectedNode.get("missingAttachments").asText())
                        .as("missingAttachments non corrisponde all'expected value");
            } else {
                    for (int i = 0; i < missingAttachments.size(); i++) {
                        String actualAttachment = missingAttachments.get(i).asText();
                        String expectedAttachment = expectedNode.get("missingAttachments").get(i).asText();

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
