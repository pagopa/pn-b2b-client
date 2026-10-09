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
    // 1b. LabelProximityOccurrenceLocator
    // -----------------------------------------------------------------------

    /**
     * Variante di {@link #labelProximity(String, float, float)} che considera solo
     * l'N-esima occorrenza dell'etichetta nel documento (1-based), nell'ordine in cui
     * compare nel testo estratto.
     *
     * <p>Utile per documenti in cui un blocco di campi si ripete più volte (es. un blocco
     * di dati per ciascun destinatario in una notifica multidestinatario): passando
     * l'indice del destinatario come {@code occurrenceIndex} si recupera il valore del
     * campo relativo a quel solo destinatario.</p>
     *
     * <p>Il valore è tutto il testo che segue l'etichetta nella colonna a destra (stessa
     * riga e righe successive), anche su più righe, fino a quando il testo rientra nella
     * colonna dell'etichetta: quel rientro segnala l'inizio di una nuova etichetta.
     * Nessuna distanza va calibrata: il confine è dato dalla posizione dell'etichetta
     * stessa, quindi la stessa chiamata funziona su documenti con margini diversi.</p>
     *
     * <p>A differenza di {@link #labelProximity(String, float, float)}, il confronto con
     * {@code labelText} richiede una corrispondenza esatta (ignorando maiuscole/minuscole e
     * spazi iniziali/finali) dell'intero frammento, non una semplice sottostringa: questo
     * evita falsi positivi quando il testo dell'etichetta compare per caso all'interno di
     * un paragrafo discorsivo del documento.</p>
     *
     * @param labelText       testo esatto dell'etichetta, così come appare nel PDF
     * @param occurrenceIndex indice 1-based dell'occorrenza dell'etichetta da considerare
     */
    public static FieldLocator labelProximityOccurrence(String labelText, int occurrenceIndex) {
        if (occurrenceIndex < 1) {
            throw new IllegalArgumentException("occurrenceIndex deve essere >= 1 (1-based)");
        }
        return allFragments -> {
            int occurrence = 0;

            for (int i = 0; i < allFragments.size(); i++) {
                TextFragment candidate = allFragments.get(i);
                if (!candidate.text().trim().equalsIgnoreCase(labelText.trim())) continue;

                occurrence++;
                if (occurrence != occurrenceIndex) continue;

                return valueColumnAfter(candidate, allFragments, i);
            }
            return List.of();
        };
    }

    /**
     * Scorre i frammenti successivi all'etichetta (in ordine di lettura) raccogliendo
     * quelli nella colonna del valore (a destra dell'etichetta), anche su più righe.
     * Si ferma non appena un frammento torna nella colonna dell'etichetta (nuova
     * etichetta) o finisce la pagina.
     */
    private static List<TextFragment> valueColumnAfter(TextFragment label, List<TextFragment> allFragments, int labelPosition) {
        List<TextFragment> results = new ArrayList<>();
        float labelXEnd = label.xEnd();

        for (int i = labelPosition + 1; i < allFragments.size(); i++) {
            TextFragment other = allFragments.get(i);
            if (other.pageIndex() != label.pageIndex()) break;
            if (other.x() < labelXEnd) break; // rientrato nella colonna dell'etichetta: fine del valore

            results.add(other);
        }
        return results;
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
