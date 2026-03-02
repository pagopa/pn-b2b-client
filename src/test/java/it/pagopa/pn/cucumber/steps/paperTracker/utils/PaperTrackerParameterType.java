package it.pagopa.pn.cucumber.steps.paperTracker.utils;

import io.cucumber.java.ParameterType;
import it.pagopa.pn.cucumber.steps.utilitySteps.PaperTrackerErrorCategory;

public class PaperTrackerParameterType {

    @ParameterType("TRACKING_ID_NOT_FOUND|RENDICONTAZIONE_SCARTATA|DATE_ERROR|STATUS_CODE_ERROR|LAST_EVENT_EXTRACTION_ERROR|EMPTY_STRING" +
            "|REGISTERED_LETTER_CODE_ERROR|DELIVERY_FAILURE_CAUSE_ERROR|ATTACHMENTS_ERROR|MAX_RETRY_REACHED_ERROR|OCR_VALIDATION|INCONSISTENT_STATE" +
            "|DUPLICATED_EVENT|NOT_RETRYABLE_EVENT_ERROR")
    public static PaperTrackerErrorCategory paperTrackerErrorCategory(String errorCategory) {
        return PaperTrackerErrorCategory.valueOf(errorCategory.toUpperCase());
    }
}
