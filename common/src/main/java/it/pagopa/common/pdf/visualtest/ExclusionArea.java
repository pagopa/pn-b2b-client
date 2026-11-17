package it.pagopa.common.pdf.visualtest;

import java.util.Collections;
import java.util.List;

/**
 * Area rettangolare da escludere dal confronto visivo pixel-level.
 *
 * <p>Le coordinate sono espresse in <em>punti PDF</em> (origine basso-sinistra)
 * e vengono convertite in coordinate di rendering pixel da {@link PdfVisualComparator}.</p>
 *
 * @param pageIndex   indice di pagina 0-based
 * @param x           bordo sinistro
 * @param y           bordo inferiore
 * @param width       larghezza
 * @param height      altezza
 */
public record ExclusionArea(int pageIndex, float x, float y, float width, float height) {

    /**
     * Crea una {@code ExclusionArea} a partire da un {@link TextFragment} applicando
     * la {@link MaskStrategy} specificata.
     *
     * @param fragment   frammento sorgente
     * @param strategy   strategia di mascheramento
     * @param pageWidthPt  larghezza della pagina in punti (necessaria per FULL_LINE)
     */
    public static ExclusionArea from(TextFragment fragment, MaskStrategy strategy, float pageWidthPt) {
        return areas(fragment, strategy, pageWidthPt).get(0);
    }

    /** All the areas of a strategy: {@link MaskStrategy#TO_LINE_END_AND_NEXT_LINE} needs two. */
    public static List<ExclusionArea> areas(TextFragment fragment, MaskStrategy strategy, float pageWidthPt) {
        final float paddingPt = 3.0f;
        if (strategy == MaskStrategy.TO_LINE_END_AND_NEXT_LINE) {
            float nextLineGap = fragment.height() * 1.3f;
            return List.of(
                    single(fragment, MaskStrategy.TO_LINE_END, pageWidthPt, paddingPt),
                    new ExclusionArea(fragment.pageIndex(), 0,
                            Math.max(0, fragment.y() - nextLineGap - paddingPt),
                            pageWidthPt,
                            fragment.height() + (paddingPt * 2)));
        }
        return List.of(single(fragment, strategy, pageWidthPt, paddingPt));
    }

    private static ExclusionArea single(TextFragment fragment, MaskStrategy strategy, float pageWidthPt, float paddingPt) {
        return switch (strategy) {
            case TIGHT_BOX -> new ExclusionArea(
                    fragment.pageIndex(),
                    Math.max(0, fragment.x() - paddingPt),
                    Math.max(0, fragment.y() - paddingPt),
                    fragment.width() + (paddingPt * 2),
                    fragment.height() + (paddingPt * 2));

            case FULL_LINE -> new ExclusionArea(
                    fragment.pageIndex(),
                    0,
                    Math.max(0, fragment.y() - 2 - paddingPt),
                    pageWidthPt,
                    fragment.height() + 4 + (paddingPt * 2));

            case TO_LINE_END, TO_LINE_END_AND_NEXT_LINE -> new ExclusionArea(
                    fragment.pageIndex(),
                    Math.max(0, fragment.x() - paddingPt),
                    Math.max(0, fragment.y() - paddingPt),
                    Math.max(0, pageWidthPt - fragment.x() + paddingPt),
                    fragment.height() + (paddingPt * 2));

            case FULL_TABLE_CELL -> new ExclusionArea(
                    fragment.pageIndex(),
                    Math.max(0, fragment.x() - 6 - paddingPt),
                    Math.max(0, fragment.y() - 4 - paddingPt),
                    fragment.width() + 12 + (paddingPt * 2),
                    fragment.height() + 8 + (paddingPt * 2));
        };
    }

    /**
     * Raccoglie tutte le {@code ExclusionArea} da una lista di frammenti,
     * applicando la stessa strategia a tutti.
     */
    public static List<ExclusionArea> fromAll(
            List<TextFragment> fragments, MaskStrategy strategy, float pageWidthPt) {
        return fragments.stream()
                .map(f -> ExclusionArea.from(f, strategy, pageWidthPt))
                .toList();
    }
}
