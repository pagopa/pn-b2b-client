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
                        FieldLocators.regex(Pattern.compile("\\b[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d\\b")),
                        SendFieldValidators.iun(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Data emissione",
                        FieldLocators.regex(Pattern.compile("\\b(?:0?[1-9]|[12]\\d|3[01])/(?:0?[1-9]|1[0-2])/\\d{4}\\b")),
                        SendFieldValidators.dataItaliana(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale mittente",
                        FieldLocators.regex(Pattern.compile("\\b\\d{11}\\b")),
                        SendFieldValidators.codiceFiscalePg(),
                        MaskStrategy.TIGHT_BOX)
                .field(
                        "Codice Fiscale destinatario",
                        FieldLocators.regex(Pattern.compile("\\b[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]\\b")),
                        SendFieldValidators.codiceFiscalePf(),
                        MaskStrategy.TIGHT_BOX)
                .build();
    }
}
