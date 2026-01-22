package it.pagopa.pn.interop.cucumber.utility;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public final class StepParser {
    private StepParser() {}

    public static String normalizeNullable(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.equalsIgnoreCase("null") ? null : v;
    }

    public static <T> T parseNullable(String value, Function<String, T> parser) {
        String v = normalizeNullable(value);
        return v == null ? null : parser.apply(v);
    }

    public static <T> T parseNullableSafe(String value, Function<String, T> parser) {
        String v = normalizeNullable(value);
        if (v == null) return null;
        try {
            return parser.apply(v);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public static Integer nullableInteger(String value) {
        return parseNullableSafe(value, Integer::valueOf);
    }

    public static Boolean nullableBoolean(String value) {
        return parseNullable(value, Boolean::parseBoolean);
    }

    public static UUID uuidOrRandomOrNull(String value) {
        return parseNullableSafe(value, v ->
                v.equalsIgnoreCase("random") ? UUID.randomUUID() : UUID.fromString(v)
        );
    }

    public static <E> List<E> singletonListNullable(String value, Function<String, E> mapper) {
        return parseNullableSafe(value, v -> List.of(mapper.apply(v)));
    }
}
