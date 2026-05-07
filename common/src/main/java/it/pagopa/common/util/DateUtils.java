package it.pagopa.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter FORMATTER_ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String calculateDate(String duration) {
        if (StringUtils.toNullable(duration) == null) {
            return null;
        }

        if (duration.startsWith("OFFSET(") && duration.endsWith(")")) {
            LocalDate startDate = LocalDate.now();

            int value = Integer.parseInt(duration.substring(0, duration.length() - 1));
            char unit = duration.charAt(duration.length() - 1);

            LocalDate endDate = switch (unit) {
                case 'Y' -> startDate.plusYears(value);
                case 'M' -> startDate.plusMonths(value);
                case 'D' -> startDate.plusDays(value);
                default -> throw new IllegalArgumentException("Formato durata non valido: " + duration);
            };
            return endDate.format(FORMATTER_ISO);
        }
        return duration;
    }
}
