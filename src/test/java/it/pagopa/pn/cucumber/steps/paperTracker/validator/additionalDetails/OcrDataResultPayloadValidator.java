package it.pagopa.pn.cucumber.steps.paperTracker.validator.additionalDetails;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Paper-Tracker: Validatore per il campo ocrDataResultPayload presente in additionalDetails.
 * Verifica che il campo ocrDataResultPayload sia presente e, se è un array, confronta ogni elemento con l'expected value.
 */
public class OcrDataResultPayloadValidator implements AdditionalDetailsValidator {

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
