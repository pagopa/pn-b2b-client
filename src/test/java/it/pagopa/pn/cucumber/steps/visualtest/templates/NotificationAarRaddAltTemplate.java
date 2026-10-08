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
                        FieldLocators.labelProximity("IUN", 300f, 15f),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale mittente",
                        FieldLocators.labelProximity("C.F.", 200f, 15f),
                        present(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.labelProximity("Codice fiscale", 250f, 15f),
                        present(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data emissione",
                        FieldLocators.labelProximity("data", 200f, 15f),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
