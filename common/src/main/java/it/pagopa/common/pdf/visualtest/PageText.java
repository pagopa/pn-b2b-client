package it.pagopa.common.pdf.visualtest;

import java.util.Collections;
import java.util.List;

/**
 * Raccolta di tutti i {@link TextFragment} estratti da una singola pagina PDF.
 *
 * @param pageIndex  indice di pagina 0-based
 * @param fragments  frammenti di testo in ordine di estrazione (posizione)
 */
public record PageText(int pageIndex, List<TextFragment> fragments) {

    public PageText {
        fragments = Collections.unmodifiableList(fragments);
    }

    /** Testo concatenato dell'intera pagina (frammenti separati da spazio). */
    public String plainText() {
        StringBuilder sb = new StringBuilder();
        for (TextFragment f : fragments) {
            if (!sb.isEmpty()) {
                sb.append(' ');
            }
            sb.append(f.text());
        }
        return sb.toString();
    }

    /**
     * Raggruppa i frammenti in righe logiche in base alla vicinanza verticale.
     *
     * @param tolerancePt tolleranza in punti PDF per considerare due frammenti sulla stessa riga
     * @return lista di righe; ogni riga è una lista di frammenti ordinata per X crescente
     */
    public List<List<TextFragment>> groupIntoLines(float tolerancePt) {
        if (fragments.isEmpty()) {
            return List.of();
        }
        List<TextFragment> sorted = fragments.stream()
                .sorted((a, b) -> {
                    int cmpY = Float.compare(b.y(), a.y()); // y decrescente (alto → basso nella pagina)
                    return cmpY != 0 ? cmpY : Float.compare(a.x(), b.x());
                })
                .toList();

        List<List<TextFragment>> lines = new java.util.ArrayList<>();
        List<TextFragment> currentLine = new java.util.ArrayList<>();
        currentLine.add(sorted.get(0));
        float lineBaseY = sorted.get(0).y();

        for (int i = 1; i < sorted.size(); i++) {
            TextFragment f = sorted.get(i);
            if (Math.abs(f.y() - lineBaseY) <= tolerancePt) {
                currentLine.add(f);
            } else {
                lines.add(currentLine.stream()
                        .sorted(java.util.Comparator.comparingDouble(TextFragment::x))
                        .toList());
                currentLine = new java.util.ArrayList<>();
                currentLine.add(f);
                lineBaseY = f.y();
            }
        }
        lines.add(currentLine.stream()
                .sorted(java.util.Comparator.comparingDouble(TextFragment::x))
                .toList());
        return Collections.unmodifiableList(lines);
    }
}
