package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Attestazione opponibile a terzi: notifica digitale. */
public final class PecDeliveryTemplate {

    public static final String KEY = "PEC_DELIVERY";

    private PecDeliveryTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Data consegna",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Ora consegna",
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
