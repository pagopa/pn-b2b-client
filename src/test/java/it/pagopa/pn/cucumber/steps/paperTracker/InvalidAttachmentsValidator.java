package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidAttachmentsValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode invalidAttachments = actualNode.get("invalidAttachments");
        if (invalidAttachments != null && invalidAttachments.isArray()) {
            for (int i = 0; i < invalidAttachments.size(); i++) {
                String actualAttachment = invalidAttachments.get(i).asText();
                String expectedAttachment = expectedNode.get("invalidAttachments").get(i).asText();

                // valida invalidAttachments
                assertThat(actualAttachment).isEqualTo(expectedAttachment)
                        .as("invalidAttachments[" + i + "] non corrisponde all'expected value");
            }
        }
        else {
            throw new IllegalStateException("invalidAttachments non presente o non è un array!");
        }
    }
}
