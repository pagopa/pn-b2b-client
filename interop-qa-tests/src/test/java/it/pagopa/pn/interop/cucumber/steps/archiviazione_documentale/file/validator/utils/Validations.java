package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.ArchivingUtils.TS_FORMAT;

public final class Validations {

    /**
     * Valida un timestamp nei formati supportati:
     *
     * <ul>
     *   <li>yyyyMMddHHmmss (formato compatto legacy)</li>
     *   <li>ISO-8601 completo (es. 2024-05-30T10:15:30Z)</li>
     * </ul>
     */
    public static boolean isValidTimestamp(String value) {
        return isCompactTimestamp(value) || isIsoTimestamp(value);
    }

    /**
     * Valida un timestamp nel formato compatto yyyyMMddHHmmss.
     */
    public static boolean isCompactTimestamp(String value) {
        if (value == null || !value.matches("\\d{14}")) {
            return false;
        }

        try {
            TS_FORMAT.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida un timestamp ISO-8601 completo.
     */
    public static boolean isIsoTimestamp(String value) {
        if (value == null) {
            return false;
        }

        try {
            OffsetDateTime.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Valida una stringa UUID standard.
     */
    public static boolean isValidUUID(String value) {
        if (value == null) {
            return false;
        }

        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
