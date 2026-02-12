package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.interop.generated.openapi.clients.probing.model.EserviceStateFE;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;

import java.time.*;
import java.util.List;

public class ProbingUtils {

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");

    public record EserviceFilters(String eserviceName, String producerName, Integer versionNumber,
                                  List<EserviceStateFE> states) {
    }

    public static OffsetTime italyToday(LocalTime time) {
        // “oggi” secondo l’Italia
        LocalDate todayRome = LocalDate.now(ROME);

        // offset corretto per oggi in Italia (CET/CEST)
        ZoneOffset offset = ZonedDateTime.of(todayRome, time, ROME).getOffset();

        return OffsetTime.of(time.withNano(0), offset);
    }

    public static boolean matchesAllFilters(SearchEserviceContent item, EserviceFilters f) {
        if (item == null) return false;

        if (f.eserviceName() != null) {
            String actual = safeTrim(item.getEserviceName());
            String expected = safeTrim(f.eserviceName());
            if (!containsIgnoreCase(actual, expected)) return false;
        }

        if (f.producerName() != null) {
            String actual = safeTrim(item.getProducerName());
            String expected = safeTrim(f.producerName());
            if (!equalsIgnoreCase(actual, expected)) return false;
        }

        if (f.versionNumber() != null) {
            Integer actual = item.getVersionNumber();
            if (actual == null || !actual.equals(f.versionNumber())) return false;
        }

        if (f.states() != null && !f.states().isEmpty()) {
            EserviceStateFE actual = item.getState();
            return actual != null && f.states().contains(actual);
        }

        return true;
    }

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    }

    private static boolean containsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.toLowerCase().contains(b.toLowerCase());
    }
}
