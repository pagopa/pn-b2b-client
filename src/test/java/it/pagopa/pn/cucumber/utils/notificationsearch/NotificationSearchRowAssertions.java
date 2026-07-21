package it.pagopa.pn.cucumber.utils.notificationsearch;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
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
 * destinatari), il confronto è un'uguaglianza esatta con i valori attesi (ordine libero): la riga deve
 * contenere tutti e soli i valori elencati in tabella, non un semplice sottoinsieme.
 * <p>
 * Il criterio {@value #ITEMS_FOUND_FIELD} è un caso speciale: non è un campo di una riga ma verifica il
 * numero di notifiche restituite (es. {@code | itemsFound | 3 |}), quindi non viene letto per
 * riflessione né applicato riga per riga.
 */
public final class NotificationSearchRowAssertions {

    private static final String ITEMS_FOUND_FIELD = "itemsFound";

    private NotificationSearchRowAssertions() {
    }

    public static void assertAllRowsMatchCriteria(List<?> rows, Map<String, List<String>> criteria) {
        assertThat(rows).as("L'elenco delle notifiche recuperate non deve essere nullo").isNotNull();
        List<String> itemsFound = criteria.get(ITEMS_FOUND_FIELD);
        if (itemsFound != null) {
            assertItemsFound(rows, itemsFound);
        }
        rows.forEach(row -> criteria.forEach((field, allowedValues) -> {
            if (!ITEMS_FOUND_FIELD.equals(field)) {
                assertRowFieldMatches(row, field, allowedValues);
            }
        }));
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
            assertListFieldMatchesExactly(row, field, actualList, allowedValues);
            return;
        }
        String actualValueAsString = actualValue == null ? null : actualValue.toString();
        OffsetDateTime actualDateTime = parseAsDateTime(actualValueAsString);
        if (actualDateTime != null) {
            assertDateFieldWithinRange(row, field, actualDateTime, allowedValues);
            return;
        }
        assertThat(allowedValues)
                .as("Il campo '%s' della notifica con iun '%s' vale '%s': atteso uno tra %s",
                        field, readIunSafely(row), actualValueAsString, allowedValues)
                .contains(actualValueAsString);
    }

    private static void assertListFieldMatchesExactly(Object row, String field, List<?> actualList, List<String> allowedValues) {
        List<String> actualValuesAsString = actualList.stream()
                .map(value -> value == null ? null : value.toString())
                .collect(Collectors.toList());
        assertThat(actualValuesAsString)
                .as("Il campo '%s' della notifica con iun '%s' vale %s: atteso esattamente %s",
                        field, readIunSafely(row), actualValuesAsString, allowedValues)
                .containsExactlyInAnyOrderElementsOf(allowedValues);
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
