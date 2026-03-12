package it.pagopa.pn.cucumber.steps.paperTracker.proxy;

import it.pagopa.pn.cucumber.steps.paperTracker.EventsTimestampValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.OcrAttachmentsFinalValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.OcrRequestValidator;
import it.pagopa.pn.cucumber.steps.paperTracker.SchemaValidator;
import org.springframework.stereotype.Component;

@Component
public class SchemaValidatorProxy {
    private static final OcrAttachmentsFinalValidator OCR_ATTACHMENTS_VALIDATOR = new OcrAttachmentsFinalValidator();
    private static final OcrRequestValidator OCR_REQUEST_VALIDATOR = new OcrRequestValidator();
    private static final EventsTimestampValidator EVENTS_TIMESTAMP_VALIDATOR = new EventsTimestampValidator();

    public SchemaValidator provide(String sequenceName) {
        return switch (sequenceName) {
            case "OK_AR_INVALID_DATETIME",
                 "OK_AR_TIMESTAMP_ERR",
                 "OK_RIR_INVALID_DATETIME",
                 "OK_RIR_TIMESTAMP_ERR" -> new SchemaValidator(OCR_ATTACHMENTS_VALIDATOR, OCR_REQUEST_VALIDATOR);
            default -> new SchemaValidator(OCR_ATTACHMENTS_VALIDATOR, OCR_REQUEST_VALIDATOR, EVENTS_TIMESTAMP_VALIDATOR);
        };
    }
}



