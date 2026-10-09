package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestato di avvenuto accesso</em>
 * (Notification Viewed Legal Fact).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class NotificationViewedTemplate {

    public static final String KEY = "NOTIFICATION_VIEWED";

    private NotificationViewedTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data accesso",
                        FieldLocators.aboveY(500f, FieldLocators.labelSameLine("in data", 250f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.aboveY(550f, FieldLocators.labelSameLine("Codice Fiscale", 250f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
