package it.pagopa.pn.cucumber.utils.notificationsearch;

import it.pagopa.common.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica che ogni notifica restituita da una ricerca rispetti i criteri attesi, indipendentemente dal
 * tipo concreto di riga (notifica legale o bonaria, es. {@code FullNotificationSearchRow} o
 * {@code LegalNotificationSearchRow}). I criteri sono coppie campo/valori ammessi: il valore del campo
 * viene letto dalla riga per riflessione tramite {@link NotificationRowFieldReader}.
 * <p>
 * Se il valore letto dalla riga è una data/ora ISO-8601 (es. {@code sentAt}, sia esso tipizzato
 * {@code OffsetDateTime} o {@code String} a seconda del modello), il confronto non è testuale ma per
 * data: un solo valore atteso richiede che la notifica sia stata inviata in quel giorno, due valori
 * (separati da virgola nella cella del feature) definiscono un range inclusivo {@code [start, end]}.
 * <p>
 * Se il valore letto dalla riga è una {@link List} (es. {@code recipients}, la lista dei taxId dei
 * destinatari), la riga è valida se contiene almeno tutti i valori elencati in tabella (ordine libero):
 * la lista effettiva può contenere anche altri valori non elencati, non deve necessariamente coincidere.
 * <p>
 * Tutti i confronti testuali (campo singolo, elementi di lista, valore di {@value #CONSISTENT_VALUE})
 * ignorano maiuscole/minuscole: conta il contenuto del valore, non la sua formattazione esatta.
 * <p>
 * Se per un campo scalare (non lista, non data) vengono elencati più valori ammessi (es.
 * {@code communicationType | LEGAL, INFORMAL}), il controllo è duplice: ogni riga deve avere uno dei
 * valori elencati, e in più ognuno dei valori elencati deve comparire in almeno una riga tra quelle
 * restituite (non basta quindi che le righe abbiano tutte lo stesso valore tra quelli ammessi).
 * <p>
 * Il criterio {@value #ITEMS_FOUND_FIELD} è un caso speciale: non è un campo di una riga ma verifica il
 * numero di notifiche restituite (es. {@code | itemsFound | 3 |}), quindi non viene letto per
 * riflessione né applicato riga per riga.
 * <p>
 * Il valore atteso {@value #CONSISTENT_VALUE} è un altro caso speciale, utile quando la request espone
 * un identificativo (es. l'id di un gruppo) ma la riga di risposta espone invece un valore derivato
 * diverso (es. il nome del gruppo), che quindi non è confrontabile con il valore inviato in request: in
 * questo caso si verifica solo che il campo sia valorizzato e che abbia lo stesso valore su tutte le
 * righe restituite, senza confrontarlo con un valore atteso specifico.
 * <p>
 * Il placeholder {@code $NOT_EMPTY} (risolto tramite {@link StringUtils#resolveValue} nel
 * {@link NotificationSearchCriteriaMapper} in {@link #NOT_EMPTY_VALUE}) è un ulteriore caso speciale,
 * ancora più permissivo di {@value #CONSISTENT_VALUE}: verifica solo che il campo sia presente e non
 * vuoto su ogni riga restituita, senza richiedere che il valore sia lo stesso su tutte le righe né
 * confrontarlo con un valore atteso. È il caso di campi testuali a scelta libera dell'ente mittente
 * (es. {@code sender}, la denominazione del mittente) i cui dati, negli ambienti di test, sono spesso
 * "sporchi" (es. impostati a un valore diverso da quello effettivamente usato in fase di creazione
 * della notifica): un confronto puntuale o di coerenza tra le righe risulterebbe quindi instabile.
 */
public final class NotificationSearchRowAssertions {

    private static final String ITEMS_FOUND_FIELD = "itemsFound";
    private static final String CONSISTENT_VALUE = "CONSISTENT";
    private static final String NOT_EMPTY_VALUE = StringUtils.NOT_EMPTY_MARKER;

    private NotificationSearchRowAssertions() {
    }

    public static void assertAllRowsMatchCriteria(List<?> rows, Map<String, List<String>> criteria) {
        assertThat(rows).as("L'elenco delle notifiche recuperate non deve essere nullo").isNotNull();
        List<String> itemsFound = criteria.get(ITEMS_FOUND_FIELD);
        if (itemsFound != null) {
            assertItemsFound(rows, itemsFound);
        }
        criteria.forEach((field, allowedValues) -> {
            if (ITEMS_FOUND_FIELD.equals(field)) {
                return;
            }
            if (isConsistentValue(allowedValues)) {
                assertFieldConsistentAcrossRows(rows, field);
                return;
            }
            if (isNotEmptyValue(allowedValues)) {
                rows.forEach(row -> assertFieldNotEmpty(row, field));
                return;
            }
            rows.forEach(row -> assertRowFieldMatches(row, field, allowedValues));
            if (allowedValues.size() > 1) {
                assertAllAllowedScalarValuesAreRepresented(rows, field, allowedValues);
            }
        });
    }

    /**
     * Verifica che, quando per un campo scalare sono ammessi più valori, ognuno di essi compaia in
     * almeno una delle righe restituite. Non si applica ai campi lista (es. {@code recipients}) né ai
     * campi data (es. {@code sentAt}), per cui più valori indicano rispettivamente un elenco di valori
     * ammessi per riga o un range: per questi due casi il tipo del campo viene dedotto dalla prima riga.
     */
    private static void assertAllAllowedScalarValuesAreRepresented(List<?> rows, String field, List<String> allowedValues) {
        if (!rows.isEmpty()) {
            Object firstValue = NotificationRowFieldReader.readField(rows.get(0), field);
            if (firstValue instanceof List) {
                return;
            }
            String firstValueAsString = firstValue == null ? null : firstValue.toString();
            if (parseAsDateTime(firstValueAsString) != null) {
                return;
            }
        }
        List<String> actualValues = rows.stream()
                .map(row -> NotificationRowFieldReader.readField(row, field))
                .map(value -> value == null ? null : value.toString())
                .collect(Collectors.toList());
        allowedValues.forEach(allowedValue -> {
            boolean isRepresented = actualValues.stream().anyMatch(actual -> equalsIgnoringCase(allowedValue, actual));
            assertThat(isRepresented)
                    .as("Il valore '%s' del criterio '%s' non è presente in nessuna delle notifiche restituite: valori trovati %s",
                            allowedValue, field, actualValues)
                    .isTrue();
        });
    }

    private static boolean isConsistentValue(List<String> allowedValues) {
        return allowedValues.size() == 1 && CONSISTENT_VALUE.equalsIgnoreCase(allowedValues.get(0));
    }

    private static boolean isNotEmptyValue(List<String> allowedValues) {
        return allowedValues.size() == 1 && NOT_EMPTY_VALUE.equalsIgnoreCase(allowedValues.get(0));
    }

    private static void assertFieldNotEmpty(Object row, String field) {
        Object actualValue = NotificationRowFieldReader.readField(row, field);
        assertThat(actualValue)
                .as("Il campo '%s' della notifica con iun '%s' deve essere presente", field, readIunSafely(row))
                .isNotNull();
        if (actualValue instanceof String actualString) {
            assertThat(actualString)
                    .as("Il campo '%s' della notifica con iun '%s' non deve essere vuoto", field, readIunSafely(row))
                    .isNotBlank();
        } else if (actualValue instanceof Collection<?> actualCollection) {
            assertThat(actualCollection)
                    .as("Il campo '%s' della notifica con iun '%s' non deve essere vuoto", field, readIunSafely(row))
                    .isNotEmpty();
        }
    }

    private static void assertFieldConsistentAcrossRows(List<?> rows, String field) {
        List<Object> actualValues = rows.stream()
                .map(row -> NotificationRowFieldReader.readField(row, field))
                .collect(Collectors.toList());
        actualValues.forEach(value -> assertThat(value).as("Il campo '%s' non deve essere nullo", field).isNotNull());
        if (actualValues.isEmpty()) {
            return;
        }
        Object referenceValue = actualValues.get(0);
        assertThat(actualValues)
                .as("Il campo '%s' deve avere lo stesso valore su tutte le notifiche restituite: trovati %s", field, actualValues)
                .allMatch(value -> equalsIgnoringCase(referenceValue, value));
    }

    private static void assertItemsFound(List<?> rows, List<String> allowedValues) {
        if (allowedValues.size() != 1) {
            throw new IllegalArgumentException("Il criterio '" + ITEMS_FOUND_FIELD + "' richiede un solo valore numerico, ricevuti: " + allowedValues);
        }
        String rawValue = allowedValues.get(0);
        int expectedItemsFound;
        try {
            expectedItemsFound = Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il criterio '" + ITEMS_FOUND_FIELD + "' richiede un valore numerico, ricevuto: '" + rawValue + "'", e);
        }
        assertThat(rows)
                .as("Il numero di notifiche recuperate è %s: atteso esattamente %s", rows.size(), expectedItemsFound)
                .hasSize(expectedItemsFound);
    }

    private static void assertRowFieldMatches(Object row, String field, List<String> allowedValues) {
        Object actualValue = NotificationRowFieldReader.readField(row, field);
        if (actualValue instanceof List<?> actualList) {
            assertListFieldContainsAllValues(row, field, actualList, allowedValues);
            return;
        }
        String actualValueAsString = actualValue == null ? null : actualValue.toString();
        OffsetDateTime actualDateTime = parseAsDateTime(actualValueAsString);
        if (actualDateTime != null) {
            assertDateFieldWithinRange(row, field, actualDateTime, allowedValues);
            return;
        }
        boolean matchesAnyAllowedValue = allowedValues.stream().anyMatch(allowed -> equalsIgnoringCase(allowed, actualValueAsString));
        assertThat(matchesAnyAllowedValue)
                .as("Il campo '%s' della notifica con iun '%s' vale '%s': atteso uno tra %s",
                        field, readIunSafely(row), actualValueAsString, allowedValues)
                .isTrue();
    }

    private static void assertListFieldContainsAllValues(Object row, String field, List<?> actualList, List<String> allowedValues) {
        List<String> actualValuesAsString = actualList.stream()
                .map(value -> value == null ? null : value.toString())
                .collect(Collectors.toList());
        assertThat(lowerCased(actualValuesAsString))
                .as("Il campo '%s' della notifica con iun '%s' vale %s: atteso che contenga almeno %s",
                        field, readIunSafely(row), actualValuesAsString, allowedValues)
                .containsAll(lowerCased(allowedValues));
    }

    private static List<String> lowerCased(List<String> values) {
        return values.stream().map(value -> value == null ? null : value.toLowerCase()).collect(Collectors.toList());
    }

    /**
     * Confronto testuale case-insensitive, usato per tutti i criteri (campo singolo, elementi di lista,
     * valore di riferimento di {@value #CONSISTENT_VALUE}). I valori non stringa vengono confrontati con
     * {@link Object#equals(Object)}.
     */
    private static boolean equalsIgnoringCase(Object expected, Object actual) {
        if (expected instanceof String && actual instanceof String) {
            return ((String) expected).equalsIgnoreCase((String) actual);
        }
        return Objects.equals(expected, actual);
    }

    private static void assertDateFieldWithinRange(Object row, String field, OffsetDateTime actualDateTime, List<String> allowedValues) {
        LocalDate actualDate = actualDateTime.toLocalDate();
        LocalDate startDate = parseLocalDate(allowedValues.get(0), field);
        LocalDate endDate = allowedValues.size() > 1 ? parseLocalDate(allowedValues.get(1), field) : startDate;
        assertThat(actualDate)
                .as("Il campo '%s' della notifica con iun '%s' vale '%s': atteso tra '%s' e '%s'",
                        field, readIunSafely(row), actualDateTime, startDate, endDate)
                .isBetween(startDate, endDate);
    }

    private static OffsetDateTime parseAsDateTime(String value) {
        if (value == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDate parseLocalDate(String rawValue, String field) {
        try {
            return LocalDate.parse(rawValue);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Il valore '" + rawValue + "' del criterio '" + field + "' non è una data valida (formato atteso yyyy-MM-dd)", e);
        }
    }

    private static String readIunSafely(Object row) {
        try {
            Object iun = NotificationRowFieldReader.readField(row, "iun");
            return iun == null ? "N/A" : iun.toString();
        } catch (RuntimeException e) {
            return "N/A";
        }
    }
}
