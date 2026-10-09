package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestazione di malfunzionamento / ripristino</em>
 * (Legal Fact Malfunction).
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class MalfunctionTemplate {

    public static final String KEY = "MALFUNCTION";

    private MalfunctionTemplate() {
    }

    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "Data inizio downtime",
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("a decorrere dalla data del", 200f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .field(
                        "Data fine downtime",
                        FieldLocators.aboveY(600f, FieldLocators.labelSameLine("e sino alla data", 200f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.FULL_LINE)
                .build();
    }
}
