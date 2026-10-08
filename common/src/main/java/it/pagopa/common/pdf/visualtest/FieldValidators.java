package it.pagopa.common.pdf.visualtest;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Factory di validatori {@link FieldValidator} standard, agnostici rispetto al dominio.
 *
 * <p>I validatori composti da questa factory sono stati progettati per essere riutilizzabili
 * su qualsiasi tipo di documento PDF. I validatori specifici di dominio (es. formato IUN)
 * devono essere definiti nel modulo applicativo che dipende da {@code common}.</p>
 */
public final class FieldValidators {

    private FieldValidators() {
    }

    // -----------------------------------------------------------------------
    // Validatori di presenza
    // -----------------------------------------------------------------------

    /**
     * Il campo deve essere presente (almeno un frammento non vuoto).
     */
    public static FieldValidator present() {
        return locatedFragments -> {
            if (locatedFragments == null || locatedFragments.isEmpty()) {
                return "Campo non trovato nel documento";
            }
            boolean hasText = locatedFragments.stream().anyMatch(f -> !f.text().isBlank());
            return hasText ? null : "Campo trovato ma privo di testo";
        };
    }

    /**
     * Il campo deve contenere il valore letterale atteso (case-insensitive, sottostringa).
     *
     * @param expected valore atteso
     */
    public static FieldValidator containsText(String expected) {
        return locatedFragments -> {
            if (locatedFragments == null || locatedFragments.isEmpty()) {
                return "Campo non trovato; valore atteso: \"" + expected + "\"";
            }
            String combined = locatedFragments.stream()
                    .map(TextFragment::text)
                    .reduce("", (a, b) -> a + " " + b)
                    .strip();
            if (combined.toLowerCase(java.util.Locale.ROOT)
                        .contains(expected.toLowerCase(java.util.Locale.ROOT))) {
                return null;
            }
            return "Valore atteso: \"" + expected + "\", trovato: \"" + combined + "\"";
        };
    }

    // -----------------------------------------------------------------------
    // Validatori di formato
    // -----------------------------------------------------------------------

    /**
     * Il campo deve essere un UUID v4 valido.
     */
    public static FieldValidator uuid() {
        return regex(Pattern.compile(
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"),
                "UUID v4 (xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx)");
    }

    /**
     * Il campo deve essere una data in formato ISO-8601 (es. {@code 2024-01-31}).
     */
    public static FieldValidator isoDate() {
        return regex(Pattern.compile(
                "\\b\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])\\b"),
                "data ISO-8601 (yyyy-MM-dd)");
    }

    /**
     * Il campo deve essere una data-ora in formato ISO-8601
     * (es. {@code 2024-01-31T10:30:00Z} o con offset {@code +02:00}).
     */
    public static FieldValidator isoDateTime() {
        return regex(Pattern.compile(
                "\\b\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])T"
                + "(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})\\b"),
                "data-ora ISO-8601 (yyyy-MM-ddTHH:mm:ssZ)");
    }

    /**
     * Il campo deve essere un intero positivo (senza segno).
     */
    public static FieldValidator positiveInteger() {
        return regex(Pattern.compile("\\b[1-9]\\d*\\b"),
                "intero positivo");
    }

    /**
     * Il campo deve fare match con la regex fornita.
     *
     * @param pattern       regex compilata
     * @param formatDescription descrizione leggibile del formato atteso (usata nei messaggi di errore)
     */
    public static FieldValidator regex(Pattern pattern, String formatDescription) {
        return locatedFragments -> {
            if (locatedFragments == null || locatedFragments.isEmpty()) {
                return "Campo non trovato; formato atteso: " + formatDescription;
            }
            for (TextFragment f : locatedFragments) {
                if (pattern.matcher(f.text()).find()) {
                    return null; // almeno un frammento è conforme
                }
            }
            String combined = locatedFragments.stream()
                    .map(TextFragment::text)
                    .reduce("", (a, b) -> a + " " + b)
                    .strip();
            return "Formato atteso: " + formatDescription + ", trovato: \"" + combined + "\"";
        };
    }

    // -----------------------------------------------------------------------
    // Validatori posizionali
    // -----------------------------------------------------------------------

    /**
     * Il campo deve trovarsi all'interno di una tolleranza posizionale rispetto
     * alla posizione attesa (in punti PDF).
     *
     * @param expectedX    coordinata X attesa
     * @param expectedY    coordinata Y attesa
     * @param tolerancePt  tolleranza in punti PDF (applicata sia su X che su Y)
     */
    public static FieldValidator withinPosition(float expectedX, float expectedY, float tolerancePt) {
        return locatedFragments -> {
            if (locatedFragments == null || locatedFragments.isEmpty()) {
                return "Campo non trovato per verifica posizionale; posizione attesa: ("
                        + expectedX + ", " + expectedY + ")";
            }
            TextFragment first = locatedFragments.get(0);
            float dx = Math.abs(first.x() - expectedX);
            float dy = Math.abs(first.y() - expectedY);
            if (dx <= tolerancePt && dy <= tolerancePt) {
                return null;
            }
            return String.format(
                    "Posizione attesa: (%.1f, %.1f), trovata: (%.1f, %.1f), "
                    + "scostamento: (Δx=%.1f, Δy=%.1f), tolleranza: %.1f pt",
                    expectedX, expectedY, first.x(), first.y(), dx, dy, tolerancePt);
        };
    }

    /**
     * Combina due validatori in AND logico: entrambi devono passare.
     */
    public static FieldValidator and(FieldValidator first, FieldValidator second) {
        return locatedFragments -> {
            String err1 = first.validate(locatedFragments);
            if (err1 != null) return err1;
            return second.validate(locatedFragments);
        };
    }
}
