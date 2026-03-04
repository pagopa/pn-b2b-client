package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface CustomConditionalValidator {

    List<String> validate(JsonNode trackingNode);
}
