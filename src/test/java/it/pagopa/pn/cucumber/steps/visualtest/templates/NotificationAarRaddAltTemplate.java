package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Avviso di Avvenuta Ricezione RADD Alternativo</em>
 * (Notification AAR RADD Alt).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class NotificationAarRaddAltTemplate {

    public static final String KEY = "NOTIFICATION_AAR_RADD_ALT";

    private NotificationAarRaddAltTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.onPage(1, FieldLocators.labelProximity("Identificativo Univoco Notifica:", 150f, 20f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.onPage(1, FieldLocators.labelProximity("Codice fiscale:", 150f, 20f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
