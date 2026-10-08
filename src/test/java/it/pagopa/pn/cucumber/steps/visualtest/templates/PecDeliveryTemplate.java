package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import java.util.regex.Pattern;

import static it.pagopa.common.pdf.visualtest.FieldValidators.*;

/**
 * Template del documento <em>Avvenuta ricezione – notifica digitale (PEC)</em>
 * (PEC Delivery Workflow Legal Fact).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class PecDeliveryTemplate {

    public static final String KEY = "PEC_DELIVERY";

    private PecDeliveryTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.labelProximity("IUN", 300f, 15f),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Indirizzo PEC",
                        FieldLocators.regex(Pattern.compile(
                                "[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}")),
                        SendFieldValidators.indirizzoEmail(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data/ora risposta PEC",
                        FieldLocators.labelProximity("data", 250f, 20f),
                        and(SendFieldValidators.dataItaliana(), present()),
                        MaskStrategy.FULL_LINE)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.labelProximity("Codice fiscale", 250f, 15f),
                        present(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
