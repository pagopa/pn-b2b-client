package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AffectedEventsValidator implements AdditionalDetailsValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode affectEventsNode = actualNode.get("affectedEvents");
        if (affectEventsNode != null && affectEventsNode.isArray()) {
            for (int i = 0; i < affectEventsNode.size(); i++) {
                JsonNode actualEvent = affectEventsNode.get(i);
                JsonNode expectedEvent = expectedNode.get("affectedEvents").get(i);

                // valida statusCode
                assertThat(actualEvent.get("statusCode").textValue()).isEqualTo(expectedEvent.get("statusCode").textValue());

                // valida timestamp
                assertThat(actualEvent.get("statusTimestamp").asText()).isNotNull().satisfies(OffsetDateTime::parse);

                // valida deliveryFailureCause se presente negli expectedEvent
                if (expectedEvent.has("deliveryFailureCause")) {
                    assertThat(actualEvent.get("deliveryFailureCause").textValue()).isEqualTo(expectedEvent.get("deliveryFailureCause").textValue());
                }
            }
        }
        else {
            throw new IllegalStateException("affectEvents non presente o non è un array!");
        }
    }
}
