package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.pn.interop.cucumber.enums.ResolvableToken;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public final class StepParser {
    private StepParser() {
    }

    public static String normalize(String value) {
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

    public static Long longOrRandomOrNull(String value) {
        return parseCore(value, true, () -> ThreadLocalRandom.current().nextLong(), Long::parseLong);
    }

    public static <E> List<E> singletonListNullable(String value, Function<String, E> mapper) {
        return parseCore(value, true, null, v -> List.of(mapper.apply(v)));
    }

    public static Duration durationOrNull(String s) {
        String normalized = normalize(s);
        if (normalized == null) return null;

        s = normalized.trim().toLowerCase(Locale.ROOT);
        if (s.endsWith("ms")) return Duration.ofMillis(Long.parseLong(s.substring(0, s.length() - 2)));
        if (s.endsWith("s")) return Duration.ofSeconds(Long.parseLong(s.substring(0, s.length() - 1)));
        if (s.endsWith("m")) return Duration.ofMinutes(Long.parseLong(s.substring(0, s.length() - 1)));
        if (s.endsWith("h")) return Duration.ofHours(Long.parseLong(s.substring(0, s.length() - 1)));
        return Duration.parse(s);
    }

    public static OffsetDateTime dateTimeOrNull(String raw) {
        raw = normalize(raw);
        if (raw == null) return null;

        String token = raw.trim();
        String lower = token.toLowerCase();

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (lower.equals("now")) return now;

        // now+ / now- con suffisso h/m/s
        if (lower.startsWith("now+") || lower.startsWith("now-")) {
            boolean plus = lower.charAt(3) == '+';
            String amountPart = lower.substring(4); // es: "15s", "2h", "10m"

            char unit = amountPart.charAt(amountPart.length() - 1);
            long value = Long.parseLong(amountPart.substring(0, amountPart.length() - 1));

            return switch (unit) {
                case 'h' -> plus ? now.plusHours(value) : now.minusHours(value);
                case 'm' -> plus ? now.plusMinutes(value) : now.minusMinutes(value);
                case 's' -> plus ? now.plusSeconds(value) : now.minusSeconds(value);
                default -> throw new IllegalArgumentException("Unità non supportata nel token: " + token);
            };
        }

        return OffsetDateTime.parse(token);
    }
}
