package it.pagopa.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe di utility per la gestione e la risoluzione di placeholder dinamici
 * utilizzati nei test Gherkin.
 * <p>
 * La classe supporta funzioni custom espresse nel formato:
 *
 * <pre>
 * $NOME_FUNZIONE(argomenti)
 * </pre>
 * <p>
 * Esempi:
 *
 * <pre>
 * $DATE_ADD(-1D)
 * $DATE_ADD(2M)
 * $TODAY()
 * </pre>
 * <p>
 * Funzioni supportate:
 * <ul>
 *     <li><b>DATE_ADD</b>: aggiunge o sottrae un intervallo temporale alla data corrente.</li>
 *     <li><b>TODAY</b>: restituisce la data corrente.</li>
 * </ul>
 * <p>
 * Unità temporali supportate da DATE_ADD:
 * <ul>
 *     <li>D = Giorni</li>
 *     <li>W = Settimane</li>
 *     <li>M = Mesi</li>
 *     <li>Y = Anni</li>
 * </ul>
 * <p>
 * Le date restituite sono formattate secondo lo standard ISO-8601:
 *
 * <pre>
 * yyyy-MM-dd
 * </pre>
 */
public class DateUtils {

    /**
     * Formatter ISO utilizzato per serializzare le date.
     */
    public static final DateTimeFormatter FORMATTER_ISO =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Pattern utilizzato per identificare le funzioni dinamiche supportate.
     * <p>
     * Esempio:
     *
     * <pre>
     * $DATE_ADD(-1D)
     * </pre>
     */
    private static final Pattern FUNCTION_PATTERN =
            Pattern.compile("^\\$(\\w+)\\((.*)\\)$");

    /**
     * Pattern utilizzato per il parsing degli argomenti della funzione DATE_ADD.
     * <p>
     * Formati supportati:
     *
     * <pre>
     * 1D
     * -10M
     * +2Y
     * </pre>
     */
    private static final Pattern DATE_ADD_PATTERN =
            Pattern.compile("^([+-]?\\d+)([YMDW])$");

    /**
     * Registro delle funzioni dinamiche supportate.
     */
    private static final Map<String, Function<String, String>> FUNCTIONS =
            Map.of(
                    "DATE_ADD", DateUtils::dateAdd,
                    "TODAY", DateUtils::today
            );

    /**
     * Risolve un placeholder dinamico restituendone il valore calcolato.
     * <p>
     * Se il valore fornito non corrisponde a una funzione supportata,
     * viene restituito invariato.
     * <p>
     * Esempi:
     *
     * <pre>
     * $TODAY()       -> 2026-05-07
     * $DATE_ADD(-1D) -> 2026-05-06
     * </pre>
     *
     * @param value espressione da risolvere
     * @return valore risolto oppure il valore originale se non contiene funzioni
     * @throws IllegalArgumentException se la funzione non è supportata
     *                                  oppure se gli argomenti non sono validi
     */
    public static String resolveDate(String value) {

        if (StringUtils.resolveValue(value) == null) {
            return null;
        }

        Matcher matcher = FUNCTION_PATTERN.matcher(value);

        if (!matcher.matches()) {
            return value;
        }

        String functionName = matcher.group(1);
        String argument = matcher.group(2);

        Function<String, String> function = FUNCTIONS.get(functionName);

        if (function == null) {
            throw new IllegalArgumentException(
                    "Funzione non supportata: $" + functionName
            );
        }

        return function.apply(argument);
    }

    /**
     * Risolve la funzione DATE_ADD.
     * <p>
     * Aggiunge o sottrae un intervallo temporale alla data corrente.
     * <p>
     * Unità supportate:
     * <ul>
     *     <li>D = Giorni</li>
     *     <li>W = Settimane</li>
     *     <li>M = Mesi</li>
     *     <li>Y = Anni</li>
     * </ul>
     * <p>
     * Esempi:
     *
     * <pre>
     * -1D
     * +2M
     * 1Y
     * </pre>
     *
     * @param value espressione temporale
     * @return data calcolata formattata come ISO_LOCAL_DATE
     * @throws IllegalArgumentException se il formato dell'espressione non è valido
     */
    private static String dateAdd(String value) {

        Matcher matcher = DATE_ADD_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Formato DATE_ADD non valido: " + value
            );
        }

        int amount = Integer.parseInt(matcher.group(1));
        char unit = matcher.group(2).charAt(0);

        LocalDate date = LocalDate.now();

        LocalDate result = switch (unit) {
            case 'Y' -> date.plusYears(amount);
            case 'W' -> date.plusWeeks(amount);
            case 'M' -> date.plusMonths(amount);
            case 'D' -> date.plusDays(amount);
            default -> throw new IllegalArgumentException(
                    "Unità temporale non valida: " + unit
            );
        };

        return result.format(FORMATTER_ISO);
    }

    /**
     * Risolve la funzione TODAY.
     * <p>
     * Restituisce la data corrente di sistema formattata come ISO_LOCAL_DATE.
     *
     * @param ignored parametro non utilizzato
     * @return data corrente
     */
    private static String today(String ignored) {
        return LocalDate.now().format(FORMATTER_ISO);
    }
}