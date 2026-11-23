package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sensitivity guard for what the visual comparison adds to the static text check. Each {@link Mutation} is a realistic
 * template regression (bold left open, fallback font, extra line break, ...) that keeps the extracted text identical:
 * the text check must not see it, so the report must fail because of the visual comparison, both when the layout is
 * guaranteed (pixel-perfect) and when it falls back to bands. Text regressions (typos, unreplaced placeholders,
 * literal entities) are the text check's job and are covered by a single smoke case.
 */
class MutationDetectionTest {

    private static final String IUN = "AAAA-BBBB-CCCC-202601-A-1";
    private static final String SAME_WIDTH_IUN = "DDDD-EEEE-FFFF-202612-B-2";
    private static final String NARROW_IUN = "IJIL-LIJI-JIIL-201111-I-1";
    private static final String WIDE_IUN = "WMWM-MWMW-WMWM-202808-W-8";

    private static final PDFont REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDFont TIMES = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);

    private static final PdfDocumentTemplate TEMPLATE = PdfDocumentTemplate.builder("MUTATION")
            .field("IUN", FieldLocators.regex(Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d")),
                    FieldValidators.present(), MaskStrategy.INLINE_VALUE)
            .build();

    /** Template regressions that leave the extracted text unchanged. */
    enum Mutation {
        NONE,
        /** One word of a static sentence in bold ({@code <b>statico</b>}). */
        BOLD_WORD,
        /** A {@code <b>} never closed: everything after it is bold. */
        UNCLOSED_BOLD_TAG,
        /** The CSS font is not found and the renderer falls back to a serif font. */
        FALLBACK_FONT,
        /** Font size 10 pt → 10.5 pt on one paragraph. */
        LARGER_FONT_SIZE,
        /** An extra {@code <br>} before a paragraph: what follows moves down one line. */
        EXTRA_LINE_BREAK,
        /** Line height of the value paragraph 13.5 pt → 15 pt (missing CSS rule). */
        WIDER_LINE_SPACING,
        /** {@code text-align} changed from left to center. */
        CENTERED_PARAGRAPH,
        /** Container narrower: same words, different line breaks. */
        NARROWER_CONTAINER,
        /** Two words glued together ("staticoprecede"): the text check joins words without spaces. */
        MISSING_SPACE,
        /** Two static words glued in the last line of the paragraph that holds the variable value (the golden has them apart). */
        MISSING_SPACE_IN_VALUE_PARAGRAPH,
        /** The variable value (masked in the pixel comparison) rendered in bold. */
        VALUE_IN_BOLD,
        /** The variable value rendered in another font family. */
        VALUE_IN_SERIF,
        /** A word painted white on white: still in the text layer, gone from the page. */
        INVISIBLE_WORD,
        /** The separator rule is not drawn. */
        MISSING_RULE,
        /** The tinted box is not drawn. */
        MISSING_BOX,
        /** Text regression: a typo in a static word (smoke case for the text check). */
        TYPO
    }

    // -----------------------------------------------------------------------
    // Tests
    // -----------------------------------------------------------------------

    static Stream<Arguments> visualMutations() {
        List<Arguments> out = new ArrayList<>();
        for (Mutation m : Mutation.values()) {
            if (m == Mutation.NONE || m == Mutation.TYPO || m == Mutation.VALUE_IN_BOLD || m == Mutation.VALUE_IN_SERIF) {
                continue;
            }
            out.add(Arguments.of(LayoutStability.Mode.GUARANTEED, IUN, SAME_WIDTH_IUN, m));
            if (m != Mutation.CENTERED_PARAGRAPH) {
                out.add(Arguments.of(LayoutStability.Mode.NOT_GUARANTEED, NARROW_IUN, WIDE_IUN, m));
            }
        }
        return out.stream();
    }

    /**
     * Known tolerance: with bands, a paragraph moved sideways is treated as flexible (it may be centred or aligned to
     * a neighbour holding a value) and is checked as text only, so a static paragraph that loses its alignment passes.
     */
    @Test
    void sidewaysMoveOfStaticParagraphIsToleratedInBands() {
        PdfComplianceReport mutated = PdfComplianceChecker.check(page(NARROW_IUN, Mutation.NONE),
                page(WIDE_IUN, Mutation.CENTERED_PARAGRAPH), TEMPLATE);
        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, mutated.layoutMode(), mutated::toString);
        assertTrue(mutated.isCompliant(), mutated::toString);
    }

    @ParameterizedTest(name = "{0} {3}")
    @MethodSource("visualMutations")
    void visualOnlyRegressionIsDetected(LayoutStability.Mode mode, String goldenIun, String actualIun, Mutation mutation) {
        byte[] expected = page(goldenIun, Mutation.NONE);
        PdfComplianceReport clean = PdfComplianceChecker.check(expected, page(actualIun, Mutation.NONE), TEMPLATE);
        assertEquals(mode, clean.layoutMode(), clean::toString);
        assertTrue(clean.isCompliant(), () -> "falso positivo senza difetti: " + clean);

        Path diffPath = diffPath(mode, mutation);
        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, page(actualIun, mutation), TEMPLATE, diffPath);
        exportPng(diffPath);
        assertTrue(mutated.textDifferences().isEmpty(),
                () -> mutation + " doveva lasciare invariato il testo: " + mutated.textDifferences());
        assertFalse(mutated.isCompliant(), () -> mutation + " non rilevato in " + mode + ": " + mutated);
        assertFalse(mutated.visualResult().equal(), () -> mutation + " non rilevato dal confronto visivo: " + mutated);
    }

    /** Where to save the diff of each mutation: only when {@code -Dmutation.diff.dir=<folder>} is given, otherwise nothing is written. */
    private static Path diffPath(LayoutStability.Mode mode, Mutation mutation) {
        String dir = System.getProperty("mutation.diff.dir");
        if (dir == null || dir.isBlank()) {
            return null;
        }
        try {
            Path folder = Files.createDirectories(Path.of(dir));
            return folder.resolve(mode.name().toLowerCase() + "-" + mutation.name().toLowerCase() + ".pdf");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** The guaranteed mode writes a PDF diff: render its first page as PNG next to it (bands already write a PNG). */
    private static void exportPng(Path diffPdf) {
        if (diffPdf == null || !Files.exists(diffPdf)) {
            return;
        }
        try (PDDocument doc = Loader.loadPDF(diffPdf.toFile())) {
            BufferedImage image = new PDFRenderer(doc).renderImageWithDPI(0, 110);
            ImageIO.write(image, "png", diffPdf.resolveSibling(diffPdf.getFileName().toString().replaceAll("\\.pdf$", "") + ".png").toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Stream<Arguments> valueStyleCases() {
        List<Arguments> out = new ArrayList<>();
        for (Mutation m : List.of(Mutation.VALUE_IN_BOLD, Mutation.VALUE_IN_SERIF)) {
            out.add(Arguments.of(IUN, SAME_WIDTH_IUN, m));
            out.add(Arguments.of(NARROW_IUN, WIDE_IUN, m));
        }
        return out.stream();
    }

    /**
     * The area of the variable value is masked, so the pixels cannot show that the value is bold or in another font: the
     * style (font, size, rendering mode, colours) of each value is compared with the golden master instead.
     */
    @ParameterizedTest(name = "{0} {2}")
    @MethodSource("valueStyleCases")
    void valueStyleChangeIsDetected(String goldenIun, String actualIun, Mutation mutation) {
        byte[] expected = page(goldenIun, Mutation.NONE);
        PdfComplianceReport clean = PdfComplianceChecker.check(expected, page(actualIun, Mutation.NONE), TEMPLATE);
        assertTrue(clean.isCompliant(), () -> "falso positivo senza difetti: " + clean);

        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, page(actualIun, mutation), TEMPLATE);
        assertTrue(mutated.textDifferences().isEmpty(), () -> mutation + " doveva lasciare invariato il testo");
        assertFalse(mutated.styleDifferences().isEmpty(), () -> mutation + " non rilevato nello stile del valore: " + mutated);
        assertFalse(mutated.isCompliant(), mutated::toString);
    }

    @Test
    void textRegressionIsReportedByTextCheck() {
        PdfComplianceReport mutated = PdfComplianceChecker.check(page(IUN, Mutation.NONE), page(SAME_WIDTH_IUN, Mutation.TYPO), TEMPLATE);
        assertFalse(mutated.textDifferences().isEmpty(), mutated::toString);
        assertFalse(mutated.isCompliant(), mutated::toString);
    }

    /** Where a tiny defect is painted, in PDF points (origin bottom-left), on the synthetic page. */
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

    static Stream<Arguments> smallDefects() {
        List<Arguments> out = new ArrayList<>();
        for (Spot spot : Spot.values()) {
            out.add(Arguments.of(LayoutStability.Mode.GUARANTEED, IUN, SAME_WIDTH_IUN, spot));
            out.add(Arguments.of(LayoutStability.Mode.NOT_GUARANTEED, NARROW_IUN, WIDE_IUN, spot));
        }
        return out.stream();
    }

    /**
     * Minimum sensitivity guard: a 4 pt square, red at 40% opacity, must be reported wherever it lands (static word,
     * word of the value paragraph, graphics, blank area), with the layout guaranteed and with the band fallback.
     */
    @ParameterizedTest(name = "{0} {3}")
    @MethodSource("smallDefects")
    void smallDefectIsDetected(LayoutStability.Mode mode, String goldenIun, String actualIun, Spot spot) {
        byte[] expected = page(goldenIun, Mutation.NONE);
        byte[] actual = page(actualIun, Mutation.NONE);
        PdfComplianceReport clean = PdfComplianceChecker.check(expected, actual, TEMPLATE);
        assertEquals(mode, clean.layoutMode(), clean::toString);
        assertTrue(clean.isCompliant(), () -> "falso positivo senza difetti: " + clean);

        PdfComplianceReport mutated = PdfComplianceChecker.check(expected,
                paint(actual, spot.x, spot.y, new Color(220, 0, 0), 0.4f, 4), TEMPLATE);
        assertFalse(mutated.isCompliant(), () -> "difetto " + spot + " non rilevato in " + mode + ": " + mutated);
    }

    /** Threshold guard: a 6% darkening of a flat area is a defect also when the layout falls back to bands. */
    @ParameterizedTest(name = "{0}")
    @EnumSource(value = FlatSpot.class)
    void slightColourShiftOnFlatAreaIsDetectedInBands(FlatSpot spot) {
        byte[] expected = page(NARROW_IUN, Mutation.NONE);
        byte[] actual = darken(page(WIDE_IUN, Mutation.NONE), spot);
        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, actual, TEMPLATE);
        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, mutated.layoutMode(), mutated::toString);
        assertFalse(mutated.isCompliant(), () -> "viraggio su " + spot + " non rilevato: " + mutated);
    }

    /**
     * A red element is compared like any other colour: a change of shade inside the red family (the logo of a cover
     * page, a red warning) is a defect also when the layout falls back to bands.
     */
    @ParameterizedTest(name = "{0}")
    @EnumSource(value = FlatSpot.class)
    void shadeChangeOfRedElementIsDetectedInBands(FlatSpot spot) {
        byte[] expected = paint(page(NARROW_IUN, Mutation.NONE), spot.x, spot.y, new Color(220, 0, 0), 1f, 12);
        byte[] sameRed = paint(page(WIDE_IUN, Mutation.NONE), spot.x, spot.y, new Color(220, 0, 0), 1f, 12);
        PdfComplianceReport clean = PdfComplianceChecker.check(expected, sameRed, TEMPLATE);
        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, clean.layoutMode(), clean::toString);
        assertTrue(clean.isCompliant(), () -> "falso positivo senza difetti: " + clean);

        byte[] otherRed = paint(page(WIDE_IUN, Mutation.NONE), spot.x, spot.y, new Color(200, 110, 110), 1f, 12);
        PdfComplianceReport mutated = PdfComplianceChecker.check(expected, otherRed, TEMPLATE);
        assertFalse(mutated.isCompliant(), () -> "cambio di tonalità del rosso su " + spot + " non rilevato: " + mutated);
    }

    /**
     * A fixed area ({@code ignoreArea}, e.g. a QR code) is anchored to the page while the paragraph next to it moves
     * down one line because the value above wraps differently: the area must stay masked on the actual page too, so a
     * different QR code is not a difference.
     */
    @Test
    void fixedAreaNextToMovedParagraphIsMaskedInBands() throws IOException {
        float width = widthWhereWideIunAddsALine();
        // Same place on both pages: just below the golden value paragraph, beside the paragraph that follows it.
        float qrTop = 740 - 13.5f * wrap(REGULAR, String.format(QR_TEXT, NARROW_IUN), width).size() - 25;
        PdfDocumentTemplate template = PdfDocumentTemplate.builder("FIXED_AREA")
                .field("IUN", FieldLocators.regex(Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d")),
                        FieldValidators.present(), MaskStrategy.INLINE_VALUE)
                .ignoreArea(0, 46f, qrTop - 4 * QR_CELLS - 4, 4 * QR_CELLS + 8, 4 * QR_CELLS + 8)
                .build();
        byte[] expected = pageWithQr(NARROW_IUN, width, qrTop, 1);
        PdfComplianceReport sameQr = PdfComplianceChecker.check(expected, pageWithQr(WIDE_IUN, width, qrTop, 1), template);
        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, sameQr.layoutMode(), sameQr::toString);
        assertTrue(sameQr.isCompliant(), () -> "falso positivo con lo stesso QR: " + sameQr);

        PdfComplianceReport otherQr = PdfComplianceChecker.check(expected, pageWithQr(WIDE_IUN, width, qrTop, 2), template);
        assertTrue(otherQr.isCompliant(), () -> "il QR nell'area fissa doveva essere ignorato: " + otherQr);
    }

    private static final int QR_CELLS = 18;

    private static final String QR_TEXT = "Con riferimento alla notifica avente IUN %s si attesta che la comunicazione "
            + "e stata inviata regolarmente al destinatario indicato nel presente documento.";

    /** A container width where the wide IUN wraps the value paragraph on one more line than the narrow one. */
    private static float widthWhereWideIunAddsALine() {
        try {
            for (float w = 220; w <= 400; w += 1) {
                if (wrap(REGULAR, String.format(QR_TEXT, WIDE_IUN), w).size()
                        > wrap(REGULAR, String.format(QR_TEXT, NARROW_IUN), w).size()) {
                    return w;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new IllegalStateException("nessuna larghezza in cui lo IUN largo aggiunge una riga");
    }

    /** Value paragraph, then a static paragraph that follows it, and next to that a page-anchored "QR code". */
    private static byte[] pageWithQr(String iun, float width, float qrTop, long qrSeed) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                runs(cs, 50, 770, 10, new Run("Attestazione di prova", REGULAR));
                float y = 740;
                for (String l : wrap(REGULAR, String.format(QR_TEXT, iun), width)) {
                    runs(cs, 50, y, 10, new Run(l, REGULAR));
                    y -= 13.5f;
                }
                y -= 26.5f;
                for (String l : List.of("Inquadra il codice QR con la fotocamera del tuo dispositivo",
                        "oppure vai sul sito indicato e inserisci il codice della notifica",
                        "per consultare i documenti che ti riguardano")) {
                    runs(cs, 150, y, 10, new Run(l, REGULAR));
                    y -= 13.5f;
                }
                Random random = new Random(qrSeed);
                cs.setNonStrokingColor(Color.BLACK);
                for (int row = 0; row < QR_CELLS; row++) {
                    for (int col = 0; col < QR_CELLS; col++) {
                        if (random.nextBoolean()) {
                            cs.addRect(50 + col * 4f, qrTop - (row + 1) * 4f, 4f, 4f);
                        }
                    }
                }
                cs.fill();
                runs(cs, 50, 40, 10, new Run("Pie di pagina fisso", REGULAR));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Flat areas of the synthetic page, in PDF points (origin bottom-left). */
    enum FlatSpot {
        GRAPHIC_BOX(470, 300),
        BLANK_AREA(300, 450);

        final float x;
        final float y;

        FlatSpot(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    // -----------------------------------------------------------------------
    // Synthetic page: static text, a paragraph wrapping around the value, a thin grey rule, a tinted box
    // -----------------------------------------------------------------------

    private record Run(String text, PDFont font, Color color) {
        Run(String text, PDFont font) {
            this(text, font, Color.BLACK);
        }
    }

    private static byte[] page(String iun, Mutation m) {
        String text = "Con riferimento alla notifica avente IUN " + iun + " si attesta che la comunicazione e stata inviata "
                + "regolarmente al destinatario indicato nel presente documento e che l'invio e avvenuto "
                + "correttamente secondo le modalita previste dalla normativa vigente.";
        PDFont after = m == Mutation.UNCLOSED_BOLD_TAG ? BOLD : REGULAR;
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                runs(cs, 50, 770, 10, new Run("Attestazione di prova", REGULAR));

                List<Run> intro = switch (m) {
                    case BOLD_WORD -> List.of(new Run("Questo paragrafo ", REGULAR), new Run("statico", BOLD),
                            new Run(" precede il testo con il valore variabile.", REGULAR));
                    case UNCLOSED_BOLD_TAG -> List.of(new Run("Questo paragrafo ", REGULAR),
                            new Run("statico precede il testo con il valore variabile.", BOLD));
                    case MISSING_SPACE -> List.of(new Run("Questo paragrafo staticoprecede il testo con il valore variabile.", REGULAR));
                    case TYPO -> List.of(new Run("Questo paragrafo statco precede il testo con il valore variabile.", REGULAR));
                    default -> List.of(new Run("Questo paragrafo statico precede il testo con il valore variabile.", REGULAR));
                };
                runs(cs, 50, 740, 10, intro.toArray(Run[]::new));

                float leading = m == Mutation.WIDER_LINE_SPACING ? 15f : 13.5f;
                float width = m == Mutation.NARROWER_CONTAINER ? 260 : 300;
                float y = 700;
                List<String> wrapped = wrap(after, text, width);
                for (int i = 0; i < wrapped.size(); i++) {
                    String l = wrapped.get(i);
                    if (m == Mutation.MISSING_SPACE_IN_VALUE_PARAGRAPH && i == wrapped.size() - 1) {
                        l = l.replaceFirst(" ", "");
                    }
                    if ((m == Mutation.VALUE_IN_BOLD || m == Mutation.VALUE_IN_SERIF) && l.contains(iun)) {
                        PDFont valueFont = m == Mutation.VALUE_IN_BOLD ? BOLD : TIMES;
                        int at = l.indexOf(iun);
                        runs(cs, 50, y, 10, new Run(l.substring(0, at), after), new Run(iun, valueFont),
                                new Run(l.substring(at + iun.length()), after));
                    } else {
                        runs(cs, 50, y, 10, new Run(l, after));
                    }
                    y -= leading;
                }

                if (m != Mutation.MISSING_RULE) {
                    cs.setStrokingColor(new Color(160, 160, 160));
                    cs.setLineWidth(0.6f);
                    cs.moveTo(50, 620);
                    cs.lineTo(545, 620);
                    cs.stroke();
                }

                float shift = m == Mutation.EXTRA_LINE_BREAK ? 13.5f : 0;
                String outro = "Questo paragrafo statico segue la linea di separazione.";
                float outroSize = m == Mutation.LARGER_FONT_SIZE ? 10.5f : 10;
                PDFont outroFont = m == Mutation.FALLBACK_FONT ? TIMES : after;
                float outroX = m == Mutation.CENTERED_PARAGRAPH
                        ? (PDRectangle.A4.getWidth() - outroFont.getStringWidth(outro) / 1000 * outroSize) / 2 : 50;
                if (m == Mutation.INVISIBLE_WORD) {
                    runs(cs, outroX, 600 - shift, outroSize, new Run("Questo paragrafo statico segue la linea di ", outroFont),
                            new Run("separazione.", outroFont, Color.WHITE));
                } else {
                    runs(cs, outroX, 600 - shift, outroSize, new Run(outro, outroFont));
                }

                if (m != Mutation.MISSING_BOX) {
                    cs.setNonStrokingColor(new Color(225, 235, 250));
                    cs.addRect(450, 260 - shift, 95, 80);
                    cs.fill();
                }
                runs(cs, 50, 40, 10, new Run("Pie di pagina fisso", after));
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static byte[] darken(byte[] pdf, FlatSpot spot) {
        return paint(pdf, spot.x, spot.y, Color.BLACK, 0.06f, 8);
    }

    private static byte[] paint(byte[] pdf, float x, float y, Color color, float alpha, float size) {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDPage page = doc.getPage(0);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
                gs.setNonStrokingAlphaConstant(alpha);
                cs.setGraphicsStateParameters(gs);
                cs.setNonStrokingColor(color);
                cs.addRect(x, y, size, size);
                cs.fill();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<String> wrap(PDFont font, String text, float width) throws IOException {
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

    /** One line made of consecutive runs, each with its own font and colour. */
    private static void runs(PDPageContentStream cs, float x, float y, float size, Run... runs) throws IOException {
        float cursor = x;
        for (Run r : runs) {
            cs.setNonStrokingColor(r.color());
            cs.beginText();
            cs.setFont(r.font(), size);
            cs.newLineAtOffset(cursor, y);
            cs.showText(r.text());
            cs.endText();
            cursor += r.font().getStringWidth(r.text()) / 1000 * size;
        }
    }
}
