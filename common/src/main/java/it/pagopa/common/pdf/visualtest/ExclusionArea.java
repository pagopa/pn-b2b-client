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
        return switch (strategy) {
            case TIGHT_BOX -> new ExclusionArea(
                    fragment.pageIndex(),
                    fragment.x(),
                    fragment.y(),
                    fragment.width(),
                    fragment.height());

            case FULL_LINE -> new ExclusionArea(
                    fragment.pageIndex(),
                    0,
                    fragment.y() - 2,     // piccolo padding verticale
                    pageWidthPt,
                    fragment.height() + 4);

            case FULL_TABLE_CELL -> new ExclusionArea(
                    fragment.pageIndex(),
                    Math.max(0, fragment.x() - 6),
                    Math.max(0, fragment.y() - 4),
                    fragment.width() + 12,
                    fragment.height() + 8);
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
