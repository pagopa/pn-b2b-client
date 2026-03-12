package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OcrRequestValidator implements CustomConditionalValidator {
    List<String> errors = new ArrayList<>();

    /**
     * Valida la regola cross-field relativa agli OCR.
     *
     * <p>Se la lista {@code validationFlow.ocrRequests} contiene dei nodi
     * con {@code responseStatus} uguale a "OK", allora ciascuno di questi nodi deve contenere i campi
     * obbligatori {@code uri}, {@code responseTimestamp}, {@code requestTimestamp}, {@code attachmentEventId}, {@code finalEventId} e {@code documentType}.</p>
     *
     * @param trackingNode the tracking object da validare
     * @return una lista di errori, vuota se la validazione è passata senza problemi
     */
    public List<String> validate(JsonNode trackingNode) {
        trackingNode = trackingNode.get("trackings").get(0);

        JsonNode validationFlow = trackingNode.get("validationFlow");
        if (validationFlow == null || validationFlow.get("ocrRequests") == null) {
            return errors;
        }

        JsonNode ocrRequests = validationFlow.get("ocrRequests");
        if (!ocrRequests.isArray()) {
            errors.add("validationFlow.ocrRequests non è un array.");
            return errors;
        } else {
            for (JsonNode ocrRequest : ocrRequests) {
                validateOcrRequest(ocrRequest);
            }
        }
        return errors;
    }

    private void validateOcrRequest(JsonNode ocrRequest) {
        JsonNode responseStatus = ocrRequest.get("responseStatus");
        if (responseStatus != null && responseStatus.asText().equals("OK")) {
            JsonNode uri = ocrRequest.get("uri");
            JsonNode responseTimestamp = ocrRequest.get("responseTimestamp");
            JsonNode requestTimestamp = ocrRequest.get("requestTimestamp");
            JsonNode attachmentEventId = ocrRequest.get("attachmentEventId");
            JsonNode finalEventId = ocrRequest.get("finalEventId");
            JsonNode documentType = ocrRequest.get("documentType");
            if (uri == null || responseTimestamp == null || requestTimestamp == null || attachmentEventId == null || finalEventId == null || documentType == null) {
                errors.add("Un ocrRequest con responseStatus OK è privo di uno o più campi obbligatori (uri, responseTimestamp, requestTimestamp, attachmentEventId, finalEventId, documentType).");
            }
        }
    }
}
