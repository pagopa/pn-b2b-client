package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestazione di disponibilità feedback analogico</em>
 * (Analog Feedback Availability Statement).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class AnalogFeedbackAvailabilityStatementTemplate {

    public static final String KEY = "ANALOG_FEEDBACK_AVAILABILITY_STATEMENT";

    private AnalogFeedbackAvailabilityStatementTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(650f, FieldLocators.labelSameLine("IUN", 200f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data attestazione",
                        FieldLocators.aboveY(680f, FieldLocators.labelSameLine("in data", 150f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .build();
    }
}
