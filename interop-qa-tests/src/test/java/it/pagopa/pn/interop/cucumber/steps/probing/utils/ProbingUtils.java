package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.interop.generated.openapi.clients.probing.model.EserviceStateFE;
import it.pagopa.interop.generated.openapi.clients.probing.model.SearchEserviceContent;

import java.time.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProbingUtils {
    public record PollingPolicy(int maxTry, long sleepMs) {
    }

    private static final ZoneId ROME = ZoneId.of("Europe/Rome");

    public static OffsetTime italyToday(LocalTime time) {
        // “oggi” secondo l’Italia
        LocalDate todayRome = LocalDate.now(ROME);

        // offset corretto per oggi in Italia (CET/CEST)
        ZoneOffset offset = ZonedDateTime.of(todayRome, time, ROME).getOffset();

        return OffsetTime.of(time.withNano(0), offset);
    }

    public static boolean isWithinExpectedWindow(OffsetDateTime now, OffsetDateTime expectedStartDate, OffsetDateTime expectedEndDate) {
        if (now == null) {
            throw new IllegalArgumentException("now must not be null");
        }

        if (expectedStartDate != null && now.isBefore(expectedStartDate)) {
            return false;
        }

        return expectedEndDate == null || !now.isAfter(expectedEndDate);
    }

    public static void waitUntilExpectedWindowStarts(OffsetDateTime expectedStart) {
        if (expectedStart == null) return;

        OffsetDateTime now = OffsetDateTime.now();
        if (!now.isBefore(expectedStart)) return;

        // Attesa “di allineamento” allo start: cap per non addormentare troppo il test
        Duration toWait = Duration.between(now, expectedStart);

        // cap: max 10s
        Duration capped = toWait.compareTo(Duration.ofSeconds(10)) > 0 ? Duration.ofSeconds(10) : toWait;

        sleepQuietly(capped);
    }

    public static PollingPolicy computePollingPolicy(OffsetDateTime expectedStart, OffsetDateTime expectedEnd, Integer expectedFrequency) {
        OffsetDateTime now = OffsetDateTime.now();

        // Deadline: se ho endDate, uso quella; altrimenti uso un fallback ragionevole (es. 30s)
        OffsetDateTime deadline = (expectedEnd != null) ? expectedEnd : now.plusSeconds(30);

        // Se la deadline è già passata, comunque concedi un minimo di tempo (es. 5s) per non avere maxTry=0
        if (deadline.isBefore(now)) {
            deadline = now.plusSeconds(5);
        }

        long totalMs = Duration.between(now, deadline).toMillis();

        // Sleep: guidato dalla frequency, con limiti.
        // Assunzione: expectedFrequency espressa in secondi
        long sleepMs;
        if (expectedFrequency == null || expectedFrequency <= 0) {
            sleepMs = 1_000L; // default
        } else {
            long freqMs = TimeUnit.SECONDS.toMillis(expectedFrequency.longValue());
            // polling ~ ogni metà periodo, ma con min/max
            sleepMs = Math.max(500L, Math.min(2_000L, freqMs / 2));
        }

        int maxTry = (int) Math.max(1, Math.ceil(totalMs / (double) sleepMs));

        // cap di sicurezza per non avere loop infiniti in casi strani (es. endDate molto avanti)
        maxTry = Math.min(maxTry, 120); // max 120 tentativi

        return new PollingPolicy(maxTry, sleepMs);
    }

    public static void sleepQuietly(Duration d) {
        try {
            Thread.sleep(Math.max(0L, d.toMillis()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public record EserviceFilters(String eserviceName, String producerName, Integer versionNumber,
                                  List<EserviceStateFE> states) {
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
