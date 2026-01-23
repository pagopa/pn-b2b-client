package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.pn.interop.cucumber.utility.enums.ResolvableToken;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public final class StepParser {
    private StepParser() {
    }

    private static String normalize(String value) {
        if (value == null) return null;

        String v = value.trim();
        ResolvableToken token = ResolvableToken.from(v);

        if (token == ResolvableToken.NULL) return null;
        if (token == ResolvableToken.BLANK) return "";
        return v;
    }

    private static <T> T parseCore(String value, boolean safe, Supplier<T> randomSupplier, Function<String, T> parser) {
        String v = normalize(value);
        if (v == null) return null;

        ResolvableToken token = ResolvableToken.from(v);

        try {
            if (token == ResolvableToken.RANDOM && randomSupplier != null) {
                return randomSupplier.get();
            }
            return parser.apply(v);
        } catch (RuntimeException ex) {
            if (safe) return null;
            throw ex;
        }
    }

    public static String nullOrBlankOrValue(String value) {
        return normalize(value);
    }

    public static String nullOrValue(String value) {
        String v = normalize(value);
        return (v == null || v.isBlank()) ? null : v;
    }

    public static <T> T parseNullable(String value, Function<String, T> parser) {
        return parseCore(value, false, null, parser);
    }

    public static <T> T parseNullableSafe(String value, Function<String, T> parser) {
        return parseCore(value, true, null, parser);
    }

    public static Integer nullableInteger(String value) {
        return parseCore(value, true, null, Integer::valueOf);
    }

    public static Boolean nullableBoolean(String value) {
        return parseCore(value, false, null, Boolean::parseBoolean);
    }

    public static UUID uuidOrRandomOrNull(String value) {
        return parseCore(value, true, UUID::randomUUID, UUID::fromString);
    }

    public static Integer intOrRandomOrNull(String value) {
        return parseCore(value, true, () -> ThreadLocalRandom.current().nextInt(), Integer::parseInt);
    }

    public static <E> List<E> singletonListNullable(String value, Function<String, E> mapper) {
        return parseCore(value, true, null, v -> List.of(mapper.apply(v)));
    }
}
