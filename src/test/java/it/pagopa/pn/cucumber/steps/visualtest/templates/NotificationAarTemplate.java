package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Avviso di avvenuta ricezione: il codice QR cambia a ogni notifica. */
public final class NotificationAarTemplate {

    public static final String KEY = "NOTIFICATION_AAR";

    private NotificationAarTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(2)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.TO_LINE_END)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePf(),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .ignoreArea(0, 26f, 302f, 122f, 125f)
                .build();
    }
}
