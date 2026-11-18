package it.pagopa.common.pdf.visualtest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Comparison for documents whose dynamic values changed the line breaks, so that the page no longer lines up with the
 * golden master. The static paragraphs are the anchors: consecutive static paragraphs form a rigid run, compared pixel
 * by pixel with the same run of the golden master moved by the vertical offset of its anchors. A paragraph that holds
 * an {@link MaskStrategy#INLINE_VALUE} value is only checked as text (done by {@link StaticTextComparator}) and for
 * its spacing: the distance from the neighbouring paragraphs and the line pitch must be the golden ones.
 *
 * <p>Not covered: the look of the paragraphs with a variable value, and graphics that lie between a run and the next
 * run when they do not move together.</p>
 */
final class PdfBandComparator {

    private static final Logger log = LoggerFactory.getLogger(PdfBandComparator.class);

    private static final int DPI = 300;
    private static final float SCALE = DPI / 72f;
    private static final float DY_TOLERANCE_PT = 0.15f;
    private static final float SPACING_TOLERANCE_PT = 0.3f;
    private static final float EDGE_ZONE = 0.12f;
    private static final int CHANNEL_THRESHOLD = 40;
    private static final int MIN_ALLOWED_PIXELS = 8;
    private static final double ALLOWED_FRACTION = 0.00001;
    private static final int MARGIN_PX = 2;
    private static final float FRAME_PT = 14f;
    private static final float WORD_PAD_PT = 1.5f;
    private static final int MIN_ALLOWED_WORD_PIXELS = 16;

    private PdfBandComparator() {
    }

    private record Run(int page, int firstEntry, int lastEntry, float dy) {
    }

    /** Only the words of the paragraphs with a variable value, for pages compared whole. */
    static PdfVisualComparator.VisualDiffResult compareWordsOnly(byte[] expectedPdf, byte[] actualPdf,
                                                                  ExtractedPdfText expected, ExtractedPdfText actual,
                                                                  PdfDocumentTemplate template,
                                                                  List<ExclusionArea> expectedMasks,
                                                                  List<ExclusionArea> actualMasks, Path diffOutputPath) {
        return compare(expectedPdf, actualPdf, expected, actual, template, expectedMasks, actualMasks, diffOutputPath, false);
    }

    static PdfVisualComparator.VisualDiffResult compare(byte[] expectedPdf, byte[] actualPdf,
                                                         ExtractedPdfText expected, ExtractedPdfText actual,
                                                         PdfDocumentTemplate template,
                                                         List<ExclusionArea> expectedMasks,
                                                         List<ExclusionArea> actualMasks, Path diffOutputPath) {
        return compare(expectedPdf, actualPdf, expected, actual, template, expectedMasks, actualMasks, diffOutputPath, true);
    }

    private static PdfVisualComparator.VisualDiffResult compare(byte[] expectedPdf, byte[] actualPdf,
                                                                 ExtractedPdfText expected, ExtractedPdfText actual,
                                                                 PdfDocumentTemplate template,
                                                                 List<ExclusionArea> expectedMasks,
                                                                 List<ExclusionArea> actualMasks, Path diffOutputPath,
                                                                 boolean withRuns) {
        List<String> problems = new ArrayList<>();
        List<String> unverified = new ArrayList<>();
        List<LayoutModel.Pair> entries = LayoutModel.align(LayoutModel.paragraphs(expected, template),
                LayoutModel.paragraphs(actual, template));
        Set<TextFragment> expectedValues = LayoutStability.inlineFragments(expected, template);
        Set<TextFragment> actualValues = LayoutStability.inlineFragments(actual, template);
        try (PDDocument expectedDoc = Loader.loadPDF(expectedPdf); PDDocument actualDoc = Loader.loadPDF(actualPdf)) {
            if (expectedDoc.getNumberOfPages() != actualDoc.getNumberOfPages()) {
                return PdfVisualComparator.VisualDiffResult.failure("Numero pagine diverso – atteso: "
                        + expectedDoc.getNumberOfPages() + ", trovato: " + actualDoc.getNumberOfPages(), null);
            }
            PDFRenderer expectedRenderer = new PDFRenderer(expectedDoc);
            PDFRenderer actualRenderer = new PDFRenderer(actualDoc);
            List<BufferedImage> failures = new ArrayList<>();
            List<String> coverage = new ArrayList<>();
            int runCount = 0;
            for (int page = 0; page < expectedDoc.getNumberOfPages(); page++) {
                float pageWidth = expectedDoc.getPage(page).getCropBox().getWidth();
                float pageHeight = expectedDoc.getPage(page).getCropBox().getHeight();
                final int pageIndex = page;
                BufferedImage golden = render(expectedRenderer, page, 0f, 0f, pageWidth, pageHeight);
                Map<String, BufferedImage> moved = new HashMap<>();
                Pixels pixels = new Pixels(golden.getWidth(), golden.getHeight());
                List<Run> runs = withRuns ? runsOfPage(entries, page, pageHeight, problems) : List.of();
                runCount += runs.size();
                if (withRuns) {
                    edgeBands(entries, page, golden, moved, actualRenderer, pageWidth, pageHeight, expectedMasks,
                            actualMasks, pixels, problems, failures);
                }
                for (Run run : runs) {
                    BufferedImage image = moved.computeIfAbsent("0:" + Math.round(run.dy() * 100),
                            k -> render(actualRenderer, pageIndex, 0f, run.dy(), pageWidth, pageHeight));
                    BufferedImage failure = compareRun(run, entries, golden, image, pageHeight, expectedMasks,
                            actualMasks, pixels, problems);
                    if (failure != null) {
                        failures.add(failure);
                    }
                }
                compareWords(entries, page, expectedValues, actualValues, golden, moved, actualRenderer, pageWidth,
                        pageHeight, expectedMasks, actualMasks, pixels, problems, unverified);
                coverage.add("pagina " + (page + 1) + ": " + pixels.summary(golden));
            }
            String coverageText = String.join("; ", coverage) + (unverified.isEmpty() ? ""
                    : "; parole non confrontabili: " + String.join(", ", unverified));
            if (problems.isEmpty()) {
                log.debug("PdfBandComparator: {} bande identiche", runCount);
                return PdfVisualComparator.VisualDiffResult.success(expectedDoc.getNumberOfPages()).withCoverage(coverageText);
            }
            Path saved = writeDiff(failures, diffOutputPath);
            return PdfVisualComparator.VisualDiffResult.failure("Differenze nel confronto per bande: "
                    + String.join("; ", problems), saved).withCoverage(coverageText);
        } catch (IOException e) {
            return PdfVisualComparator.VisualDiffResult.failure("Errore I/O nel confronto per bande: " + e.getMessage(), null);
        }
    }

    /** Which pixels of a page were compared, masked on purpose, or left alone. */
    private static final class Pixels {
        final int width;
        final BitSet compared;
        final BitSet masked;

        Pixels(int width, int height) {
            this.width = width;
            this.compared = new BitSet(width * height);
            this.masked = new BitSet(width * height);
        }

        String summary(BufferedImage golden) {
            long ink = 0;
            long comparedInk = 0;
            long maskedInk = 0;
            for (int y = 0; y < golden.getHeight(); y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = golden.getRGB(x, y);
                    if (((rgb >> 16) & 0xFF) > 245 && ((rgb >> 8) & 0xFF) > 245 && (rgb & 0xFF) > 245) {
                        continue;
                    }
                    ink++;
                    int i = y * width + x;
                    if (compared.get(i)) {
                        comparedInk++;
                    } else if (masked.get(i)) {
                        maskedInk++;
                    }
                }
            }
            if (ink == 0) {
                return "pagina vuota";
            }
            return String.format(Locale.ROOT, "%.1f%% dell'inchiostro del golden confrontato, %.1f%% mascherato, %.1f%% non verificato",
                    100.0 * comparedInk / ink, 100.0 * maskedInk / ink, 100.0 * (ink - comparedInk - maskedInk) / ink);
        }
    }

    /** What lies above the first and below the last paragraph is fixed to the page: compared as it is when that paragraph is variable. */
    private static void edgeBands(List<LayoutModel.Pair> entries, int page, BufferedImage golden,
                                  Map<String, BufferedImage> moved, PDFRenderer actualRenderer, float pageWidth,
                                  float pageHeight, List<ExclusionArea> expectedMasks, List<ExclusionArea> actualMasks,
                                  Pixels pixels, List<String> problems, List<BufferedImage> failures) {
        List<LayoutModel.Pair> onPage = entries.stream()
                .filter(e -> e.expected() != null && e.expected().page() == page).toList();
        if (onPage.isEmpty()) {
            return;
        }
        BufferedImage fixed = moved.computeIfAbsent("0:0", k -> render(actualRenderer, page, 0f, 0f, pageWidth, pageHeight));
        for (LayoutModel.Pair pair : onPage) {
            if (pair.matched() && flexible(pair)) {
                float top = Math.max(pair.expected().first().top(), pair.actual().first().top()) + 1f;
                float bottom = Math.min(pair.expected().last().bottom(), pair.actual().last().bottom()) - 1f;
                if (top > bottom) {
                    BufferedImage f = compareRegion(page, top, bottom, 0f,
                            "la fascia accanto a \"" + shorten(pair.expected().identity()) + "\"", entries, golden, fixed,
                            pageHeight, expectedMasks, actualMasks, pixels, problems);
                    if (f != null) {
                        failures.add(f);
                    }
                }
            }
        }
        LayoutModel.Pair first = onPage.get(0);
        LayoutModel.Pair last = onPage.get(onPage.size() - 1);
        if (first.matched() && flexible(first)) {
            BufferedImage f = compareRegion(page, pageHeight, first.expected().first().top() + 1f, 0f,
                    "la fascia sopra \"" + shorten(first.expected().identity()) + "\"", entries, golden, fixed,
                    pageHeight, expectedMasks, actualMasks, pixels, problems);
            if (f != null) {
                failures.add(f);
            }
        }
        if (last.matched() && flexible(last)) {
            BufferedImage f = compareRegion(page, last.expected().last().bottom() - 1f, 0f, 0f,
                    "la fascia sotto \"" + shorten(last.expected().identity()) + "\"", entries, golden, fixed,
                    pageHeight, expectedMasks, actualMasks, pixels, problems);
            if (f != null) {
                failures.add(f);
            }
        }
    }

    private record Word(TextFragment expected, TextFragment actual) {
    }

    /**
     * The static words of the paragraphs with a variable value, one by one: each word of the actual page is brought
     * onto the golden one with its own offset and compared pixel by pixel, so font, weight and colour are checked too.
     */
    private static void compareWords(List<LayoutModel.Pair> entries, int page, Set<TextFragment> expectedValues,
                                     Set<TextFragment> actualValues, BufferedImage golden,
                                     Map<String, BufferedImage> moved, PDFRenderer actualRenderer, float pageWidth,
                                     float pageHeight, List<ExclusionArea> expectedMasks,
                                     List<ExclusionArea> actualMasks, Pixels pixels, List<String> problems,
                                     List<String> unverified) {
        Map<String, List<Word>> groups = new java.util.LinkedHashMap<>();
        for (LayoutModel.Pair pair : entries) {
            if (!pair.matched() || pair.expected().page() != page || !flexible(pair)) {
                continue;
            }
            List<TextFragment> e = words(pair.expected(), expectedValues);
            List<TextFragment> a = words(pair.actual(), actualValues);
            if (e.size() != a.size()) {
                unverified.add("\"" + shorten(pair.expected().identity()) + "\"");
                continue;
            }
            checkWordSpacing(pair, expectedValues, actualValues, page, problems);
            for (int i = 0; i < e.size(); i++) {
                float dx = a.get(i).x() - e.get(i).x();
                float dy = a.get(i).y() - e.get(i).y();
                groups.computeIfAbsent(Math.round(dx * 100) + ":" + Math.round(dy * 100), k -> new ArrayList<>())
                        .add(new Word(e.get(i), a.get(i)));
            }
        }
        int reported = 0;
        int width = golden.getWidth();
        for (Map.Entry<String, List<Word>> group : groups.entrySet()) {
            Word sample = group.getValue().get(0);
            float dx = sample.actual().x() - sample.expected().x();
            float dy = sample.actual().y() - sample.expected().y();
            BufferedImage image = moved.computeIfAbsent(group.getKey(),
                    k -> render(actualRenderer, page, -dx, dy, pageWidth, pageHeight));
            for (Word word : group.getValue()) {
                TextFragment w = word.expected();
                int rowStart = clamp(Math.round((pageHeight - (w.yTop() + WORD_PAD_PT)) * SCALE), golden.getHeight());
                int rowEnd = clamp(Math.round((pageHeight - (w.y() - WORD_PAD_PT)) * SCALE), golden.getHeight());
                int x1 = clamp(Math.round((w.x() - WORD_PAD_PT) * SCALE), width);
                int x2 = clamp(Math.round((w.xEnd() + WORD_PAD_PT) * SCALE), width);
                BitSet masked = new BitSet((rowEnd - rowStart) * width);
                for (ExclusionArea area : expectedMasks) {
                    mask(masked, area, 0f, 0f, page, rowStart, rowEnd, width, pageHeight);
                }
                for (ExclusionArea area : actualMasks) {
                    mask(masked, area, dx, dy, page, rowStart, rowEnd, width, pageHeight);
                }
                long diffs = 0;
                for (int y = rowStart; y < rowEnd; y++) {
                    for (int x = x1; x < x2; x++) {
                        int index = y * width + x;
                        if (masked.get((y - rowStart) * width + x)) {
                            pixels.masked.set(index);
                        } else {
                            pixels.compared.set(index);
                            if (differs(golden.getRGB(x, y), image.getRGB(x, y))) {
                                diffs++;
                            }
                        }
                    }
                }
                if (diffs > MIN_ALLOWED_WORD_PIXELS && reported++ < 5) {
                    problems.add("pagina " + (page + 1) + ": la parola \"" + w.text() + "\" ha " + diffs + " pixel diversi");
                }
            }
        }
    }

    private static List<TextFragment> words(LayoutModel.Paragraph paragraph, Set<TextFragment> values) {
        List<TextFragment> words = new ArrayList<>();
        for (LayoutModel.Line line : paragraph.lines()) {
            for (TextFragment f : line.fragments()) {
                if (!values.contains(f)) {
                    words.add(f);
                }
            }
        }
        return words;
    }

    /** Two static words side by side (no value between them) keep the golden gap. */
    private static void checkWordSpacing(LayoutModel.Pair pair, Set<TextFragment> expectedValues,
                                         Set<TextFragment> actualValues, int page, List<String> problems) {
        List<TextFragment> e = new ArrayList<>();
        List<TextFragment> a = new ArrayList<>();
        for (LayoutModel.Line line : pair.expected().lines()) {
            e.addAll(line.fragments());
        }
        for (LayoutModel.Line line : pair.actual().lines()) {
            a.addAll(line.fragments());
        }
        if (e.size() != a.size()) {
            return;
        }
        for (int i = 1; i < e.size(); i++) {
            boolean sameLine = Math.abs(e.get(i).y() - e.get(i - 1).y()) < 2f && Math.abs(a.get(i).y() - a.get(i - 1).y()) < 2f;
            if (!sameLine || expectedValues.contains(e.get(i)) || expectedValues.contains(e.get(i - 1))
                    || actualValues.contains(a.get(i)) || actualValues.contains(a.get(i - 1))) {
                continue;
            }
            float expectedGap = e.get(i).x() - e.get(i - 1).xEnd();
            float actualGap = a.get(i).x() - a.get(i - 1).xEnd();
            if (Math.abs(expectedGap - actualGap) > SPACING_TOLERANCE_PT) {
                problems.add("pagina " + (page + 1) + ": spazio diverso prima di \"" + e.get(i).text() + "\" ("
                        + fmt(expectedGap) + " → " + fmt(actualGap) + "pt)");
                return;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Runs and spacing
    // -----------------------------------------------------------------------

    private static List<Run> runsOfPage(List<LayoutModel.Pair> entries, int page, float pageHeight, List<String> problems) {
        List<Run> runs = new ArrayList<>();
        int runStart = -1;
        float runDy = 0;
        for (int k = 0; k < entries.size(); k++) {
            LayoutModel.Pair e = entries.get(k);
            if (pageOf(e) != page) {
                continue;
            }
            if (!e.matched()) {
                problems.add("pagina " + (page + 1) + ": paragrafo " + (e.expected() != null ? "atteso" : "trovato")
                        + " senza corrispondente \"" + shorten((e.expected() != null ? e.expected() : e.actual()).identity()) + "\"");
                runStart = close(runs, page, runStart, k - 1, runDy);
                continue;
            }
            if (flexible(e)) {
                runStart = close(runs, page, runStart, k - 1, runDy);
                checkInlineParagraph(entries, k, page, pageHeight, problems);
                continue;
            }
            float dy = e.actual().first().y() - e.expected().first().y();
            if (runStart >= 0 && Math.abs(dy - runDy) > DY_TOLERANCE_PT) {
                if (!anchoredToEdge(e.expected(), dy, pageHeight)) {
                    problems.add("pagina " + (page + 1) + ": spaziatura diversa prima di \""
                            + shorten(e.expected().identity()) + "\" (" + fmt(dy - runDy) + "pt)");
                }
                runStart = close(runs, page, runStart, k - 1, runDy);
            }
            if (runStart < 0) {
                runStart = k;
                runDy = dy;
            }
        }
        close(runs, page, runStart, lastIndexOfPage(entries, page), runDy);
        return runs;
    }

    private static int close(List<Run> runs, int page, int start, int end, float dy) {
        if (start >= 0) {
            runs.add(new Run(page, start, end, dy));
        }
        return -1;
    }

    private static void checkInlineParagraph(List<LayoutModel.Pair> entries, int k, int page, float pageHeight,
                                             List<String> problems) {
        LayoutModel.Pair e = entries.get(k);
        LayoutModel.Pair before = k > 0 && entries.get(k - 1).matched() && pageOf(entries.get(k - 1)) == page
                ? entries.get(k - 1) : null;
        LayoutModel.Pair after = k + 1 < entries.size() && entries.get(k + 1).matched() && pageOf(entries.get(k + 1)) == page
                ? entries.get(k + 1) : null;
        if (before != null && isStatic(before) && !inEdgeZone(before.expected(), pageHeight)) {
            compareSpacing(problems, page, "prima di", e,
                    before.expected().last().y() - e.expected().first().y(),
                    before.actual().last().y() - e.actual().first().y());
        }
        if (after != null && isStatic(after) && !inEdgeZone(after.expected(), pageHeight)) {
            compareSpacing(problems, page, "dopo", e,
                    e.expected().last().y() - after.expected().first().y(),
                    e.actual().last().y() - after.actual().first().y());
        }
        List<LayoutModel.Line> golden = e.expected().lines();
        List<LayoutModel.Line> found = e.actual().lines();
        if (golden.size() > 1) {
            float pitch = golden.get(0).y() - golden.get(1).y();
            for (int i = 1; i < found.size(); i++) {
                if (Math.abs(found.get(i - 1).y() - found.get(i).y() - pitch) > SPACING_TOLERANCE_PT) {
                    problems.add("pagina " + (page + 1) + ": interlinea diversa in \"" + shorten(e.expected().identity()) + "\"");
                    break;
                }
            }
        }
    }

    private static void compareSpacing(List<String> problems, int page, String where, LayoutModel.Pair paragraph,
                                       float expectedDistance, float actualDistance) {
        if (Math.abs(expectedDistance - actualDistance) > SPACING_TOLERANCE_PT) {
            problems.add("pagina " + (page + 1) + ": distanza " + where + " \"" + shorten(paragraph.expected().identity())
                    + "\" " + fmt(expectedDistance) + " → " + fmt(actualDistance) + "pt");
        }
    }

    private static boolean isStatic(LayoutModel.Pair p) {
        return !flexible(p);
    }

    /** Holds a variable value, or moved sideways (centred or aligned to a neighbour that changed): checked as text only. */
    private static boolean flexible(LayoutModel.Pair p) {
        return p.expected().inline() || p.actual().inline()
                || Math.abs(p.actual().first().left() - p.expected().first().left()) > DY_TOLERANCE_PT;
    }

    private static boolean anchoredToEdge(LayoutModel.Paragraph golden, float dy, float pageHeight) {
        return Math.abs(dy) <= DY_TOLERANCE_PT && inEdgeZone(golden, pageHeight);
    }

    private static boolean inEdgeZone(LayoutModel.Paragraph p, float pageHeight) {
        return p.last().bottom() < pageHeight * EDGE_ZONE || p.first().top() > pageHeight * (1 - EDGE_ZONE);
    }

    // -----------------------------------------------------------------------
    // Pixels
    // -----------------------------------------------------------------------

    private static BufferedImage compareRun(Run run, List<LayoutModel.Pair> entries, BufferedImage golden,
                                            BufferedImage moved, float pageHeight, List<ExclusionArea> expectedMasks,
                                            List<ExclusionArea> actualMasks, Pixels pixels, List<String> problems) {
        String label = "la banda da \"" + shorten(entries.get(run.firstEntry()).expected().identity()) + "\"";
        return compareRegion(run.page(), regionTop(run, entries, pageHeight), regionBottom(run, entries), run.dy(),
                label, entries, golden, moved, pageHeight, expectedMasks, actualMasks, pixels, problems);
    }

    private static BufferedImage compareRegion(int page, float yTop, float yBottom, float dy, String label,
                                               List<LayoutModel.Pair> entries, BufferedImage golden,
                                               BufferedImage moved, float pageHeight, List<ExclusionArea> expectedMasks,
                                               List<ExclusionArea> actualMasks, Pixels pixels, List<String> problems) {
        int rowStart = clamp(Math.round((pageHeight - yTop) * SCALE), golden.getHeight());
        int rowEnd = clamp(Math.round((pageHeight - yBottom) * SCALE), golden.getHeight());
        if (rowEnd <= rowStart) {
            return null;
        }
        int width = golden.getWidth();
        BitSet masked = new BitSet((rowEnd - rowStart) * width);
        for (ExclusionArea area : expectedMasks) {
            mask(masked, area, 0f, page, rowStart, rowEnd, width, pageHeight);
        }
        for (ExclusionArea area : actualMasks) {
            mask(masked, area, dy, page, rowStart, rowEnd, width, pageHeight);
        }
        for (LayoutModel.Pair pair : entries) {
            if (pair.matched() && pair.expected().page() == page && flexible(pair)) {
                mask(masked, around(pair.expected(), framePad(pair)), 0f, page, rowStart, rowEnd, width, pageHeight);
                mask(masked, around(pair.actual(), framePad(pair)), dy, page, rowStart, rowEnd, width, pageHeight);
            }
        }
        long diffs = 0;
        long compared = 0;
        BufferedImage overlay = new BufferedImage(width, rowEnd - rowStart, BufferedImage.TYPE_INT_RGB);
        for (int y = rowStart; y < rowEnd; y++) {
            for (int x = 0; x < width; x++) {
                int g = golden.getRGB(x, y);
                if (masked.get((y - rowStart) * width + x)) {
                    overlay.setRGB(x, y - rowStart, 0xDDDDFF);
                    pixels.masked.set(y * width + x);
                    continue;
                }
                pixels.compared.set(y * width + x);
                compared++;
                if (differs(g, moved.getRGB(x, y))) {
                    diffs++;
                    overlay.setRGB(x, y - rowStart, 0xFF0000);
                } else {
                    overlay.setRGB(x, y - rowStart, g);
                }
            }
        }
        long allowed = Math.max(MIN_ALLOWED_PIXELS, Math.round(compared * ALLOWED_FRACTION));
        if (diffs <= allowed) {
            return null;
        }
        problems.add("pagina " + (page + 1) + ": " + label + " ha " + diffs + " pixel diversi (consentiti " + allowed + ")");
        return overlay;
    }

    private static float regionTop(Run run, List<LayoutModel.Pair> entries, float pageHeight) {
        LayoutModel.Paragraph first = entries.get(run.firstEntry()).expected();
        LayoutModel.Pair previous = neighbour(entries, run.firstEntry(), -1, run.page());
        if (previous == null) {
            return pageHeight;
        }
        return previous.matched() && flexible(previous) ? previous.expected().last().bottom() - 1f : first.first().top() + 0.5f;
    }

    private static float regionBottom(Run run, List<LayoutModel.Pair> entries) {
        LayoutModel.Paragraph last = entries.get(run.lastEntry()).expected();
        LayoutModel.Pair next = neighbour(entries, run.lastEntry(), 1, run.page());
        if (next == null) {
            return 0f;
        }
        return next.matched() && flexible(next) ? next.expected().first().top() + 1f : last.last().bottom() - 0.5f;
    }

    /** Nearest expected paragraph of the page before ({@code -1}) or after ({@code 1}) an entry; inline-ness is the golden one. */
    private static LayoutModel.Pair neighbour(List<LayoutModel.Pair> entries, int from, int step, int page) {
        for (int k = from + step; k >= 0 && k < entries.size(); k += step) {
            LayoutModel.Pair e = entries.get(k);
            if (e.expected() != null && e.expected().page() == page) {
                return e;
            }
        }
        return null;
    }

    /** The paragraph and the frame that may grow with it. */
    /** Only a paragraph that moved sideways drags a frame with it: a variable value alone does not. */
    private static float framePad(LayoutModel.Pair pair) {
        boolean moved = Math.abs(pair.actual().first().left() - pair.expected().first().left()) > DY_TOLERANCE_PT;
        return moved ? FRAME_PT : 2f;
    }

    private static ExclusionArea around(LayoutModel.Paragraph p, float pad) {
        float left = (float) p.lines().stream().mapToDouble(LayoutModel.Line::left).min().orElse(0);
        float right = (float) p.lines().stream().mapToDouble(LayoutModel.Line::right).max().orElse(0);
        return new ExclusionArea(p.page(), Math.max(0, left - pad), Math.max(0, p.last().bottom() - pad),
                right - left + 2 * pad, p.first().top() - p.last().bottom() + 2 * pad);
    }

    private static void mask(BitSet masked, ExclusionArea area, float dy, int page, int rowStart, int rowEnd,
                             int width, float pageHeight) {
        mask(masked, area, 0f, dy, page, rowStart, rowEnd, width, pageHeight);
    }

    private static void mask(BitSet masked, ExclusionArea area, float dx, float dy, int page, int rowStart, int rowEnd,
                             int width, float pageHeight) {
        if (area.pageIndex() != page) {
            return;
        }
        float yInGolden = area.y() - dy;
        float xInGolden = area.x() - dx;
        int x1 = Math.max(0, Math.round(xInGolden * SCALE) - MARGIN_PX);
        int x2 = Math.min(width, Math.round((xInGolden + area.width()) * SCALE) + MARGIN_PX);
        int y1 = Math.max(rowStart, Math.round((pageHeight - (yInGolden + area.height())) * SCALE) - MARGIN_PX);
        int y2 = Math.min(rowEnd, Math.round((pageHeight - yInGolden) * SCALE) + MARGIN_PX);
        for (int y = y1; y < y2; y++) {
            masked.set((y - rowStart) * width + x1, (y - rowStart) * width + Math.max(x1, x2));
        }
    }

    private static boolean differs(int a, int b) {
        if (a == b) {
            return false;
        }
        if (squiggleAgainstPaper(a, b) || squiggleAgainstPaper(b, a)) {
            return false;
        }
        return Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF)) > CHANNEL_THRESHOLD
                || Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF)) > CHANNEL_THRESHOLD
                || Math.abs((a & 0xFF) - (b & 0xFF)) > CHANNEL_THRESHOLD;
    }

    /**
     * The Template Engine draws a red wavy underline (spell check) as a pattern anchored to the page, not to the text:
     * moving the text moves the phase of the wave. Red against white or red is not a difference.
     */
    private static boolean squiggleAgainstPaper(int red, int other) {
        boolean isRed = ((red >> 16) & 0xFF) > 190 && ((red >> 8) & 0xFF) < 130 && (red & 0xFF) < 130;
        boolean paperOrRed = (((other >> 16) & 0xFF) > 200 && ((other >> 8) & 0xFF) > 200 && (other & 0xFF) > 200)
                || (((other >> 16) & 0xFF) > 190 && ((other >> 8) & 0xFF) < 130 && (other & 0xFF) < 130);
        return isRed && paperOrRed;
    }

    /** The page rendered with its content moved down by {@code dyPt}, in vector space so that the glyphs rasterise as in the golden master. */
    private static BufferedImage render(PDFRenderer renderer, int page, float dxPt, float dyPt, float pageWidthPt, float pageHeightPt) {
        try {
            BufferedImage image = new BufferedImage(Math.round(pageWidthPt * SCALE), Math.round(pageHeightPt * SCALE),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setBackground(Color.WHITE);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(dxPt * SCALE, dyPt * SCALE);
            renderer.renderPageToGraphics(page, g, SCALE);
            g.dispose();
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Rendering della pagina " + (page + 1) + " non riuscito", e);
        }
    }

    private static Path writeDiff(List<BufferedImage> failures, Path diffOutputPath) {
        if (diffOutputPath == null || failures.isEmpty()) {
            return null;
        }
        int width = failures.get(0).getWidth();
        int height = failures.stream().mapToInt(BufferedImage::getHeight).sum();
        BufferedImage sheet = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = sheet.createGraphics();
        int y = 0;
        for (BufferedImage f : failures) {
            g.drawImage(f, 0, y, null);
            y += f.getHeight();
        }
        g.dispose();
        Path target = diffOutputPath.resolveSibling(diffOutputPath.getFileName().toString().replaceAll("\\.pdf$", "") + "-bands.png");
        try {
            ImageIO.write(sheet, "png", target.toFile());
            return target;
        } catch (IOException e) {
            log.warn("PdfBandComparator: impossibile salvare {}: {}", target, e.getMessage());
            return null;
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static int pageOf(LayoutModel.Pair e) {
        return e.expected() != null ? e.expected().page() : e.actual().page();
    }

    private static int lastIndexOfPage(List<LayoutModel.Pair> entries, int page) {
        int last = -1;
        for (int k = 0; k < entries.size(); k++) {
            if (pageOf(entries.get(k)) == page) {
                last = k;
            }
        }
        return last;
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(max, value));
    }

    private static String shorten(String text) {
        return text.length() > 40 ? text.substring(0, 40) + "…" : text;
    }

    private static String fmt(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
