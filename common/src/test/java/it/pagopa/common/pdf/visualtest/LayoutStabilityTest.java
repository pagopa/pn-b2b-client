package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Layout guarantee and band fallback on a paragraph that wraps around a value of variable width. */
class LayoutStabilityTest {

    private static final String IUN = "AAAA-BBBB-CCCC-202601-A-1";
    private static final String NARROW_IUN = "IJIL-LIJI-JIIL-201111-I-1";
    private static final String WIDE_IUN = "WMWM-MWMW-WMWM-202808-W-8";

    private static final PdfDocumentTemplate TEMPLATE = PdfDocumentTemplate.builder("WRAPPED")
            .field("IUN", FieldLocators.regex(Pattern.compile("[A-Z]{4}-[A-Z]{4}-[A-Z]{4}-\\d{6}-[A-Z]-\\d")),
                    FieldValidators.present(), MaskStrategy.INLINE_VALUE)
            .build();

    @Test
    void sameWidthValueKeepsTheLayoutAndIsComparedPixelByPixel() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(IUN, doc()), page("DDDD-EEEE-FFFF-202612-B-2", doc()), TEMPLATE);

        assertEquals(LayoutStability.Mode.GUARANTEED, report.layoutMode(), report::toString);
        assertTrue(report.isCompliant(), report::toString);
    }

    @Test
    void narrowerValueInsideTheMarginsKeepsTheLayoutAndMasksTheTail() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(IUN, doc()), page("AAAA-BBBB-AAAA-202601-A-1", doc()), TEMPLATE);

        assertEquals(LayoutStability.Mode.GUARANTEED, report.layoutMode(), report::toString);
        assertTrue(report.layoutNotes().stream().anyMatch(n -> n.contains("variazione")), report::toString);
        assertTrue(report.isCompliant(), report::toString);
    }

    @Test
    void valueOutsideTheMarginsFallsBackToBandsAndStillPasses() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(NARROW_IUN, doc()), page(WIDE_IUN, doc()), TEMPLATE);

        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, report.layoutMode(), report::toString);
        assertTrue(report.isCompliant(), report::toString);
    }

    @Test
    void staticDefectIsFoundAfterAReflow() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(NARROW_IUN, doc()),
                page(WIDE_IUN, doc().footerColor(new Color(204, 0, 0))), TEMPLATE);

        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, report.layoutMode(), report::toString);
        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void spacingDefectAfterTheValueIsFoundAfterAReflow() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(NARROW_IUN, doc()),
                page(WIDE_IUN, doc().afterGap(36f)), TEMPLATE);

        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void staticDefectIsFoundWhenTheLayoutIsStable() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(IUN, doc()),
                page("DDDD-EEEE-FFFF-202612-B-2", doc().footerColor(new Color(204, 0, 0))), TEMPLATE);

        assertEquals(LayoutStability.Mode.GUARANTEED, report.layoutMode(), report::toString);
        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void changedStaticWordAfterTheValueIsFound() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(IUN, doc()),
                page(IUN, doc().word("correttamente", "scorrettamente")), TEMPLATE);

        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void lookOfTheParagraphWithTheValueIsCheckedAfterAReflow() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(NARROW_IUN, doc()),
                page(WIDE_IUN, doc().bodyColor(new Color(0, 0, 200))), TEMPLATE);

        assertEquals(LayoutStability.Mode.NOT_GUARANTEED, report.layoutMode(), report::toString);
        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void lookOfTheWordsAfterTheValueIsCheckedWhenTheLayoutIsStable() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(IUN, doc()),
                page("AAAA-BBBB-AAAA-202601-A-1", doc().bodyColor(new Color(0, 0, 200))), TEMPLATE);

        assertEquals(LayoutStability.Mode.GUARANTEED, report.layoutMode(), report::toString);
        assertFalse(report.isCompliant(), report::toString);
    }

    @Test
    void bandComparisonReportsHowMuchOfThePageWasCompared() {
        PdfComplianceReport report = PdfComplianceChecker.check(page(NARROW_IUN, doc()), page(WIDE_IUN, doc()), TEMPLATE);

        assertTrue(report.layoutNotes().stream().anyMatch(n -> n.startsWith("copertura:") && n.contains("confrontato")),
                report::toString);
    }

    // -----------------------------------------------------------------------
    // Synthetic page: static intro, paragraph that wraps around the value, static closing, footer
    // -----------------------------------------------------------------------

    private static Spec doc() {
        return new Spec();
    }

    private static final class Spec {
        private Color footerColor = Color.BLACK;
        private Color bodyColor = Color.BLACK;
        private float afterGap = 30f;
        private String from = "correttamente";
        private String to = "correttamente";

        Spec footerColor(Color c) {
            footerColor = c;
            return this;
        }

        Spec bodyColor(Color c) {
            bodyColor = c;
            return this;
        }

        Spec afterGap(float g) {
            afterGap = g;
            return this;
        }

        Spec word(String f, String t) {
            from = f;
            to = t;
            return this;
        }
    }

    private static byte[] page(String iun, Spec spec) {
        String text = "Con riferimento alla notifica avente IUN " + iun + " si attesta che la comunicazione e stata inviata "
                + "regolarmente al destinatario indicato nel presente documento e che l'invio e avvenuto "
                + spec.from.replace("correttamente", spec.to) + " secondo le modalita previste dalla normativa vigente.";
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                line(cs, font, 50, 770, "Attestazione di prova", Color.BLACK);
                line(cs, font, 50, 740, "Questo paragrafo statico precede il testo con il valore variabile.", Color.BLACK);
                float y = 700;
                for (String l : wrap(font, text, 300)) {
                    line(cs, font, 50, y, l, spec.bodyColor);
                    y -= 13.5f;
                }
                y += 13.5f - spec.afterGap;
                line(cs, font, 50, y, "Questo paragrafo statico segue il testo con il valore variabile.", Color.BLACK);
                line(cs, font, 50, y - 20, "Un secondo paragrafo statico chiude il documento.", Color.BLACK);
                line(cs, font, 50, 40, "Piè di pagina fisso", spec.footerColor);
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

    private static void line(PDPageContentStream cs, PDType1Font font, float x, float y, String text, Color color)
            throws IOException {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(x, y);
        cs.showText(text.replace("è", "e"));
        cs.endText();
    }
}
