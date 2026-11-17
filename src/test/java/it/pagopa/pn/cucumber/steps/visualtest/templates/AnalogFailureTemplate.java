package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Deposito dell'avviso di avvenuta ricezione per irreperibilità. */
public final class AnalogFailureTemplate {

    public static final String KEY = "ANALOG_FAILURE";

    private AnalogFailureTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Data deposito",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Ora deposito",
                        SendFieldLocators.time(),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePg(),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
