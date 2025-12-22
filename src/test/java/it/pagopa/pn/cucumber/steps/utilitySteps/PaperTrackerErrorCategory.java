package it.pagopa.pn.cucumber.steps.utilitySteps;

import lombok.Getter;

@Getter
public enum PaperTrackerErrorCategory {
    TRACKING_ID_NOT_FOUND("TrackingId non trovato"),
    NOT_RETRYABLE_EVENT_ERROR("Evento not retryable ricevuto"),
    RENDICONTAZIONE_SCARTATA("Rendicontazione scartata"),
    DATE_ERROR("Errore nella validazione delle date della sequenza."),
    STATUS_CODE_ERROR("Errore nella validazione della presenza degli elementi della sequenza."),
    LAST_EVENT_EXTRACTION_ERROR("Errore nell'estrazione della sequenza dall'ultimo evento."),
    REGISTERED_LETTER_CODE_ERROR("Errore nella validazione del registered letter code"),
    DELIVERY_FAILURE_CAUSE_ERROR("Errore nella validazione del delivery Failure Cause"),
    ATTACHMENTS_ERROR("Errore nella validazione degli allegati della sequenza"),
    MAX_RETRY_REACHED_ERROR("Numero massimo di retry raggiunto"),
    OCR_VALIDATION("Errore nella validazione OCR"),
    DUPLICATED_EVENT("Errore nella validazione della presenza di eventi duplicati"),
    INCONSISTENT_STATE("invalid AWAITING_REFINEMENT state for stock 890");

    private final String value;

    PaperTrackerErrorCategory(String value) {
        this.value = value;
    }
}
