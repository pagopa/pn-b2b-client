package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

public class OcrDataResultPayloadValidator {

    public void validate(JsonNode actualNode, JsonNode expectedNode) {
        JsonNode actualOcrDataResultPayload = actualNode.get("ocrDataResultPayload");
        if (actualOcrDataResultPayload != null) {
            for (int i = 0; i < actualOcrDataResultPayload.size(); i++) {
                JsonNode expectedOcrDataResultPayload = actualNode.get("ocrDataResultPayload");

                // valida predictedRefinementType
                assertThat(expectedOcrDataResultPayload.get("predictedRefinementType")).as("predictedRefinementType non corrisponde all'expected value")
                        .isEqualTo(expectedOcrDataResultPayload.get("predictedRefinementType"));

                // valida validationType
                assertThat(expectedOcrDataResultPayload.get("validationType")).as("validationType non corrisponde all'expected value")
                        .isEqualTo(expectedOcrDataResultPayload.get("validationType"));

                // valida description
                assertThat(expectedOcrDataResultPayload.get("description")).as("description non corrisponde all'expected value")
                        .isEqualTo(expectedOcrDataResultPayload.get("description"));

                // valida validationStatus
                assertThat(expectedOcrDataResultPayload.get("validationStatus")).as("validationStatus non corrisponde all'expected value")
                        .isEqualTo(expectedOcrDataResultPayload.get("validationStatus"));
            }
        }
        else {
            throw new IllegalStateException("ocrDataResultPayload non presente o non è un array!");
        }
    }
}
