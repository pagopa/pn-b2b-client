package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestato di mancato recapito per decorrenza termini</em>
 * (Analog Delivery Workflow Timeout Legal Fact).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class AnalogDeliveryWorkflowTimeoutLegalFactTemplate {

    public static final String KEY = "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT";

    private AnalogDeliveryWorkflowTimeoutLegalFactTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data decorrenza termini",
                        FieldLocators.aboveY(500f, FieldLocators.labelSameLine("alla data del", 250f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .build();
    }
}
