package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Tells whether the dynamic values of an actual PDF leave the layout of the golden master untouched.
 *
 * <p>For every line of the golden master that carries an {@link MaskStrategy#INLINE_VALUE} value the margins come from
 * the golden geometry alone: the line can grow until the longest line of its column (right limit of the text box, a
 * lower bound) and can shrink until the first word of the next line would fit (the tightest wrapped line of the
 * column bounds the box from above). At run time the line width variation, the sum of the width differences of its
 * values, is compared with them: inside the margins no word changes line, so everything but the values (and the words
 * on the same line after them, which only move sideways) is identical pixel by pixel.</p>
 */
public final class LayoutStability {

    /** Differences below this (points) are rendering noise: a shift of the tail of the line is not expected. */
    private static final float NO_SHIFT_PT = 0.02f;
    /** Tolerance on the position of the words that follow a value. */
    private static final float TAIL_TOLERANCE_PT = 0.6f;
    private static final float SAFETY_PT = 0.5f;
    private static final float TAIL_MASK_PADDING_PT = 3f;

    private LayoutStability() {
    }

    public enum Mode {
        /** The template has no inline value: the whole page is compared. */
        NOT_APPLICABLE,
        /** Layout proved identical: pixel-perfect comparison, values masked. */
        GUARANTEED,
        /** Some value falls outside the margins (or the structure differs): comparison by bands. */
        NOT_GUARANTEED
    }

    /**
     * @param mode      outcome
     * @param tailMasks areas to mask, in the expected and in the actual page, where a value moved the words after it
     * @param notes     one line per checked text line, for the log and the report
     */
    public record Result(Mode mode, List<ExclusionArea> tailMasks, List<String> notes) {
    }

    /** Margins of one line, in points: how much it can grow and shrink without changing a line break. */
    record Margins(float grow, float shrink) {
    }

    public static Result evaluate(ExtractedPdfText expected, ExtractedPdfText actual, PdfDocumentTemplate template) {
        boolean hasInline = template.fieldDefinitions().stream()
                .anyMatch(d -> d.maskStrategy() == MaskStrategy.INLINE_VALUE);
        if (!hasInline) {
            return new Result(Mode.NOT_APPLICABLE, List.of(), List.of());
        }
        Set<TextFragment> expectedInline = inlineFragments(expected, template);
        Set<TextFragment> actualInline = inlineFragments(actual, template);
        List<LayoutModel.Paragraph> expectedParagraphs = LayoutModel.paragraphs(expected, template);
        List<LayoutModel.Paragraph> actualParagraphs = LayoutModel.paragraphs(actual, template);
        Map<String, float[]> columns = columnLimits(expectedParagraphs);

        List<String> notes = new ArrayList<>();
        List<ExclusionArea> tailMasks = new ArrayList<>();
        boolean guaranteed = true;
        for (LayoutModel.Pair pair : LayoutModel.align(expectedParagraphs, actualParagraphs)) {
            if (!pair.matched()) {
                notes.add("paragrafo senza corrispondente: " + preview(pair));
                guaranteed = false;
                continue;
            }
            if (!pair.expected().inline() && !pair.actual().inline()) {
                continue;
            }
            guaranteed &= checkParagraph(pair, expectedInline, actualInline, columns, notes, tailMasks);
        }
        return new Result(guaranteed ? Mode.GUARANTEED : Mode.NOT_GUARANTEED, List.copyOf(tailMasks), List.copyOf(notes));
    }

    private static boolean checkParagraph(LayoutModel.Pair pair, Set<TextFragment> expectedInline,
                                          Set<TextFragment> actualInline, Map<String, float[]> columns,
                                          List<String> notes, List<ExclusionArea> tailMasks) {
        List<LayoutModel.Line> expectedLines = pair.expected().lines();
        List<LayoutModel.Line> actualLines = pair.actual().lines();
        if (expectedLines.size() != actualLines.size()) {
            notes.add("righe del paragrafo " + expectedLines.size() + " → " + actualLines.size() + ": "
                    + preview(pair));
            return false;
        }
        boolean ok = true;
        for (int i = 0; i < expectedLines.size(); i++) {
            LayoutModel.Line e = expectedLines.get(i);
            LayoutModel.Line a = actualLines.get(i);
            List<TextFragment> eValues = e.fragments().stream().filter(expectedInline::contains).toList();
            List<TextFragment> aValues = a.fragments().stream().filter(actualInline::contains).toList();
            if (e.fragments().size() != a.fragments().size() || eValues.size() != aValues.size()
                    || !sameStaticWords(e, a, expectedInline)) {
                notes.add("p" + (e.page() + 1) + " y=" + fmt(e.y()) + ": le parole della riga cambiano");
                ok = false;
                continue;
            }
            if (eValues.isEmpty()) {
                continue;
            }
            float delta = 0;
            for (int k = 0; k < eValues.size(); k++) {
                delta += aValues.get(k).width() - eValues.get(k).width();
            }
            Margins margins = margins(e, i + 1 < expectedLines.size() ? expectedLines.get(i + 1) : null, columns,
                    expectedInline);
            boolean within = delta <= margins.grow() && -delta <= margins.shrink();
            notes.add("p" + (e.page() + 1) + " y=" + fmt(e.y()) + ": variazione " + fmt(delta) + "pt, margini [-"
                    + fmt(margins.shrink()) + ", +" + fmt(margins.grow()) + "] " + (within ? "ok" : "FUORI"));
            if (!within) {
                ok = false;
                continue;
            }
            if (Math.abs(delta) > NO_SHIFT_PT) {
                if (!tailFollowsValues(e, a, expectedInline, actualInline)) {
                    notes.add("p" + (e.page() + 1) + " y=" + fmt(e.y()) + ": le parole dopo il valore non seguono lo spostamento");
                    ok = false;
                } else {
                    addTailMasks(e, a, expectedInline, actualInline, tailMasks);
                }
            }
        }
        return ok;
    }

    // -----------------------------------------------------------------------
    // Margins from the golden geometry
    // -----------------------------------------------------------------------

    /** Per page and left edge: {lower bound, upper bound} of the right limit of the text box. */
    private static Map<String, float[]> columnLimits(List<LayoutModel.Paragraph> paragraphs) {
        Map<String, float[]> limits = new HashMap<>();
        for (LayoutModel.Paragraph p : paragraphs) {
            for (LayoutModel.Line line : p.lines()) {
                limits.computeIfAbsent(columnKey(line), k -> new float[]{0f, Float.MAX_VALUE});
                float[] limit = limits.get(columnKey(line));
                limit[0] = Math.max(limit[0], line.right());
            }
        }
        for (LayoutModel.Paragraph p : paragraphs) {
            for (int i = 0; i + 1 < p.lines().size(); i++) {
                LayoutModel.Line line = p.lines().get(i);
                float[] limit = limits.get(columnKey(line));
                float bound = wrapBound(line, p.lines().get(i + 1));
                if (bound > limit[0]) {
                    limit[1] = Math.min(limit[1], bound);
                }
            }
        }
        return limits;
    }

    /** Where the line would end if the first word of the next line were on it: the box is narrower than that. */
    private static float wrapBound(LayoutModel.Line line, LayoutModel.Line next) {
        return line.right() + spaceWidth(line) + next.fragments().get(0).width();
    }

    private static float spaceWidth(LayoutModel.Line line) {
        List<Float> gaps = new ArrayList<>();
        for (int i = 1; i < line.fragments().size(); i++) {
            float gap = line.fragments().get(i).x() - line.fragments().get(i - 1).xEnd();
            if (gap > 0 && gap < LayoutModel.SEGMENT_GAP_PT) {
                gaps.add(gap);
            }
        }
        if (gaps.isEmpty()) {
            return 2.5f;
        }
        Collections.sort(gaps);
        return gaps.get(gaps.size() / 2);
    }

    private static String columnKey(LayoutModel.Line line) {
        return line.page() + ":" + Math.round(line.left());
    }

    private static Margins margins(LayoutModel.Line line, LayoutModel.Line next, Map<String, float[]> columns,
                                   Set<TextFragment> inline) {
        float[] limit = columns.get(columnKey(line));
        float grow = limit[0] - line.right();
        float nextSegmentGap = gapAfterFirstValue(line, inline);
        if (nextSegmentGap >= 0) {
            grow = Math.min(grow, nextSegmentGap);
        }
        float shrink = Float.MAX_VALUE;
        if (next != null && Math.abs(line.left() - next.left()) <= 1.5f && line.y() - next.y() > 0) {
            float bound = wrapBound(line, next);
            if (bound > limit[0]) {
                shrink = Math.max(0f, bound - limit[1] - SAFETY_PT);
            }
        }
        return new Margins(Math.max(0f, grow - SAFETY_PT), shrink);
    }

    /** Room between the segment of the line that holds a value and the next one (table cells); -1 if it is the last. */
    private static float gapAfterFirstValue(LayoutModel.Line line, Set<TextFragment> inline) {
        List<TextFragment> fragments = line.fragments();
        for (int i = 0; i < fragments.size(); i++) {
            if (!inline.contains(fragments.get(i))) {
                continue;
            }
            for (int j = i + 1; j < fragments.size(); j++) {
                float gap = fragments.get(j).x() - fragments.get(j - 1).xEnd();
                if (gap >= LayoutModel.SEGMENT_GAP_PT) {
                    return Math.max(0f, gap - 2f);
                }
            }
            return -1f;
        }
        return -1f;
    }

    // -----------------------------------------------------------------------
    // Words after a value
    // -----------------------------------------------------------------------

    private static boolean sameStaticWords(LayoutModel.Line e, LayoutModel.Line a, Set<TextFragment> expectedInline) {
        for (int i = 0; i < e.fragments().size(); i++) {
            TextFragment ef = e.fragments().get(i);
            if (!expectedInline.contains(ef) && !ef.text().equals(a.fragments().get(i).text())) {
                return false;
            }
        }
        return true;
    }

    /** Each static word is where it was, moved only by the width differences of the values before it. */
    private static boolean tailFollowsValues(LayoutModel.Line e, LayoutModel.Line a, Set<TextFragment> expectedInline,
                                             Set<TextFragment> actualInline) {
        float shift = 0;
        for (int i = 0; i < e.fragments().size(); i++) {
            TextFragment ef = e.fragments().get(i);
            TextFragment af = a.fragments().get(i);
            if (Math.abs(af.x() - (ef.x() + shift)) > TAIL_TOLERANCE_PT || Math.abs(af.y() - ef.y()) > TAIL_TOLERANCE_PT) {
                return false;
            }
            if (expectedInline.contains(ef)) {
                shift += af.width() - ef.width();
            }
        }
        return true;
    }

    private static void addTailMasks(LayoutModel.Line e, LayoutModel.Line a, Set<TextFragment> expectedInline,
                                     Set<TextFragment> actualInline, List<ExclusionArea> masks) {
        int firstValue = 0;
        while (!expectedInline.contains(e.fragments().get(firstValue))) {
            firstValue++;
        }
        for (LayoutModel.Line line : List.of(e, a)) {
            List<TextFragment> tail = line.fragments().subList(firstValue, line.fragments().size());
            float x1 = (float) tail.stream().mapToDouble(TextFragment::x).min().orElse(0) - TAIL_MASK_PADDING_PT;
            float x2 = (float) tail.stream().mapToDouble(TextFragment::xEnd).max().orElse(0) + TAIL_MASK_PADDING_PT;
            float y1 = (float) tail.stream().mapToDouble(TextFragment::y).min().orElse(0) - TAIL_MASK_PADDING_PT;
            float y2 = (float) tail.stream().mapToDouble(TextFragment::yTop).max().orElse(0) + TAIL_MASK_PADDING_PT;
            masks.add(new ExclusionArea(line.page(), Math.max(0, x1), Math.max(0, y1), x2 - Math.max(0, x1), y2 - Math.max(0, y1)));
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    static Set<TextFragment> inlineFragments(ExtractedPdfText text, PdfDocumentTemplate template) {
        Set<TextFragment> inline = Collections.newSetFromMap(new IdentityHashMap<>());
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            if (def.maskStrategy() == MaskStrategy.INLINE_VALUE) {
                inline.addAll(def.locator().locate(text.allFragments()));
            }
        }
        return inline;
    }

    private static String preview(LayoutModel.Pair pair) {
        String identity = pair.expected() != null ? pair.expected().identity() : pair.actual().identity();
        String side = pair.expected() != null ? "atteso" : "trovato";
        return side + " \"" + identity.substring(0, Math.min(60, identity.length())) + "\"";
    }

    private static String fmt(float value) {
        return value > 1e6f ? "∞" : String.format(Locale.ROOT, "%.2f", value);
    }
}
