package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.FieldValidators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldLocators;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

/** Attestazione di malfunzionamento e ripristino: la descrizione del disservizio ha lunghezza variabile, quindi il corpo dopo "ATTESTA CHE:" fino al footer è escluso dal confronto visivo. */
public final class MalfunctionTemplate {

    public static final String KEY = "MALFUNCTION";

    private MalfunctionTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .expectedPageCount(1)
                .field("Date disservizio",
                        SendFieldLocators.date(),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .field("Ore disservizio",
                        SendFieldLocators.time(),
                        SendFieldValidators.oraHHmm(),
                        MaskStrategy.TIGHT_BOX)
                .field("Corpo dell'attestazione",
                        FieldLocators.between(FieldLocators.lineContaining("CHE:"), FieldLocators.lineContaining("azioni")),
                        FieldValidators.present(),
                        MaskStrategy.FULL_LINE)
                .build();
    }
}
