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
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Indirizzo PEC",
                        FieldLocators.aboveY(500f, FieldLocators.labelSameLine("Domicilio digitale", 300f)),
                        SendFieldValidators.indirizzoEmail(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data/ora risposta PEC",
                        FieldLocators.aboveY(450f, FieldLocators.labelSameLine("in data", 250f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.aboveY(550f, FieldLocators.labelSameLine("Codice Fiscale", 250f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
