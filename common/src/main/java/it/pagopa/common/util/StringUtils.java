package it.pagopa.common.util;

public class StringUtils {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String toNullable(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return value;
    }
}
