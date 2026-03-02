package it.pagopa.pn.cucumber.steps.pa.strategies;

import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public final class ExpectedErrorMessageStrategies {

    private ExpectedErrorMessageStrategies() {}

    private static final Map<String, Function<String, String>> ERROR_STRATEGIES =
        Map.ofEntries(
                entry("RANGE_MAX_LAT", value -> "Validation errors: [format attribute \"double\" not supported, numeric instance is greater than the required maximum (maximum: 90, found: " + value + ")]"),
                entry("RANGE_MIN_LAT", value -> "Validation errors: [numeric instance is lower than the required minimum (minimum: -90, found: " + value + "), format attribute \"double\" not supported]"),
                entry("RANGE_MAX_LON", value -> "Validation errors: [format attribute \"double\" not supported, numeric instance is greater than the required maximum (maximum: 180, found: " + value + ")]"),
                entry("RANGE_MIN_LON", value -> "Validation errors: [format attribute \"double\" not supported, numeric instance is lower than the required minimum (minimum: -180, found: " + value + ")]"),
                entry("NULL_LAT", value -> "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]"),
                entry("NULL_LON", value -> "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]"),
                entry("NULL_LAT_LON", value -> "Validation errors: [instance type (null) does not match any allowed primitive type (allowed: [\"integer\",\"number\"]), format attribute \"double\" not supported]")
        );

    public static String build(String errorType, String value) {
        return ERROR_STRATEGIES.getOrDefault(errorType, v -> "UNKNOWN_ERROR").apply(value);
    }
}