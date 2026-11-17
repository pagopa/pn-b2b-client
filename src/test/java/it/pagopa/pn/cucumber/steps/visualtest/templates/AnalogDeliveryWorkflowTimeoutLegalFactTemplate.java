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
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Data decorrenza termini",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Ora decorrenza termini",
                        SendFieldLocators.time(),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.TO_LINE_END_AND_NEXT_LINE)
                .field("Codice fiscale destinatario",
                        SendFieldLocators.codiceFiscalePg(),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
