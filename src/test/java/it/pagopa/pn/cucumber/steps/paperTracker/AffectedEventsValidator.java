package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AffectedEventsValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode affectEventsNode = actualNode.get("affectedEvents");
        if (affectEventsNode != null && affectEventsNode.isArray()) {
            for (int i = 0; i < affectEventsNode.size(); i++) {
                JsonNode actualEvent = affectEventsNode.get(i);
                JsonNode expectedEvent = expectedNode.get("affectedEvents").get(i);

                // Validate eventId
                assertThat(actualEvent.get("statusCode").textValue()).isEqualTo(expectedEvent.get("statusCode").textValue());

                // Validate timestamp
                assertThat(actualEvent.get("statusTimestamp").asText()).isNotNull().satisfies(OffsetDateTime::parse);
            }
        }
        else {
            throw new IllegalStateException("affectEvents non presente o non è un array!");
        }
    }
}
