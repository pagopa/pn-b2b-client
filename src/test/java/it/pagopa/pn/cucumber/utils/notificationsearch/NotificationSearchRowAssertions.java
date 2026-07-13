package it.pagopa.pn.cucumber.utils.notificationsearch;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

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
 */
public final class NotificationSearchRowAssertions {

    private NotificationSearchRowAssertions() {
    }

    public static void assertAllRowsMatchCriteria(List<?> rows, Map<String, List<String>> criteria) {
        assertThat(rows).as("L'elenco delle notifiche recuperate non deve essere nullo").isNotNull();
        rows.forEach(row -> criteria.forEach((field, allowedValues) -> assertRowFieldMatches(row, field, allowedValues)));
    }

    private static void assertRowFieldMatches(Object row, String field, List<String> allowedValues) {
        Object actualValue = NotificationRowFieldReader.readField(row, field);
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
