package it.pagopa.common.pdf.visualtest.support;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Genera da zero PDF sintetici e deterministici per i test del {@code PdfComplianceChecker}.
 *
 * <p>Il documento prodotto contiene una parte statica (titolo, paragrafo, immagine/logo, tabella)
 * e tre campi dinamici posizionati a destra di un'etichetta:</p>
 * <ul>
 *   <li>{@value #LABEL_ID} → identificativo univoco (UUID v4)</li>
 *   <li>{@value #LABEL_DATE} → data ISO-8601</li>
 *   <li>{@value #LABEL_AMOUNT} → intero positivo</li>
 * </ul>
 *
 * <p>Ogni aspetto del documento è configurabile tramite {@link Spec} per introdurre differenze
 * mirate (testo, impaginazione, immagini, tabelle, numero di pagine).</p>
 */
public final class TestPdfFactory {

    public static final String LABEL_ID = "Codice identificativo:";
    public static final String LABEL_DATE = "Data emissione:";
    public static final String LABEL_AMOUNT = "Importo dovuto:";

    public static final String DEFAULT_ID = "3f2b1c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d";
    public static final String DEFAULT_DATE = "2024-01-31";
    public static final String DEFAULT_AMOUNT = "1250";

    public static final String DEFAULT_TITLE = "ATTESTAZIONE DI TEST";
    public static final String DEFAULT_PARAGRAPH =
            "Il presente documento attesta la corretta generazione del contenuto statico.";

    private static final float LABEL_X = 50f;
    private static final float VALUE_X = 200f;
    private static final float ID_Y = 640f;
    private static final float DATE_Y = 615f;
    private static final float AMOUNT_Y = 590f;

    private TestPdfFactory() {
    }

    public static Spec spec() {
        return new Spec();
    }

    /** Documento di riferimento (golden) a una pagina con i valori dinamici di default. */
    public static byte[] reference() {
        return spec().build();
    }

    /** Configurazione fluente del documento da generare. */
    public static final class Spec {
        private String id = DEFAULT_ID;
        private String date = DEFAULT_DATE;
        private String amount = DEFAULT_AMOUNT;
        private String title = DEFAULT_TITLE;
        private String paragraph = DEFAULT_PARAGRAPH;
        private String[][] tableRows = {
                {"Voce", "Descrizione"},
                {"A", "Spese di notifica"},
                {"B", "Diritti di segreteria"}
        };
        private float layoutShiftY = 0f;
        private Color logoColor = new Color(0, 102, 204);
        private boolean drawTableBorders = true;
        private int pageCount = 1;
        private boolean omitIdField = false;

        public Spec id(String v) { this.id = v; return this; }
        public Spec date(String v) { this.date = v; return this; }
        public Spec amount(String v) { this.amount = v; return this; }
        public Spec title(String v) { this.title = v; return this; }
        public Spec paragraph(String v) { this.paragraph = v; return this; }
        public Spec tableRows(String[][] v) { this.tableRows = v; return this; }
        public Spec layoutShiftY(float v) { this.layoutShiftY = v; return this; }
        public Spec logoColor(Color v) { this.logoColor = v; return this; }
        public Spec drawTableBorders(boolean v) { this.drawTableBorders = v; return this; }
        public Spec pageCount(int v) { this.pageCount = v; return this; }
        public Spec omitIdField() { this.omitIdField = true; return this; }

        public byte[] build() {
            try (PDDocument doc = new PDDocument()) {
                PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDImageXObject logo = LosslessFactory.createFromImage(doc, logoImage(logoColor));

                for (int p = 0; p < pageCount; p++) {
                    PDPage page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                        if (p == 0) {
                            drawFirstPage(cs, regular, bold, logo);
                        } else {
                            text(cs, regular, 11, LABEL_X, 780, "Pagina aggiuntiva " + (p + 1));
                        }
                    }
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.save(out);
                return out.toByteArray();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private void drawFirstPage(PDPageContentStream cs, PDType1Font regular, PDType1Font bold,
                                   PDImageXObject logo) throws IOException {
            float dy = layoutShiftY;

            cs.drawImage(logo, 470, 760, 80, 40);
            text(cs, bold, 16, LABEL_X, 770 + dy, title);
            text(cs, regular, 10, LABEL_X, 730 + dy, paragraph);

            if (!omitIdField) {
                labelValue(cs, bold, regular, LABEL_ID, id, ID_Y + dy);
            }
            labelValue(cs, bold, regular, LABEL_DATE, date, DATE_Y + dy);
            labelValue(cs, bold, regular, LABEL_AMOUNT, amount, AMOUNT_Y + dy);

            drawTable(cs, regular, 50, 520 + dy);
        }

        private void labelValue(PDPageContentStream cs, PDType1Font bold, PDType1Font regular,
                                String label, String value, float y) throws IOException {
            text(cs, bold, 11, LABEL_X, y, label);
            if (value != null && !value.isEmpty()) {
                text(cs, regular, 11, VALUE_X, y, value);
            }
        }

        private void drawTable(PDPageContentStream cs, PDType1Font font, float x, float topY)
                throws IOException {
            float rowH = 22f;
            float[] colW = {80f, 300f};
            float tableW = colW[0] + colW[1];

            for (int r = 0; r < tableRows.length; r++) {
                float baseline = topY - (r + 1) * rowH + 7;
                text(cs, font, 10, x + 5, baseline, tableRows[r][0]);
                text(cs, font, 10, x + colW[0] + 5, baseline, tableRows[r][1]);
            }

            if (drawTableBorders) {
                cs.setStrokingColor(Color.BLACK);
                cs.setLineWidth(0.8f);
                for (int r = 0; r <= tableRows.length; r++) {
                    float y = topY - r * rowH;
                    cs.moveTo(x, y);
                    cs.lineTo(x + tableW, y);
                }
                float bottom = topY - tableRows.length * rowH;
                float cx = x;
                for (int c = 0; c <= colW.length; c++) {
                    cs.moveTo(cx, topY);
                    cs.lineTo(cx, bottom);
                    if (c < colW.length) {
                        cx += colW[c];
                    }
                }
                cs.stroke();
            }
        }
    }

    private static void text(PDPageContentStream cs, PDType1Font font, float size, float x, float y, String s)
            throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(s);
        cs.endText();
    }

    private static BufferedImage logoImage(Color color) {
        BufferedImage img = new BufferedImage(80, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 80, 40);
            g.setColor(color);
            g.fillOval(5, 5, 30, 30);
            g.fillRect(40, 10, 35, 20);
        } finally {
            g.dispose();
        }
        return img;
    }
}
