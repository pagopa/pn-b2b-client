package it.pagopa.common.pdf.visualtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizza i campi dinamici del PDF confrontandoli con i {@link PdfDocumentTemplate#fieldDefinitions()}.
 *
 * <p>Per ogni {@link PdfDocumentTemplate.FieldDefinition}:</p>
 * <ol>
 *   <li>Localizza i frammenti tramite il {@link FieldLocator}</li>
 *   <li>Valida il valore tramite il {@link FieldValidator}</li>
 *   <li>Se la validazione fallisce, accumula un {@link FieldIssue}</li>
 *   <li>In ogni caso (pass o fail), calcola le {@link ExclusionArea} per il visual diff</li>
 * </ol>
 *
 * <p>La classe è {@code final} e non ha stato condiviso tra invocazioni diverse.</p>
 */
public final class DynamicFieldAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(DynamicFieldAnalyzer.class);

    /** Padding orizzontale della pagina in punti PDF usato per la strategia FULL_LINE. */
    private static final float DEFAULT_PAGE_WIDTH_PT = 595f; // A4 larghezza

    private DynamicFieldAnalyzer() {
    }

    /**
     * Esegue l'analisi completa di tutti i campi definiti nel template sul testo estratto.
     *
     * @param extractedText testo estratto dal PDF da verificare
     * @param template      template che descrive i campi attesi
     * @return risultato dell'analisi contenente errori e aree di esclusione
     */
    public static AnalysisResult analyze(ExtractedPdfText extractedText, PdfDocumentTemplate template) {
        List<TextFragment> allFragments = extractedText.allFragments();
        List<FieldIssue> issues = new ArrayList<>();
        List<ExclusionArea> exclusions = new ArrayList<>();

        float pageWidthPt = resolvePageWidth(extractedText);

        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            List<TextFragment> located = def.locator().locate(allFragments);
            String error = def.validator().validate(located);

            if (error != null) {
                log.debug("FieldIssue [{}}]: {}", def.fieldName(), error);
                issues.add(new FieldIssue(def.fieldName(), error, located));
            } else {
                log.debug("Campo [{}] validato con successo ({} frammenti)", def.fieldName(), located.size());
            }

            // Aggiungi le aree di esclusione anche se la validazione fallisce,
            // così il visual diff esclude comunque le aree dinamiche.
            for (TextFragment f : located) {
                exclusions.add(ExclusionArea.from(f, def.maskStrategy(), pageWidthPt));
            }
        }

        return new AnalysisResult(List.copyOf(issues), List.copyOf(exclusions));
    }

    private static float resolvePageWidth(ExtractedPdfText extractedText) {
        // Stima la larghezza dalla X massima dei frammenti o usa il default A4
        return extractedText.allFragments().stream()
                .map(TextFragment::xEnd)
                .max(Float::compare)
                .map(maxX -> Math.max(maxX + 72f, DEFAULT_PAGE_WIDTH_PT)) // aggiunge margine
                .orElse(DEFAULT_PAGE_WIDTH_PT);
    }

    // -----------------------------------------------------------------------
    // Result record
    // -----------------------------------------------------------------------

    /**
     * Risultato dell'analisi dinamica: raccoglie tutti gli errori accumulati
     * e le aree di esclusione calcolate per il visual diff.
     *
     * @param issues     problemi riscontrati durante la validazione dei campi
     * @param exclusions aree da escludere dal confronto visivo pixel-level
     */
    public record AnalysisResult(List<FieldIssue> issues, List<ExclusionArea> exclusions) {

        /** @return {@code true} se non è stato trovato nessun problema */
        public boolean isValid() {
            return issues.isEmpty();
        }
    }
}
