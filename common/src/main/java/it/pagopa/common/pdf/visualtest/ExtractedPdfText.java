package it.pagopa.common.pdf.visualtest;

import java.util.Collections;
import java.util.List;

/**
 * Risultato dell'estrazione testuale completa di un documento PDF:
 * raccoglie le {@link PageText} di tutte le pagine.
 *
 * @param pages lista di {@link PageText} in ordine di pagina (0-based)
 */
public record ExtractedPdfText(List<PageText> pages) {

    public ExtractedPdfText {
        pages = Collections.unmodifiableList(pages);
    }

    /** Numero di pagine del documento. */
    public int pageCount() {
        return pages.size();
    }

    /**
     * Restituisce tutti i {@link TextFragment} del documento, in ordine
     * pagina → posizione verticale decrescente → posizione orizzontale crescente.
     */
    public List<TextFragment> allFragments() {
        return pages.stream()
                .flatMap(p -> p.fragments().stream())
                .toList();
    }

    /**
     * Testo concatenato dell'intero documento (pagine separate da {@code \n}).
     */
    public String fullText() {
        StringBuilder sb = new StringBuilder();
        for (PageText page : pages) {
            if (!sb.isEmpty()) {
                sb.append('\n');
            }
            sb.append(page.plainText());
        }
        return sb.toString();
    }
}
