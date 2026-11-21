package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Attestazione di mancata ricezione degli esiti dal recapito estero. */
public final class AnalogDeliveryWorkflowTimeoutLegalFactTemplate {

    public static final String KEY = "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT";

    private AnalogDeliveryWorkflowTimeoutLegalFactTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("IUN",
                        SendFieldLocators.iun(),
                        SendFieldValidators.iun(),
                        MaskStrategy.INLINE_VALUE)
                .field("Data decorrenza termini",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.INLINE_VALUE)
                .field("Ora decorrenza termini",
                        SendFieldLocators.time(),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.INLINE_VALUE)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePg(),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
