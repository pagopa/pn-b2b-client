package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Dichiarazione di annullamento della notifica. */
public final class NotificationCancelledTemplate {

    public static final String KEY = "NOTIFICATION_CANCELLED";

    private NotificationCancelledTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Data annullamento",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Ora annullamento",
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
