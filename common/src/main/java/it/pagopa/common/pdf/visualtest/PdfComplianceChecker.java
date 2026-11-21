package it.pagopa.common.pdf.visualtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade che orchestra il processo completo di verifica ibrida di un documento PDF:
 *
 * <ol>
 *   <li>Controllo strutturale sul numero di pagine (pre-check economico via {@link PdfTextExtractor})</li>
 *   <li>Estrazione geometrica del testo con {@link PdfTextExtractor}</li>
 *   <li>Analisi dinamica dei campi con {@link DynamicFieldAnalyzer}</li>
 *   <li>Verifica della stabilità del layout con {@link LayoutStability}: confronto pixel-level con {@link PdfVisualComparator}
 *       se garantita, altrimenti per bande con {@link PdfBandComparator}</li>
 * </ol>
 *
 * <p>La classe è {@code final} e stateless: ogni invocazione di {@link #check} è indipendente.</p>
 */
public final class PdfComplianceChecker {

    private static final Logger log = LoggerFactory.getLogger(PdfComplianceChecker.class);

    private PdfComplianceChecker() {
    }

    /**
     * Esegue la verifica di conformità completa.
     *
     * @param expectedPdf    contenuto binario del PDF golden master
     * @param actualPdf      contenuto binario del PDF da verificare
     * @param template       template che descrive la struttura e i campi attesi
     * @param diffOutputPath path dove salvare l'immagine di diff ({@code null} = non salvare)
     * @return report aggregato con tutti gli errori riscontrati
     */
    public static PdfComplianceReport check(
            byte[] expectedPdf,
            byte[] actualPdf,
            PdfDocumentTemplate template,
            Path diffOutputPath) {

        List<String> structuralErrors = new ArrayList<>();
        List<String> textDifferences = new ArrayList<>();

        // ── 1. Validazione numero pagine (pre-check) ──────────────────────
        if (template.expectedPageCount() > 0) {
            int actualPages = safeGetPageCount(actualPdf, structuralErrors);
            if (actualPages > 0 && actualPages != template.expectedPageCount()) {
                structuralErrors.add(
                        "Numero pagine atteso: " + template.expectedPageCount()
                        + ", trovato: " + actualPages);
            }
        }

        // ── 2. Estrazione geometrica del testo ────────────────────────────
        ExtractedPdfText extractedText;
        try {
            extractedText = PdfTextExtractor.extract(actualPdf);
        } catch (PdfTextExtractor.PdfExtractionException e) {
            structuralErrors.add("Impossibile estrarre il testo dal PDF: " + e.getMessage());
            log.warn("PdfComplianceChecker: estrazione testo fallita", e);
            return new PdfComplianceReport(List.of(), null, structuralErrors, List.of(), LayoutStability.Mode.NOT_APPLICABLE, List.of());
        }

        // ── 3. Analisi dinamica dei campi ─────────────────────────────────
        DynamicFieldAnalyzer.AnalysisResult analysis =
                DynamicFieldAnalyzer.analyze(extractedText, template);
        log.debug("PdfComplianceChecker: analisi campi completata – {} errori, {} exclusions",
                analysis.issues().size(), analysis.exclusions().size());

        // ── 4. Confronto visivo ───────────────────────────────────────────
        PdfVisualComparator.VisualDiffResult visualResult = null;
        LayoutStability.Mode layoutMode = LayoutStability.Mode.NOT_APPLICABLE;
        List<String> layoutNotes = List.of();
        if (structuralErrors.isEmpty()) {
            try {
                ExtractedPdfText expectedExtracted = PdfTextExtractor.extract(expectedPdf);
                DynamicFieldAnalyzer.AnalysisResult expectedAnalysis = DynamicFieldAnalyzer.analyze(expectedExtracted, template);
                StaticTextComparator.firstDifference(
                                StaticTextComparator.normalizedWords(expectedExtracted, template),
                                StaticTextComparator.normalizedWords(extractedText, template))
                        .ifPresent(textDifferences::add);

                LayoutStability.Result layout = LayoutStability.evaluate(expectedExtracted, extractedText, template);
                layoutMode = layout.mode();
                layoutNotes = layout.notes();

                List<ExclusionArea> expectedMasks = new ArrayList<>(expectedAnalysis.exclusions());
                expectedMasks.addAll(template.fixedExclusions());
                List<ExclusionArea> actualMasks = new ArrayList<>(analysis.exclusions());
                if (layoutMode == LayoutStability.Mode.NOT_GUARANTEED) {
                    visualResult = PdfBandComparator.compare(expectedPdf, actualPdf, expectedExtracted, extractedText,
                            template, expectedMasks, actualMasks, diffOutputPath);
                    if (visualResult.coverage() != null) {
                        layoutNotes = new ArrayList<>(layoutNotes);
                        layoutNotes.add("copertura: " + visualResult.coverage());
                    }
                } else {
                    List<ExclusionArea> all = new ArrayList<>(expectedMasks);
                    all.addAll(actualMasks);
                    all.addAll(layout.tailMasks());
                    visualResult = PdfVisualComparator.compare(expectedPdf, actualPdf, List.copyOf(all), diffOutputPath);
                    if (CoverageMap.enabled(diffOutputPath)) {
                        try {
                            CoverageMap.write(CoverageMap.ofMasks(expectedPdf, all), diffOutputPath);
                        } catch (java.io.IOException e) {
                            log.warn("PdfComplianceChecker: mappa di copertura non scritta: {}", e.getMessage());
                        }
                    }
                    if (visualResult.equal() && !layout.tailMasks().isEmpty()) {
                        PdfVisualComparator.VisualDiffResult words = PdfBandComparator.compareWordsOnly(expectedPdf,
                                actualPdf, expectedExtracted, extractedText, template, expectedMasks, actualMasks,
                                diffOutputPath);
                        if (!words.equal()) {
                            visualResult = words;
                        }
                    }
                }
            } catch (PdfTextExtractor.PdfExtractionException e) {
                structuralErrors.add("Impossibile estrarre il testo del golden master: " + e.getMessage());
            }
        } else {
            log.warn("PdfComplianceChecker: confronto visivo saltato a causa di errori strutturali");
        }

        return new PdfComplianceReport(analysis.issues(), visualResult, structuralErrors, textDifferences,
                layoutMode, layoutNotes);
    }

    /**
     * Overload senza path di diff (non salva l'immagine di differenza).
     */
    public static PdfComplianceReport check(
            byte[] expectedPdf, byte[] actualPdf, PdfDocumentTemplate template) {
        return check(expectedPdf, actualPdf, template, null);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private static int safeGetPageCount(byte[] pdfContent, List<String> errorAccumulator) {
        try {
            return it.pagopa.common.util.PDFUtility.getNumberOfPages(pdfContent);
        } catch (IllegalStateException e) {
            errorAccumulator.add("Impossibile leggere il numero di pagine: " + e.getMessage());
            return -1;
        }
    }
}
