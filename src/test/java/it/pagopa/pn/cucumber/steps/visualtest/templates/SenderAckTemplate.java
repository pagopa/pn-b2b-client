package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Attestazione opponibile a terzi: notifica presa in carico. */
public final class SenderAckTemplate {

    public static final String KEY = "SENDER_ACK";

    private SenderAckTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Data presa in carico",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Ora presa in carico",
                        SendFieldLocators.time(),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePf(),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
