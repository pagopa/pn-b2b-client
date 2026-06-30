package it.pagopa.common.util;

import it.pagopa.common.model.ISharedContext;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
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
     * Nome della funzione per recuperare valori dal contesto.
     */
    public static final String CONTEXT_FUNCTION_NAME = "CONTEXT";

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
                        "Not supported string function: $" + functionName
                );
            }

            return function.get();
        }

        if (value.isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }

        return value;
    }

    public static String resolveDynamicValues(String textTemplate, ISharedContext context) {
        StringBuilder text = new StringBuilder();
        String functionName, argument;
        int reachedIndex = 0;
        int functionNameStart = textTemplate.indexOf("$", reachedIndex);
        int functionNameEnd = textTemplate.indexOf("(", functionNameStart);
        int argumentStart = 0, argumentEnd = 0;

        while (functionNameStart > -1 && functionNameEnd > -1) {
            String value = "";

            // Raccogli testo non coinvolto in una funzione
            text.append(textTemplate.substring(reachedIndex, functionNameStart));

            // Recupera il nome di una funzione
            functionName = textTemplate.substring(functionNameStart + 1, functionNameEnd);

            // Recupera l'argomento della funzione
            argumentStart = functionNameEnd + 1;
            argumentEnd = textTemplate.indexOf(')', functionNameEnd);
            argument = textTemplate.substring(argumentStart, argumentEnd);

            // Esegue il tipo di funzione riconosciuto:

            // 1) Funzione di recupero di un valore dal contesto
            if (functionName.equals(CONTEXT_FUNCTION_NAME)) {
                String methodName = "get" + argument.substring(0, 1).toUpperCase() + argument.substring(1);
                try {
                    Method getterMethod = ISharedContext.class.getMethod(methodName);
                    value = (String)getterMethod.invoke(context);

                } catch (NoSuchMethodException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            // 2) Funzione su DateUtils
            } else if (DateUtils.FUNCTIONS.containsKey(functionName)) {
                value = DateUtils.FUNCTIONS.get(functionName).apply(argument);

            // 3) Funzione su StringUtils
            } else if (StringUtils.FUNCTIONS.containsKey(functionName)) {
                value = StringUtils.FUNCTIONS.get(functionName).get();

            // 4) Nessuna funzione riconosciuta: considera testo ordinario
            } else {
                text.append(textTemplate.substring(functionNameStart, argumentEnd + 1));
            }
            text.append(value);

            // Controlla se c'è una prossima funzione in stringa
            reachedIndex = argumentEnd + 1;
            functionNameStart = textTemplate.indexOf("$", reachedIndex);
            functionNameEnd = textTemplate.indexOf("(", functionNameStart);
        }
        text.append(textTemplate.substring(reachedIndex));
        if (text.isEmpty()) text.append(textTemplate);
        return text.toString();
    }
}
