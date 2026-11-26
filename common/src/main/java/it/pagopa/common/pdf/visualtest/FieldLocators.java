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
     * @param labelText      testo dell'etichetta (ricerca case-insensitive)
     * @param maxOffsetPtX   distanza massima orizzontale (punti PDF) tra etichetta e valore
     * @param maxOffsetPtY   distanza massima verticale (punti PDF) tra etichetta e valore
     */
    public static FieldLocator labelProximity(String labelText, float maxOffsetPtX, float maxOffsetPtY) {
        return allFragments -> {
            List<TextFragment> results = new ArrayList<>();
            String[] labelWords = labelText.trim().toLowerCase(java.util.Locale.ROOT).split("\\s+");
            if (labelWords.length == 0 || labelWords[0].isEmpty()) {
                return results;
            }

            for (int i = 0; i < allFragments.size(); i++) {
                // Controlla se a partire dall'indice i c'è la sequenza di parole dell'etichetta
                boolean match = true;
                for (int w = 0; w < labelWords.length; w++) {
                    if (i + w >= allFragments.size()) {
                        match = false;
                        break;
                    }
                    TextFragment tf = allFragments.get(i + w);
                    String cleanTfText = java.text.Normalizer.normalize(
                            tf.text().toLowerCase(java.util.Locale.ROOT).replaceAll("^[\\p{Punct}\\s]+|[\\p{Punct}\\s]+$", ""),
                            java.text.Normalizer.Form.NFKD);
                    String cleanLabelWord = java.text.Normalizer.normalize(
                            labelWords[w].replaceAll("^[\\p{Punct}\\s]+|[\\p{Punct}\\s]+$", ""),
                            java.text.Normalizer.Form.NFKD);
                    String normTfText = java.text.Normalizer.normalize(tf.text().toLowerCase(java.util.Locale.ROOT), java.text.Normalizer.Form.NFKD);
                    String normLabelWord = java.text.Normalizer.normalize(labelWords[w], java.text.Normalizer.Form.NFKD);
                    if (!cleanTfText.equalsIgnoreCase(cleanLabelWord) && !normTfText.contains(normLabelWord)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    TextFragment lastLabelFrag = allFragments.get(i + labelWords.length - 1);
                    float labelXEnd = lastLabelFrag.xEnd();
                    float labelY    = lastLabelFrag.y();
                    int pageIdx     = lastLabelFrag.pageIndex();

                    for (int j = 0; j < allFragments.size(); j++) {
                        if (j >= i && j < i + labelWords.length) continue;
                        TextFragment other = allFragments.get(j);
                        if (other.pageIndex() != pageIdx) continue;

                        boolean toTheRight = other.x() >= labelXEnd - 2.0f
                                && other.x() <= labelXEnd + maxOffsetPtX
                                && Math.abs(other.y() - labelY) <= maxOffsetPtY;

                        boolean below = other.x() <= lastLabelFrag.xEnd() + maxOffsetPtX
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
     * {@code labelText} richiede che ogni parola dell'etichetta corrisponda esattamente a un
     * frammento, <strong>maiuscole/minuscole comprese</strong> (ignorando solo gli spazi
     * iniziali/finali e la forma Unicode), non una semplice sottostringa: questo evita falsi
     * positivi quando le stesse parole compaiono all'interno di un paragrafo discorsivo del
     * documento (es. "il domicilio digitale" in una frase rispetto all'etichetta
     * "Domicilio digitale").</p>
     *
     * @param labelText       testo esatto dell'etichetta, così come appare nel PDF
     * @param occurrenceIndex indice 1-based dell'occorrenza dell'etichetta da considerare
     */
    public static FieldLocator labelProximityOccurrence(String labelText, int occurrenceIndex) {
        if (occurrenceIndex < 1) {
            throw new IllegalArgumentException("occurrenceIndex deve essere >= 1 (1-based)");
        }
        String[] labelWords = labelText.trim().split("\\s+");
        return allFragments -> {
            int occurrence = 0;

            for (int i = 0; i + labelWords.length <= allFragments.size(); i++) {
                if (!startsWithWords(allFragments, i, labelWords)) continue;

                occurrence++;
                if (occurrence != occurrenceIndex) continue;

                int last = i + labelWords.length - 1;
                return valueColumnAfter(allFragments.get(last), allFragments, last);
            }
            return List.of();
        };
    }

    /**
     * Extraction yields one fragment per word: a label matches a run of fragments, case-sensitive so that the
     * same words inside a sentence are not taken as the label.
     */
    private static boolean startsWithWords(List<TextFragment> fragments, int start, String[] words) {
        for (int w = 0; w < words.length; w++) {
            if (!normalizeWord(fragments.get(start + w).text()).equals(normalizeWord(words[w]))) {
                return false;
            }
        }
        return true;
    }

    private static String normalizeWord(String word) {
        return java.text.Normalizer.normalize(word.trim(), java.text.Normalizer.Form.NFKD);
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
    // -----------------------------------------------------------------------
    // 4. Modificatori e filtri geometrici
    // -----------------------------------------------------------------------

    /**
     * Filtra i frammenti localizzati per includere solo quelli appartenenti alla pagina indicata (0-based).
     */
    public static FieldLocator onPage(int pageIndex, FieldLocator delegate) {
        return allFragments -> delegate.locate(allFragments).stream()
                .filter(f -> f.pageIndex() == pageIndex)
                .toList();
    }

    /**
     * Filtra i frammenti per escludere quelli al di sotto di una coordinata Y minima (es. per escludere il footer).
     */
    public static FieldLocator aboveY(float minY, FieldLocator delegate) {
        return allFragments -> delegate.locate(allFragments).stream()
                .filter(f -> f.y() >= minY)
                .toList();
    }

    /**
     * Localizza i frammenti di testo che contengono una determinata sottostringa (case-insensitive).
     */
    /**
     * Fragments strictly between the first fragment found by {@code upper} and the first fragment found by
     * {@code lower} below it, on the same page: for a body whose length depends on dynamic data.
     */
    public static FieldLocator between(FieldLocator upper, FieldLocator lower) {
        return allFragments -> {
            TextFragment top = upper.locate(allFragments).stream().findFirst().orElse(null);
            if (top == null) {
                return List.of();
            }
            TextFragment bottom = lower.locate(allFragments).stream()
                    .filter(f -> f.pageIndex() == top.pageIndex() && f.y() < top.y())
                    .findFirst().orElse(null);
            if (bottom == null) {
                return List.of();
            }
            return allFragments.stream()
                    .filter(f -> f.pageIndex() == top.pageIndex() && f.y() < top.y() - 1f && f.y() > bottom.yTop() + 1f)
                    .toList();
        };
    }

    public static FieldLocator lineContaining(String substring) {
        String lower = substring.toLowerCase(java.util.Locale.ROOT);
        return allFragments -> allFragments.stream()
                .filter(f -> f.text().toLowerCase(java.util.Locale.ROOT).contains(lower))
                .toList();
    }

    /**
     * Localizza un valore posizionato sulla stessa riga dell'etichetta (tolleranza verticale stretta <= 4.0pt),
     * immediatamente a destra dell'etichetta entro {@code maxOffsetPtX}, oppure il frammento stesso se racchiude
     * sia l'etichetta che il valore in un'unica riga.
     * Evita di catturare testo su righe successive o disclaimer sottostanti.
     */
    public static FieldLocator labelSameLine(String labelText, float maxOffsetPtX) {
        return allFragments -> {
            List<TextFragment> results = new ArrayList<>();
            String[] labelWords = labelText.trim().toLowerCase(java.util.Locale.ROOT).split("\\s+");
            if (labelWords.length == 0 || labelWords[0].isEmpty()) {
                return results;
            }

            for (int i = 0; i < allFragments.size(); i++) {
                boolean match = true;
                for (int w = 0; w < labelWords.length; w++) {
                    if (i + w >= allFragments.size()) {
                        match = false;
                        break;
                    }
                    TextFragment tf = allFragments.get(i + w);
                    String tfText = tf.text().toLowerCase(java.util.Locale.ROOT);
                    if (!tfText.contains(labelWords[w])) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    TextFragment lastLabelFrag = allFragments.get(i + labelWords.length - 1);
                    float labelXEnd = lastLabelFrag.xEnd();
                    float labelY = lastLabelFrag.y();
                    int pageIdx = lastLabelFrag.pageIndex();

                    for (int j = 0; j < allFragments.size(); j++) {
                        if (j >= i && j < i + labelWords.length) continue;
                        TextFragment other = allFragments.get(j);
                        if (other.pageIndex() != pageIdx) continue;

                        boolean sameLine = other.x() >= labelXEnd - 2.0f
                                && other.x() <= labelXEnd + maxOffsetPtX
                                && Math.abs(other.y() - labelY) <= 4.0f;

                        if (sameLine) {
                            results.add(other);
                        }
                    }
                }
            }
            return List.copyOf(results);
        };
    }
}
