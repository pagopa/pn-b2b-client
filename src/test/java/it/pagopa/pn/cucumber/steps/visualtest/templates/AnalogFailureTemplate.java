package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestato di mancato recapito</em>
 * (Analog Delivery Workflow Failure Legal Fact).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class AnalogFailureTemplate {

    public static final String KEY = "ANALOG_FAILURE";

    private AnalogFailureTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(680f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.aboveY(650f, FieldLocators.labelSameLine("CF:", 200f)),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data fine workflow",
                        FieldLocators.aboveY(620f, FieldLocators.labelSameLine("in data", 250f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .field(
                        "Ora fine workflow",
                        FieldLocators.aboveY(620f, FieldLocators.labelSameLine("alle ore", 150f)),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
