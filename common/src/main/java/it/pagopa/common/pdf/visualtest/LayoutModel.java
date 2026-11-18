package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * Lines and paragraphs rebuilt from the word fragments of a PDF: the geometry that tells whether a dynamic value can
 * reflow the page.
 */
final class LayoutModel {

    /** Words closer than this (points) belong to the same text run; a table cell boundary is far wider. */
    static final float SEGMENT_GAP_PT = 10f;
    private static final float COLUMN_GAP_PT = 30f;
    private static final float LINE_TOLERANCE_PT = 2f;
    private static final float SAME_LEFT_PT = 1.5f;
    private static final float LEADING_TO_HEIGHT = 1.6f;

    private LayoutModel() {
    }

    /** One line of text; {@code top}/{@code bottom} are PDF coordinates (y grows upwards). */
    record Line(int page, float y, float top, float bottom, float left, float right, List<TextFragment> fragments) {

        float height() {
            return top - bottom;
        }
    }

    /**
     * Consecutive lines with the same left edge and the line spacing of running text.
     * {@code identity} is the text with every dynamic value replaced by its field name and line breaks ignored.
     */
    record Paragraph(int page, List<Line> lines, String identity, boolean inline) {

        Line first() {
            return lines.get(0);
        }

        Line last() {
            return lines.get(lines.size() - 1);
        }
    }

    /** A paragraph of the expected document and the one of the actual document that has the same identity. */
    record Pair(Paragraph expected, Paragraph actual) {

        boolean matched() {
            return expected != null && actual != null;
        }
    }

    static List<Paragraph> paragraphs(ExtractedPdfText text, PdfDocumentTemplate template) {
        Set<TextFragment> inline = Collections.newSetFromMap(new IdentityHashMap<>());
        java.util.Map<TextFragment, String> placeholders = new IdentityHashMap<>();
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            for (TextFragment f : def.locator().locate(text.allFragments())) {
                placeholders.putIfAbsent(f, "{" + def.fieldName() + "}");
                if (def.maskStrategy() == MaskStrategy.INLINE_VALUE) {
                    inline.add(f);
                }
            }
        }
        List<Paragraph> result = new ArrayList<>();
        for (PageText page : text.pages()) {
            List<List<Line>> open = new ArrayList<>();
            for (List<TextFragment> row : page.groupIntoLines(LINE_TOLERANCE_PT)) {
                for (List<TextFragment> part : splitColumns(row)) {
                    Line line = line(page.pageIndex(), part);
                    List<Line> target = null;
                    for (List<Line> candidate : open) {
                        if (continues(candidate.get(candidate.size() - 1), line)) {
                            target = candidate;
                        }
                    }
                    if (target == null) {
                        target = new ArrayList<>();
                        open.add(target);
                    }
                    target.add(line);
                }
            }
            open.stream()
                    .map(lines -> paragraph(page.pageIndex(), lines, placeholders, inline))
                    .sorted(java.util.Comparator.comparingDouble((Paragraph p) -> -p.first().top())
                            .thenComparingDouble(p -> p.first().left()))
                    .forEach(result::add);
        }
        return result;
    }

    /** Words of one row separated by a wide gap belong to different columns (side by side boxes, label and value). */
    private static List<List<TextFragment>> splitColumns(List<TextFragment> row) {
        List<List<TextFragment>> parts = new ArrayList<>();
        List<TextFragment> current = new ArrayList<>();
        for (TextFragment f : row) {
            if (!current.isEmpty() && f.x() - current.get(current.size() - 1).xEnd() >= COLUMN_GAP_PT) {
                parts.add(current);
                current = new ArrayList<>();
            }
            current.add(f);
        }
        parts.add(current);
        return parts;
    }

    /** Paragraphs of the two documents paired by identity (longest common subsequence), page by page. */
    static List<Pair> align(List<Paragraph> expected, List<Paragraph> actual) {
        int n = expected.size();
        int m = actual.size();
        int[][] lcs = new int[n + 1][m + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = m - 1; j >= 0; j--) {
                lcs[i][j] = same(expected.get(i), actual.get(j))
                        ? lcs[i + 1][j + 1] + 1
                        : Math.max(lcs[i + 1][j], lcs[i][j + 1]);
            }
        }
        List<Pair> pairs = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < n || j < m) {
            if (i < n && j < m && same(expected.get(i), actual.get(j))) {
                pairs.add(new Pair(expected.get(i++), actual.get(j++)));
            } else if (j >= m || (i < n && lcs[i + 1][j] >= lcs[i][j + 1])) {
                pairs.add(new Pair(expected.get(i++), null));
            } else {
                pairs.add(new Pair(null, actual.get(j++)));
            }
        }
        return pairs;
    }

    private static boolean same(Paragraph e, Paragraph a) {
        return e.page() == a.page() && e.identity().equals(a.identity());
    }

    private static Line line(int page, List<TextFragment> row) {
        float top = -Float.MAX_VALUE;
        float bottom = Float.MAX_VALUE;
        for (TextFragment f : row) {
            top = Math.max(top, f.yTop());
            bottom = Math.min(bottom, f.y());
        }
        TextFragment first = row.get(0);
        TextFragment last = row.get(row.size() - 1);
        float right = row.stream().map(TextFragment::xEnd).max(Float::compare).orElse(last.xEnd());
        return new Line(page, first.y(), top, bottom, first.x(), right, row);
    }

    private static boolean continues(Line previous, Line next) {
        float pitch = previous.y() - next.y();
        return pitch > 0
                && pitch <= LEADING_TO_HEIGHT * Math.max(previous.height(), next.height())
                && Math.abs(previous.left() - next.left()) <= SAME_LEFT_PT;
    }

    private static Paragraph paragraph(int page, List<Line> lines, java.util.Map<TextFragment, String> placeholders,
                                       Set<TextFragment> inline) {
        StringBuilder identity = new StringBuilder();
        String lastPlaceholder = null;
        boolean hasInline = false;
        for (Line line : lines) {
            for (TextFragment f : line.fragments()) {
                String placeholder = placeholders.get(f);
                hasInline |= inline.contains(f);
                if (placeholder == null) {
                    identity.append(f.text().replaceAll("\\s+", ""));
                    lastPlaceholder = null;
                } else if (!placeholder.equals(lastPlaceholder)) {
                    identity.append(placeholder);
                    lastPlaceholder = placeholder;
                }
            }
        }
        return new Paragraph(page, List.copyOf(lines), identity.toString(), hasInline);
    }
}
