package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Comunicazione bonaria cartacea. */
public final class InformalAnalogCommunicationTemplate {

    public static final String KEY = "INFORMAL_ANALOG_COMMUNICATION";

    private InformalAnalogCommunicationTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePf(),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
