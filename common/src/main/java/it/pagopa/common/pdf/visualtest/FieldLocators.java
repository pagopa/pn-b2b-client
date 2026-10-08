package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementazioni standard dell'interfaccia {@link FieldLocator}.
 *
 * <p>Tutte le implementazioni sono agnostiche rispetto al dominio di business.</p>
 */
public final class FieldLocators {

    private FieldLocators() {
    }

    // -----------------------------------------------------------------------
    // 1. LabelProximityLocator
    // -----------------------------------------------------------------------

    /**
     * Localizza il campo cercando un'etichetta testuale e restituendo i frammenti
     * immediatamente alla destra o sotto di essa nella stessa area di prossimità.
     *
     * @param labelText      testo dell'etichetta (ricerca case-insensitive, sottostringa)
     * @param maxOffsetPtX   distanza massima orizzontale (punti PDF) tra etichetta e valore
     * @param maxOffsetPtY   distanza massima verticale (punti PDF) tra etichetta e valore
     */
    public static FieldLocator labelProximity(String labelText, float maxOffsetPtX, float maxOffsetPtY) {
        return allFragments -> {
            List<TextFragment> results = new ArrayList<>();
            String lowerLabel = labelText.toLowerCase(java.util.Locale.ROOT);

            for (int i = 0; i < allFragments.size(); i++) {
                TextFragment candidate = allFragments.get(i);
                if (candidate.text().toLowerCase(java.util.Locale.ROOT).contains(lowerLabel)) {
                    // Cerca frammenti a destra o sotto l'etichetta trovata
                    float labelXEnd = candidate.xEnd();
                    float labelY    = candidate.y();

                    for (TextFragment other : allFragments) {
                        if (other == candidate) continue;
                        if (other.pageIndex() != candidate.pageIndex()) continue;

                        boolean toTheRight = other.x() >= labelXEnd
                                && other.x() <= labelXEnd + maxOffsetPtX
                                && Math.abs(other.y() - labelY) <= maxOffsetPtY;

                        boolean below = other.x() <= candidate.xEnd() + maxOffsetPtX
                                && other.y() < labelY
                                && labelY - other.y() <= maxOffsetPtY;

                        if (toTheRight || below) {
                            results.add(other);
                        }
                    }
                }
            }
            return List.copyOf(results);
        };
    }

    // -----------------------------------------------------------------------
    // 2. RegexLocator
    // -----------------------------------------------------------------------

    /**
     * Localizza frammenti il cui testo fa match con la regex fornita.
     *
     * @param pattern regex compilata
     */
    public static FieldLocator regex(Pattern pattern) {
        return allFragments -> allFragments.stream()
                .filter(f -> pattern.matcher(f.text()).find())
                .toList();
    }

    // -----------------------------------------------------------------------
    // 3. FixedRegionLocator
    // -----------------------------------------------------------------------

    /**
     * Localizza frammenti che cadono all'interno di una regione rettangolare fissa
     * in coordinate PDF (punti, origine basso-sinistra).
     *
     * @param pageIndex indice di pagina 0-based
     * @param x         bordo sinistro della regione
     * @param y         bordo inferiore della regione
     * @param width     larghezza della regione
     * @param height    altezza della regione
     */
    public static FieldLocator fixedRegion(int pageIndex, float x, float y, float width, float height) {
        float xEnd = x + width;
        float yTop = y + height;
        return allFragments -> allFragments.stream()
                .filter(f -> f.pageIndex() == pageIndex
                        && f.x() >= x && f.xEnd() <= xEnd
                        && f.y() >= y && f.yTop() <= yTop)
                .toList();
    }
}
