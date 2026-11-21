package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sensitivity guard: a small visual defect (a 4 pt square, red at 40% opacity) painted on a document that is otherwise
 * compliant must be reported, both when the layout is guaranteed (pixel-perfect) and when it falls back to bands.
 * A change to thresholds, margins or masks that makes the comparison blind to such defects breaks this test.
 */
class MutationDetectionTest {

    private static final String IUN = "AAAA-BBBB-CCCC-202601-A-1";
    private static final String SAME_WIDTH_IUN = "DDDD-EEEE-FFFF-202612-B-2";
    private static final String NARROW_IUN = "IJIL-LIJI-JIIL-201111-I-1";
    private static final String WIDE_IUN = "WMWM-MWMW-WMWM-202808-W-8";

    private static final PdfDocumentTemplate TEMPLATE = PdfDocumentTemplate.builder("MUTATION")
            .field("IUN", FieldLocators.regex(Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d")),
                    FieldValidators.present(), MaskStrategy.INLINE_VALUE)
            .build();

    /** Where the defect is painted, in PDF points (origin bottom-left), on the actual document. */
    enum Spot {
        STATIC_WORD(60, 742),
        WORD_IN_VALUE_PARAGRAPH(62, 674),
        GRAPHIC_RULE(400, 619),
        GRAPHIC_BOX(470, 300),
        BLANK_AREA(300, 450);

        final float x;
        final float y;

        Spot(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    static Stream<Arguments> cases() {
        List<Arguments> out = new ArrayList<>();
        for (Spot spot : Spot.values()) {
            out.add(Arguments.of(LayoutStability.Mode.GUARANTEED, IUN, SAME_WIDTH_IUN, spot));
            out.add(Arguments.of(LayoutStability.Mode.NOT_GUARANTEED, NARROW_IUN, WIDE_IUN, spot));
        }
        return out.stream();
    }

    @ParameterizedTest(name = "{0} {3}")
    @MethodSource("cases")
    void smallDefectIsDetected(LayoutStability.Mode mode, String goldenIun, String actualIun, Spot spot) {
        byte[] expected = page(goldenIun);
        byte[] actual = page(actualIun);
        PdfComplianceReport clean = PdfComplianceChecker.check(expected, actual, TEMPLATE);
        assertEquals(mode, clean.layoutMode(), clean::toString);
        assertTrue(clean.isCompliant(), () -> "falso positivo senza difetti: " + clean);

        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, paint(actual, spot), TEMPLATE);
        assertFalse(mutated.isCompliant(), () -> "difetto " + spot + " non rilevato in " + mode + ": " + mutated);
    }

    /** A 6% darkening of a flat area is a defect also when the layout falls back to bands. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("flatSpots")
    void slightColourShiftOnFlatAreaIsDetectedInBands(Spot spot) {
        byte[] expected = page(NARROW_IUN);
        byte[] actual = page(WIDE_IUN);
        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, paint(actual, spot, Color.BLACK, 0.06f, 8), TEMPLATE);
        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, mutated.layoutMode(), mutated::toString);
        assertFalse(mutated.isCompliant(), () -> "viraggio su " + spot + " non rilevato: " + mutated);
    }

    static Stream<Spot> flatSpots() {
        return Stream.of(Spot.GRAPHIC_BOX, Spot.BLANK_AREA);
    }

    // -----------------------------------------------------------------------
    // Synthetic page: static text, a paragraph wrapping around the value, a thin grey rule, a tinted box
    // -----------------------------------------------------------------------

    private static byte[] page(String iun) {
        String text = "Con riferimento alla notifica avente IUN " + iun + " si attesta che la comunicazione e stata inviata "
                + "regolarmente al destinatario indicato nel presente documento e che l'invio e avvenuto "
                + "correttamente secondo le modalita previste dalla normativa vigente.";
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                line(cs, font, 50, 770, "Attestazione di prova");
                line(cs, font, 50, 740, "Questo paragrafo statico precede il testo con il valore variabile.");
                float y = 700;
                for (String l : wrap(font, text, 300)) {
                    line(cs, font, 50, y, l);
                    y -= 13.5f;
                }
                cs.setStrokingColor(new Color(160, 160, 160));
                cs.setLineWidth(0.6f);
                cs.moveTo(50, 620);
                cs.lineTo(545, 620);
                cs.stroke();
                line(cs, font, 50, 600, "Questo paragrafo statico segue la linea di separazione.");
                cs.setNonStrokingColor(new Color(225, 235, 250));
                cs.addRect(450, 260, 95, 80);
                cs.fill();
                line(cs, font, 50, 40, "Pie di pagina fisso");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] paint(byte[] pdf, Spot spot) {
        return paint(pdf, spot, new Color(220, 0, 0), 0.4f, 4);
    }

    private static byte[] paint(byte[] pdf, Spot spot, Color color, float alpha, float size) {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDPage page = doc.getPage(0);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
                gs.setNonStrokingAlphaConstant(alpha);
                cs.setGraphicsStateParameters(gs);
                cs.setNonStrokingColor(color);
                cs.addRect(spot.x, spot.y, size, size);
                cs.fill();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> wrap(PDType1Font font, String text, float width) throws IOException {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : text.split(" ")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (!current.isEmpty() && font.getStringWidth(candidate) / 1000 * 10 > width) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        lines.add(current.toString());
        return lines;
    }

    private static void line(PDPageContentStream cs, PDType1Font font, float x, float y, String text) throws IOException {
        cs.setNonStrokingColor(Color.BLACK);
        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }
}
