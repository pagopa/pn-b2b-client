package it.pagopa.pn.cucumber.steps.paperTracker;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
public class OcrAttachmentsFinalValidator implements CustomConditionalValidator {
    List<String> errors = new ArrayList<>();

    private static final String VALIDATION_MESSAGE = "If documentType in events.attachments is present in validationConfig.sendOcrAttachmentsFinalValidation, then validationFlow.ocrRequests must not be empty";

    /**
     * Valida la regola cross-field relativa agli OCR.
     *
     * <p>Se la lista {@code validationConfig.sendOcrAttachmentsFinalValidation} contiene dei
     * {@code documentType} e uno o più di questi {@code documentType} sono presenti in
     * {@code events.attachments.documentType}, allora {@code validationFlow.ocrRequests} deve
     * contenere un oggetto per ciascun {@code documentType} definito in
     * {@code validationConfig.sendOcrAttachmentsFinalValidation} che sia effettivamente arrivato.</p>
     *
     * @param trackingNode the tracking object to validate
     * @return a list of validation error messages (empty if valid)
     */
    public List<String> validate(JsonNode trackingNode) {

        // Get validationConfig.sendOcrAttachmentsFinalValidation list
        trackingNode = trackingNode.get("trackings").get(0);

        if (!trackingNode.get("state").textValue().equalsIgnoreCase("OK")) return errors;

        JsonNode validationConfig = trackingNode.get("validationConfig");
        if (validationConfig == null || validationConfig.get("sendOcrAttachmentsFinalValidation") == null) {
            return errors;
        }

        List<String> sendOcrAttachmentsFinalValidation = new ArrayList<>();
        JsonNode ocrAttachmentsNode = validationConfig.get("sendOcrAttachmentsFinalValidation");
        if (ocrAttachmentsNode.isArray()) {
            ocrAttachmentsNode.forEach(item -> sendOcrAttachmentsFinalValidation.add(item.asText()));
        }

        if (sendOcrAttachmentsFinalValidation.isEmpty()) {
            return errors;
        }

        // Check events.attachments for documentType matching the list
        JsonNode events = trackingNode.get("events");
        if (events == null || !events.isArray()) {
            return errors;
        }

        List<String> eventsDocumentType = new ArrayList<>();
        for (JsonNode event : events) {
            JsonNode attachments = event.get("attachments");
            if (attachments != null && attachments.isArray()) {
                for (JsonNode attachment : attachments) {
                    JsonNode documentTypeNode = attachment.get("documentType");
                    if (documentTypeNode != null) {
                        String documentType = documentTypeNode.asText();
                        if (sendOcrAttachmentsFinalValidation.contains(documentType)) {
                            eventsDocumentType.add(documentType);
                        }
                    }
                }
            }
        }

        verifyOcrRequestContainsDocumentType(trackingNode, eventsDocumentType);

        return errors;
    }

    private void verifyOcrRequestContainsDocumentType(JsonNode trackingNode, List<String> eventsDocumentType) {
        JsonNode validationFlow = trackingNode.get("validationFlow");
        if (validationFlow == null) {
            errors.add("validationFlow è vuota. " + VALIDATION_MESSAGE);
            return;
        }

        JsonNode ocrRequests = validationFlow.get("ocrRequests");
        if (ocrRequests == null || !ocrRequests.isArray() || ocrRequests.isEmpty()) {
            errors.add("validationFlow.ocrRequests è vuota o non presente. " + VALIDATION_MESSAGE);
            return;
        }

        List<String> ocrRequestDocumentTypes = StreamSupport.stream(ocrRequests.spliterator(), false)
                .map(node -> node.get("documentType").asText())
                .toList();
        if (!ocrRequestDocumentTypes.equals(eventsDocumentType)) {
            errors.add(String.format("ocrRequests non contiene tutti gli elementi desiderati, actual size: %d - expected: %d", ocrRequests.size(), eventsDocumentType.size()));
        }
    }
}
