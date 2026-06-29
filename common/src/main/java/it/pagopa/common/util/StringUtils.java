package it.pagopa.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                    "EMPTY", () -> "",
                    "TODAY", () -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
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

    public static String resolveContextualValue(String value, SharedStepsContext context) {
        StringBuilder text = new StringBuilder();
        String functionName = "$DA_CONTESTO(";
        int reachedIndex = 0;
        int labelStartIndex = textTemplate.indexOf(functionName, reachedIndex);
        int labelEndIndex;
        while (labelStartIndex > -1) {
            text.append(textTemplate.substring(reachedIndex, labelStartIndex));
            labelStartIndex += functionName.length();
            labelEndIndex = textTemplate.indexOf(')', labelStartIndex);
            String label = textTemplate.substring(labelStartIndex, labelEndIndex);
            String value = "";
            switch (label) {
                case "agreementId": value = sharedStepsContext.getAgreementId().toString(); break;
                case "eServiceName": value = sharedStepsContext.getEServicesCommonContext().getName(); break;
                case "eServiceId": value = sharedStepsContext.getEServicesCommonContext().getEserviceId().toString(); break;
                case "descriptorId": value = sharedStepsContext.getEServicesCommonContext().getDescriptorId().toString(); break;
                case "oldDescriptorId": value = sharedStepsContext.getEServicesCommonContext().getOldDescriptorId().toString(); break;
                case "producerName": value = sharedStepsContext.getEServicesCommonContext().getProducerName(); break;
                case "consumerName": value = sharedStepsContext.getTenantCommonContext().getConsumerTenantName(); break;
                // TODO Per ragioni di retrocompatibilità restano i valori TODAY e TODAY+GRACE_PERIOD
                // ma non andrebbero risolti come valori contestuali, piuttosto risolti da DateUtils
                case "TODAY": value = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); break;
                case "TODAY+GRACE_PERIOD":
                    value = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); break;
                default:
                    throw new IllegalArgumentException("Not supported value '" + label + "' in function $" + functionName);
            }
            text.append(value);
            // Controlla se c'è un prossimo placeholder
            reachedIndex = labelEndIndex + 1;
            labelStartIndex = textTemplate.indexOf(functionName, reachedIndex);
            if (labelStartIndex == -1) {
                text.append(textTemplate.substring(reachedIndex));
            }
        }
        if (text.isEmpty()) text.append(textTemplate);
        return text.toString();
    }
}
