package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestazione di annullamento notifica</em>
 * (Notification Cancelled Legal Fact).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class NotificationCancelledTemplate {

    public static final String KEY = "NOTIFICATION_CANCELLED";

    private NotificationCancelledTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(650f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data annullamento",
                        FieldLocators.aboveY(550f, FieldLocators.labelSameLine("in data", 200f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("Codice fiscale", 250f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
