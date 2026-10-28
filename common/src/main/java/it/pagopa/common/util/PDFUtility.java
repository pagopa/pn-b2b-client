package it.pagopa.common.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility per operazioni su contenuti PDF (Apache PDFBox).
 * Unico punto di accesso a PDFBox per i moduli che dipendono da {@code common}.
 */
public final class PDFUtility {

    private static final Logger log = LoggerFactory.getLogger(PDFUtility.class);

    /** Altezza footer esclusa dall'estrazione by-area (allineata a PnContentExtractor storico). */
    public static final float DEFAULT_FOOTER_HEIGHT = 100f;

    private PDFUtility() {
    }

    /**
     * Risultato di un'estrazione strutturata: testo (senza footer) + parole in bold.
     */
    public record StructuredText(String text, List<String> boldWords) {
    }

    /**
     * Estrae il testo da un PDF.
     *
     * @return testo estratto, oppure {@code null} se il contenuto non è leggibile
     */
    public static String extractText(byte[] pdfContent) {
        if (pdfContent == null || pdfContent.length == 0) {
            return null;
        }
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            return extractText(document);
        } catch (Exception exception) {
            log.warn("PDFUtility.extractText: errore parsing PDF: {}", exception.getMessage());
            return null;
        }
    }

    /**
     * Estrae testo escludendo il footer e le parole in bold, in un'unica apertura del documento
     * (stessa logica storica di {@code PnContentExtractor}).
     *
     * @return risultato, oppure {@code null} se il PDF non è leggibile
     */
    public static StructuredText extractStructuredText(byte[] pdfContent) {
        return extractStructuredText(pdfContent, DEFAULT_FOOTER_HEIGHT);
    }

    /**
     * Estrae testo escludendo il footer e le parole in bold, in un'unica apertura del documento.
     *
     * @param footerHeight altezza del footer da escludere (punti PDF)
     * @return risultato, oppure {@code null} se il PDF non è leggibile
     */
    public static StructuredText extractStructuredText(byte[] pdfContent, float footerHeight) {
        if (pdfContent == null || pdfContent.length == 0) {
            return null;
        }
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            List<String> boldWords = extractBoldWords(document);
            String text = extractTextExcludingFooter(document, footerHeight);
            return new StructuredText(text, boldWords);
        } catch (Exception exception) {
            log.warn("PDFUtility.extractStructuredText: errore parsing PDF: {}", exception.getMessage());
            return null;
        }
    }

    /**
     * Restituisce il numero di pagine del PDF.
     *
     * @throws IllegalStateException se il contenuto non è un PDF leggibile
     */
    public static int getNumberOfPages(byte[] pdfContent) {
        if (pdfContent == null || pdfContent.length == 0) {
            throw new IllegalStateException("Contenuto PDF assente o vuoto");
        }
        try (PDDocument document = Loader.loadPDF(pdfContent)) {
            return document.getNumberOfPages();
        } catch (Exception exception) {
            throw new IllegalStateException("Errore durante la lettura del PDF: " + exception.getMessage(), exception);
        }
    }

    /**
     * Verifica se il PDF contiene la stringa cercata (case-insensitive, come sottostringa).
     */
    public static boolean containsText(byte[] pdfContent, String searchText) {
        return containsText(pdfContent, searchText, false);
    }

    /**
     * Verifica se il PDF contiene la stringa cercata (case-insensitive).
     *
     * @param wholeWord se {@code true}, match come parola intera ({@code \b...\b})
     */
    public static boolean containsText(byte[] pdfContent, String searchText, boolean wholeWord) {
        if (searchText == null || searchText.isEmpty()) {
            return false;
        }
        String extractedText = extractText(pdfContent);
        if (extractedText == null || extractedText.isEmpty()) {
            return false;
        }
        if (wholeWord) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(searchText) + "\\b", Pattern.CASE_INSENSITIVE);
            return pattern.matcher(extractedText).find();
        }
        return extractedText.toUpperCase(Locale.ROOT).contains(searchText.toUpperCase(Locale.ROOT));
    }

    /**
     * Verifica se il testo estratto dal PDF matcha la regex indicata.
     */
    public static boolean matchesPattern(byte[] pdfContent, Pattern pattern) {
        if (pattern == null) {
            return false;
        }
        String extractedText = extractText(pdfContent);
        if (extractedText == null || extractedText.isEmpty()) {
            return false;
        }
        return pattern.matcher(extractedText).find();
    }

    private static String extractText(PDDocument document) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setSortByPosition(true);
        return pdfStripper.getText(document);
    }

    private static String extractTextExcludingFooter(PDDocument document, float footerHeight) throws IOException {
        PDFTextStripperByArea pdfTextStripperByArea = new PDFTextStripperByArea();
        pdfTextStripperByArea.setSortByPosition(true);

        String extractedText = null;
        StringBuilder textToExtract = new StringBuilder();
        for (PDPage page : document.getPages()) {
            PDRectangle mediaBox = page.getMediaBox();
            float height = mediaBox.getHeight();
            float width = mediaBox.getWidth();
            Rectangle region = new Rectangle(0, 0, (int) width, (int) (height - footerHeight));

            pdfTextStripperByArea.addRegion("contentRegion", region);
            pdfTextStripperByArea.extractRegions(page);

            String text = pdfTextStripperByArea.getTextForRegion("contentRegion");
            textToExtract.append(text).append(System.lineSeparator());

            pdfTextStripperByArea.removeRegion("contentRegion");
            extractedText = textToExtract.toString();
        }
        return extractedText;
    }

    private static List<String> extractBoldWords(PDDocument document) throws IOException {
        BoldWordExtractor boldWordExtractor = new BoldWordExtractor();
        boldWordExtractor.setSortByPosition(true);
        boldWordExtractor.getText(document);
        return boldWordExtractor.getBoldWordList();
    }

    private static final class BoldWordExtractor extends PDFTextStripper {
        private static final String BOLD = "bold";
        private final List<String> boldWordList = new ArrayList<>();

        private BoldWordExtractor() throws IOException {
            super();
        }

        private List<String> getBoldWordList() {
            return boldWordList;
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) {
            StringBuilder boldWord = new StringBuilder();
            boolean isBold = false;

            for (TextPosition textPosition : textPositions) {
                if (textPosition.getFont().getName().toLowerCase(Locale.ROOT).contains(BOLD)) {
                    isBold = true;
                    boldWord.append(textPosition.getUnicode());
                } else {
                    foundBoldWord(boldWord, isBold);
                    isBold = false;
                }
            }
            foundBoldWord(boldWord, isBold);
        }

        private void foundBoldWord(StringBuilder boldWord, boolean isBold) {
            if (isBold && !boldWord.isEmpty()) {
                boldWordList.add(boldWord.toString().trim());
                boldWord.setLength(0);
            }
        }
    }
}
