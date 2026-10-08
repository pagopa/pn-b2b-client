package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Utility di estrazione geometrica del testo da documenti PDF.
 *
 * <p>Espone esclusivamente tipi del package {@code it.pagopa.common.pdf.visualtest}
 * nelle firme pubbliche: nessun tipo nativo di Apache PDFBox trapela all'esterno.</p>
 *
 * <p>La classe è {@code final} e non instanziabile (solo metodi statici di utilità).</p>
 */
public final class PdfTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

    /** Tolleranza verticale predefinita (punti PDF) per raggruppare testo sulla stessa riga. */
    public static final float DEFAULT_LINE_TOLERANCE_PT = 2.0f;

    private PdfTextExtractor() {
    }

    /**
     * Estrae il testo strutturato da un PDF binario.
     *
     * @param pdfContent contenuto grezzo del PDF
     * @return testo estratto con bounding-box per ogni frammento
     * @throws PdfExtractionException se il PDF non è leggibile o l'estrazione fallisce
     */
    public static ExtractedPdfText extract(byte[] pdfContent) {
        validateContent(pdfContent);
        try (PDDocument doc = Loader.loadPDF(pdfContent)) {
            return extractFromDocument(doc);
        } catch (IOException e) {
            throw new PdfExtractionException("Errore durante l'apertura del PDF per estrazione geometrica", e);
        }
    }

    // -----------------------------------------------------------------------
    // Implementazione interna – i tipi PDFBox non escono da questo confine
    // -----------------------------------------------------------------------

    private static ExtractedPdfText extractFromDocument(PDDocument doc) throws IOException {
        List<PageText> pageTexts = new ArrayList<>();
        int pageCount = doc.getNumberOfPages();

        for (int pageIdx = 0; pageIdx < pageCount; pageIdx++) {
            List<TextFragment> fragments = extractPageFragments(doc, pageIdx);
            pageTexts.add(new PageText(pageIdx, fragments));
        }
        return new ExtractedPdfText(pageTexts);
    }

    private static List<TextFragment> extractPageFragments(PDDocument doc, int pageIdx) throws IOException {
        FragmentCollector collector = new FragmentCollector(pageIdx);
        collector.setStartPage(pageIdx + 1);
        collector.setEndPage(pageIdx + 1);
        collector.setSortByPosition(true);
        collector.getText(doc); // trigger dell'estrazione
        return collector.getFragments();
    }

    private static void validateContent(byte[] pdfContent) {
        if (pdfContent == null || pdfContent.length == 0) {
            throw new PdfExtractionException("Il contenuto PDF è null o vuoto", null);
        }
    }

    // -----------------------------------------------------------------------
    // Inner class privata: accede a PDFBox ma non espone nessuno dei suoi tipi
    // -----------------------------------------------------------------------

    private static final class FragmentCollector extends PDFTextStripper {

        private final int pageIndex;
        private final List<TextFragment> fragments = new ArrayList<>();

        private FragmentCollector(int pageIndex) throws IOException {
            super();
            this.pageIndex = pageIndex;
        }

        List<TextFragment> getFragments() {
            return List.copyOf(fragments);
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) {
            if (text == null || text.isBlank()) {
                return;
            }
            // Calcola il bounding-box del gruppo di TextPosition
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxXEnd = Float.MIN_VALUE;
            float maxYTop = Float.MIN_VALUE;

            for (TextPosition tp : textPositions) {
                float x = tp.getXDirAdj();
                float w = tp.getWidthDirAdj();
                // getYDirAdj() è la baseline con origine in alto: la convertiamo in origine basso-sinistra
                float baseline = tp.getPageHeight() - tp.getYDirAdj();
                float[] ascDesc = ascentDescent(tp);

                minX = Math.min(minX, x);
                minY = Math.min(minY, baseline - ascDesc[1]);
                maxXEnd = Math.max(maxXEnd, x + w);
                maxYTop = Math.max(maxYTop, baseline + ascDesc[0]);
            }

            String trimmed = text.trim();
            if (!trimmed.isEmpty()) {
                fragments.add(new TextFragment(
                        trimmed,
                        minX,
                        minY,
                        maxXEnd - minX,
                        maxYTop - minY,
                        pageIndex));
            }
        }

        /**
         * Restituisce {ascent, descent} (valori positivi, in punti PDF) del glifo, così che il
         * bounding-box copra l'intera altezza visibile del testo (inclusi i discendenti).
         */
        private static float[] ascentDescent(TextPosition tp) {
            float size = tp.getFontSizeInPt();
            float ascent = 0.8f * size;
            float descent = 0.25f * size;
            PDFontDescriptor fd = tp.getFont() != null ? tp.getFont().getFontDescriptor() : null;
            if (fd != null) {
                if (fd.getAscent() > 0) {
                    ascent = fd.getAscent() / 1000f * size;
                }
                if (fd.getDescent() < 0) {
                    descent = -fd.getDescent() / 1000f * size;
                }
            }
            return new float[]{Math.max(ascent, tp.getHeightDir()), descent};
        }
    }

    // -----------------------------------------------------------------------
    // Eccezione unchecked specializzata (evita checked IOException in API pubblica)
    // -----------------------------------------------------------------------

    /**
     * Eccezione non controllata lanciata quando l'estrazione geometrica del PDF fallisce.
     */
    public static final class PdfExtractionException extends RuntimeException {
        PdfExtractionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
