package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Attestazione di disponibilità dell'esito del recapito cartaceo. */
public final class AnalogFeedbackAvailabilityStatementTemplate {

    public static final String KEY = "ANALOG_FEEDBACK_AVAILABILITY_STATEMENT";

    private AnalogFeedbackAvailabilityStatementTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.INLINE_VALUE)
                .field("Data attestazione",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.INLINE_VALUE)
                .build();
    }
}
