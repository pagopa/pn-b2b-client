package it.pagopa.common.pdf.visualtest;

import de.redsix.pdfcompare.CompareResultImpl;
import de.redsix.pdfcompare.PageArea;
import de.redsix.pdfcompare.PdfComparator;
import it.pagopa.common.util.PDFUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Esegue il confronto visivo pixel-level tra due PDF usando {@code de.redsix:pdfcompare 1.2.8}.
 *
 * <p><strong>Responsabilità:</strong></p>
 * <ul>
 *   <li>Pre-check economico: verifica il numero di pagine prima di avviare il rendering</li>
 *   <li>Conversione coordinate: da punti PDF (origine basso-sinistra) a pixel di rendering</li>
 *   <li>Configurazione delle {@link ExclusionArea} su {@code pdfcompare} via {@code PageArea}</li>
 *   <li>Produzione di un {@link VisualDiffResult} con esito e path immagine di diff</li>
 * </ul>
 *
 * <p>I tipi nativi di {@code pdfcompare} e PDFBox sono confinati in questa classe;
 * le firme pubbliche usano solo tipi di questo package.</p>
 */
public final class PdfVisualComparator {

    private static final Logger log = LoggerFactory.getLogger(PdfVisualComparator.class);

    /**
     * DPI di rendering per il confronto visivo.
     * 150 DPI offre un buon equilibrio qualità/velocità per documenti A4.
     */
    private static final int RENDER_DPI = 150;

    /** Fattore di conversione: punti PDF (72pt=1in) → pixel al DPI scelto. */
    private static final float PDF_PT_TO_PIXEL = RENDER_DPI / 72.0f;

    private PdfVisualComparator() {
    }

    /**
     * Confronta visivamente {@code actualPdf} con {@code expectedPdf} mascherando le
     * {@code exclusions}.
     *
     * @param expectedPdf    contenuto binario del PDF golden master
     * @param actualPdf      contenuto binario del PDF da verificare
     * @param exclusions     aree da escludere dal confronto (coordinate in punti PDF)
     * @param diffOutputPath path dove salvare il PDF di diff in caso di differenze
     *                       ({@code null} per non salvare)
     * @return risultato del confronto
     */
    public static VisualDiffResult compare(
            byte[] expectedPdf,
            byte[] actualPdf,
            List<ExclusionArea> exclusions,
            Path diffOutputPath) {

        // ── Pre-check economico sul numero di pagine ──────────────────────
        int expectedPages;
        int actualPages;
        try {
            expectedPages = PDFUtility.getNumberOfPages(expectedPdf);
            actualPages   = PDFUtility.getNumberOfPages(actualPdf);
        } catch (IllegalStateException e) {
            return VisualDiffResult.failure(
                    "Impossibile leggere il numero di pagine: " + e.getMessage(), null);
        }
        if (expectedPages != actualPages) {
            return VisualDiffResult.failure(
                    "Numero pagine diverso – atteso: " + expectedPages
                    + ", trovato: " + actualPages,
                    null);
        }

        // ── Confronto visivo con pdfcompare ───────────────────────────────
        try (ByteArrayInputStream expectedStream = new ByteArrayInputStream(expectedPdf);
             ByteArrayInputStream actualStream   = new ByteArrayInputStream(actualPdf)) {

            // Costruisce il comparatore: i due stream sono passati al costruttore (API 1.2.8)
            PdfComparator<CompareResultImpl> comparator =
                    new PdfComparator<>(expectedStream, actualStream, new CompareResultImpl());

            // Aggiunge le aree di esclusione convertendo le coordinate in pixel
            for (ExclusionArea ex : exclusions) {
                comparator.withIgnore(toPageArea(ex));
            }

            // Avvia il confronto
            CompareResultImpl result = comparator.compare();

            if (result.isEqual()) {
                log.debug("PdfVisualComparator: PDF identici (escluse le maschere) – {} pagine",
                        expectedPages);
                return VisualDiffResult.success(expectedPages);
            }

            // Il diff ha trovato differenze
            int diffPageCount = result.getNumberOfPages();
            Path savedDiff = null;
            if (diffOutputPath != null) {
                try {
                    result.writeTo(diffOutputPath.toString());
                    savedDiff = diffOutputPath;
                    log.info("PdfVisualComparator: diff salvato in {}", diffOutputPath);
                } catch (Exception e) {
                    log.warn("PdfVisualComparator: impossibile salvare il diff in {}: {}",
                            diffOutputPath, e.getMessage());
                }
            }

            return VisualDiffResult.failure(
                    "Differenze visive rilevate su " + diffPageCount + " pagina/e "
                    + "(expected vs actual non corrispondono al netto delle maschere dinamiche)",
                    savedDiff);

        } catch (IOException e) {
            return VisualDiffResult.failure(
                    "Errore I/O durante il confronto visivo: " + e.getMessage(), null);
        }
    }

    // -----------------------------------------------------------------------
    // Internals
    // -----------------------------------------------------------------------

    /**
     * Converte un'area di esclusione in coordinate PDF (punti, origine basso-sinistra)
     * in un {@code PageArea} di pdfcompare (pixel, pagine 1-based).
     */
    private static PageArea toPageArea(ExclusionArea ex) {
        int page = ex.pageIndex() + 1; // pdfcompare usa pagine 1-based
        int x1   = toPixel(ex.x());
        int y1   = toPixel(ex.y());
        int x2   = toPixel(ex.x() + ex.width());
        int y2   = toPixel(ex.y() + ex.height());
        return new PageArea(page, x1, y1, x2, y2);
    }

    private static int toPixel(float ptCoordinate) {
        return Math.max(0, Math.round(ptCoordinate * PDF_PT_TO_PIXEL));
    }

    // -----------------------------------------------------------------------
    // Result record
    // -----------------------------------------------------------------------

    /**
     * Risultato del confronto visivo pixel-level.
     *
     * @param equal          {@code true} se i PDF sono visivamente identici
     * @param errorMessage   descrizione del problema ({@code null} se {@code equal=true})
     * @param diffImagePath  path al PDF di diff generato ({@code null} se non generato)
     * @param pageCount      numero di pagine confrontate ({@code -1} se fallimento precoce)
     */
    public record VisualDiffResult(boolean equal, String errorMessage, Path diffImagePath, int pageCount) {

        static VisualDiffResult success(int pageCount) {
            return new VisualDiffResult(true, null, null, pageCount);
        }

        static VisualDiffResult failure(String message, Path diffPath) {
            return new VisualDiffResult(false, message, diffPath, -1);
        }
    }
}
