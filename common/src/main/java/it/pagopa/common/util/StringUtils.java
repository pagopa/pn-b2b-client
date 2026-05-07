package it.pagopa.common.util;

import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe di utility per la gestione e normalizzazione delle stringhe.
 * <p>
 * Supporta placeholder dinamici utilizzabili nei test Gherkin
 * tramite il prefisso '$'.
 * <p>
 * Esempi:
 *
 * <pre>
 * $NULL
 * $EMPTY
 * </pre>
 * <p>
 * Placeholder supportati:
 * <ul>
 *     <li><b>$NULL</b>: restituisce {@code null}</li>
 *     <li><b>$EMPTY</b>: restituisce una stringa vuota ({@code ""})</li>
 * </ul>
 */
public class StringUtils {

    /**
     * Alfabeto maiuscolo inglese.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Pattern utilizzato per identificare i placeholder dinamici.
     * <p>
     * Esempi validi:
     *
     * <pre>
     * $NULL
     * $EMPTY
     * </pre>
     */
    private static final Pattern FUNCTION_PATTERN =
            Pattern.compile("^\\$(\\w+)$");

    /**
     * Registro delle funzioni stringa supportate.
     */
    private static final Map<String, Supplier<String>> FUNCTIONS =
            Map.of(
                    "NULL", () -> null,
                    "EMPTY", () -> ""
            );

    /**
     * Normalizza una stringa risolvendo eventuali placeholder dinamici.
     * <p>
     * Se il valore contiene un placeholder supportato,
     * viene restituito il relativo valore risolto.
     * <p>
     * Esempi:
     *
     * <pre>
     * $NULL  -> null
     * $EMPTY -> ""
     * </pre>
     * <p>
     * Inoltre:
     * <ul>
     *     <li>stringhe vuote vengono convertite in {@code null}</li>
     *     <li>la literal "null" (case insensitive) viene convertita in {@code null}</li>
     * </ul>
     *
     * @param value valore da normalizzare
     * @return valore normalizzato
     * @throws IllegalArgumentException se il placeholder non è supportato
     */
    public static String resolveValue(String value) {

        if (value == null) {
            return null;
        }

        value = value.trim();

        Matcher matcher = FUNCTION_PATTERN.matcher(value);

        if (matcher.matches()) {

            String functionName = matcher.group(1);

            Supplier<String> function = FUNCTIONS.get(functionName);

            if (function == null) {
                throw new IllegalArgumentException(
                        "Funzione stringa non supportata: $" + functionName
                );
            }

            return function.get();
        }

        if (value.isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }

        return value;
    }
}