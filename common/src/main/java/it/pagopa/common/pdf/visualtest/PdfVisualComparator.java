package it.pagopa.common.pdf.visualtest;

import de.redsix.pdfcompare.CompareResultImpl;
import de.redsix.pdfcompare.PageArea;
import de.redsix.pdfcompare.PdfComparator;
import de.redsix.pdfcompare.env.DefaultEnvironment;
import de.redsix.pdfcompare.env.SimpleEnvironment;
import it.pagopa.common.util.PDFUtility;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
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
     * 300 DPI garantisce alta fedeltà e stabilità di rendering.
     */
    private static final int RENDER_DPI = 300;

    /** Fattore di conversione: punti PDF (72pt=1in) → pixel al DPI scelto. */
    private static final float PDF_PT_TO_PIXEL = RENDER_DPI / 72.0f;

    /** Soglia tolleranza differenze pixel (0.02%) per assorbire antialiasing e variazioni cross-platform. */
    private static final double ALLOWED_DIFF_PERCENT = 0.02;

    /** Margine (pixel) aggiunto alle maschere per assorbire l'anti-aliasing dei bordi dei glifi. */
    private static final int ANTIALIAS_MARGIN_PX = 2;

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

            SimpleEnvironment env = new SimpleEnvironment(DefaultEnvironment.create());
            env.setDPI(RENDER_DPI);
            env.setAllowedDiffInPercent(ALLOWED_DIFF_PERCENT);

            // Costruisce il comparatore: i due stream sono passati al costruttore (API 1.2.8)
            PdfComparator<CompareResultImpl> comparator =
                    new PdfComparator<>(expectedStream, actualStream, new CompareResultImpl())
                            .withEnvironment(env);

            // Aggiunge le aree di esclusione convertendo le coordinate in pixel
            float[] pageHeights = pageHeightsPt(actualPdf);
            for (ExclusionArea ex : exclusions) {
                comparator.withIgnore(toPageArea(ex, pageHeights));
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
                try (java.io.OutputStream os = java.nio.file.Files.newOutputStream(diffOutputPath)) {
                    result.writeTo(os);
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
     * in un {@code PageArea} di pdfcompare (pixel, origine alto-sinistra, pagine 1-based).
     */
    private static PageArea toPageArea(ExclusionArea ex, float[] pageHeightsPt) {
        int page = ex.pageIndex() + 1; // pdfcompare usa pagine 1-based
        float pageHeight = ex.pageIndex() < pageHeightsPt.length
                ? pageHeightsPt[ex.pageIndex()]
                : PDRectangle.A4.getHeight();
        int x1   = toPixel(ex.x()) - ANTIALIAS_MARGIN_PX;
        int y1   = toPixel(pageHeight - (ex.y() + ex.height())) - ANTIALIAS_MARGIN_PX;
        int x2   = toPixel(ex.x() + ex.width()) + ANTIALIAS_MARGIN_PX;
        int y2   = toPixel(pageHeight - ex.y()) + ANTIALIAS_MARGIN_PX;
        return new PageArea(page, Math.max(0, x1), Math.max(0, y1), x2, y2);
    }

    /** Altezza (in punti PDF) della crop-box di ogni pagina, ovvero l'area renderizzata. */
    private static float[] pageHeightsPt(byte[] pdfContent) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdfContent)) {
            float[] heights = new float[doc.getNumberOfPages()];
            for (int i = 0; i < heights.length; i++) {
                heights[i] = doc.getPage(i).getCropBox().getHeight();
            }
            return heights;
        }
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
