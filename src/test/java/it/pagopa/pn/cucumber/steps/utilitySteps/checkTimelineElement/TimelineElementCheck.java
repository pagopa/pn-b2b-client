package it.pagopa.pn.cucumber.steps.utilitySteps.checkTimelineElement;

/**
 * Enum di tutte le tipologie di check che si possono effettuare su un elemento di timeline nelle classi
 * che implementano l'interfaccia {@link it.pagopa.pn.cucumber.steps.pa.b2bVersions.B2bStepsInterface}.
 * Se si necessita di creare una nuova tipologia di check con parametri, sarà sufficiente aggiungere
 * i parametri necessari all'interno di {@link TimelineElementCheckFilters}
 * e inserire qui la nuova tipologia di check
 */
public enum TimelineElementCheck {

    CHECK_RESPONSE_STATUS,
    CHECK_MUNICIPALITY_AND_FOREIGN_STATE,
    CHECK_PAYMENT_FROM_RECIPIENT_INDEX,
    CHECK_ONLY_PAYMENTS_PAGOPA,
    CHECK_FAILURE_CAUSE,
    CHECK_NOTIFICATION_DATE_DELAY,
    CHECK_SCHEDULING_DATE_DELAY,
    CHECK_ATTACHMENTS,
    CHECK_DELIVERY_FAILURE_CAUSE,
    CHECK_SEND_REQUEST_ID,
    CHECK_SERVICE_LEVEL,
    CHECK_DIGITAL_ADDRESS,
    CHECK_PHYSICAL_ADDRESS,
    CHECK_NOT_REFINED_RECIPIENT_INDEX,
    CHECK_SCHEDULING_DATE_RISPETTO_A_EVENTO,
    CHECK_NOTIFICATION_COST_FOR_USER,
    CHECK_NUMBER_OF_PAGES_AAR,
    CHECK_FIELD_MATCHES_REGEX,
    CHECK_ATTESTAZIONI_OPPONIBILI,
    CHECK_INVALIDATED_ELEMENTS_TIMESTAMP
}
