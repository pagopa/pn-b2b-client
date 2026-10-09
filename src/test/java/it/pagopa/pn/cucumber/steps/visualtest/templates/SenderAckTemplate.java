package it.pagopa.pn.cucumber.steps.visualtest.templates;

import it.pagopa.common.pdf.visualtest.FieldLocators;
import it.pagopa.common.pdf.visualtest.MaskStrategy;
import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.SendFieldValidators;

import java.util.regex.Pattern;

import static it.pagopa.common.pdf.visualtest.FieldValidators.present;

/**
 * Template del documento <em>Attestazione opponibile a terzi: notifica presa in carico</em>
 * (SENDER_ACK – singolo destinatario).
 *
 * <p>Definisce i campi dinamici che variano ad ogni emissione del documento
 * (es. IUN, CF mittente, data) e che devono essere:</p>
 * <ol>
 *   <li>Validati nel formato atteso</li>
 *   <li>Mascherati nel visual diff per evitare falsi positivi</li>
 * </ol>
 *
 * <p>Chiave del template: {@value #KEY}</p>
 */
public final class SenderAckTemplate {

    /** Chiave con cui il template è registrato nel {@link it.pagopa.common.pdf.visualtest.DocumentTemplateRegistry}. */
    public static final String KEY = "SENDER_ACK";

    private SenderAckTemplate() {
    }

    /**
     * Costruisce e restituisce il {@link PdfDocumentTemplate} configurato.
     */
    public static PdfDocumentTemplate build() {
        return PdfDocumentTemplate.builder(KEY)
                .field(
                        "IUN",
                        FieldLocators.aboveY(500f, FieldLocators.labelSameLine("IUN", 300f)),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data emissione",
                        FieldLocators.aboveY(550f, FieldLocators.labelSameLine("in data", 150f)),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale mittente",
                        FieldLocators.aboveY(550f, FieldLocators.labelSameLine("C.F.", 150f)),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.aboveY(350f, FieldLocators.labelSameLine("Codice Fiscale", 250f)),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
