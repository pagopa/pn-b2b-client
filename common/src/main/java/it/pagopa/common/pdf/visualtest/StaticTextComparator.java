package it.pagopa.common.pdf.visualtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Compares the whole text of two PDFs word by word, with the template dynamic fields replaced by a placeholder
 * and line breaks ignored: it catches changes inside visually masked areas and is immune to reflow.
 */
public final class StaticTextComparator {

    private static final int CONTEXT_CHARS = 40;

    private StaticTextComparator() {
    }

    /** Words of the document; consecutive fragments of the same field collapse into one placeholder. */
    public static List<String> normalizedWords(ExtractedPdfText text, PdfDocumentTemplate template) {
        List<TextFragment> fragments = text.allFragments();
        Map<TextFragment, String> placeholders = new IdentityHashMap<>();
        for (PdfDocumentTemplate.FieldDefinition def : template.fieldDefinitions()) {
            for (TextFragment f : def.locator().locate(fragments)) {
                placeholders.putIfAbsent(f, "{" + def.fieldName() + "}");
            }
        }
        List<String> words = new ArrayList<>();
        for (TextFragment f : fragments) {
            String placeholder = placeholders.get(f);
            if (placeholder != null) {
                if (words.isEmpty() || !words.get(words.size() - 1).equals(placeholder)) {
                    words.add(placeholder);
                }
                continue;
            }
            Arrays.stream(f.text().trim().split("\\s+")).filter(w -> !w.isEmpty()).forEach(words::add);
        }
        return words;
    }

    /**
     * Empty when the static text is the same, otherwise the first difference with some context. Words are
     * concatenated without spaces, so a word split differently by a line break does not count.
     */
    public static Optional<String> firstDifference(List<String> expected, List<String> actual) {
        String e = String.join("", expected);
        String a = String.join("", actual);
        int i = 0;
        while (i < e.length() && i < a.length() && e.charAt(i) == a.charAt(i)) {
            i++;
        }
        if (i == e.length() && i == a.length()) {
            return Optional.empty();
        }
        return Optional.of("testo diverso dal carattere " + (i + 1) + ": atteso \"" + window(e, i)
                + "\", trovato \"" + window(a, i) + "\"");
    }

    private static String window(String text, int index) {
        int from = Math.max(0, index - CONTEXT_CHARS);
        int to = Math.min(text.length(), index + CONTEXT_CHARS);
        return (from > 0 ? "…" : "") + text.substring(from, to) + (to < text.length() ? "…" : "");
    }
}
