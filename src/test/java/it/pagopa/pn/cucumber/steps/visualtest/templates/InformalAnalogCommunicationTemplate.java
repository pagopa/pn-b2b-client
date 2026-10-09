package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Comunicazione bonaria analogica / Avviso di cortesia cartaceo</em>
 * (Informal Analog Communication).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class InformalAnalogCommunicationTemplate {

    public static final String KEY = "INFORMAL_ANALOG_COMMUNICATION";

    private InformalAnalogCommunicationTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.onPage(0, FieldLocators.labelProximity("IUN:", 150f, 20f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.onPage(0, FieldLocators.labelProximity("Codice fiscale:", 150f, 20f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
