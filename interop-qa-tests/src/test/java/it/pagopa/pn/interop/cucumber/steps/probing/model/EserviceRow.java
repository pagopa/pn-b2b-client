package it.pagopa.pn.interop.cucumber.steps.probing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;

import static it.pagopa.pn.interop.cucumber.steps.probing.utils.ProbingUtils.italyToday;

@Getter
@Setter
@AllArgsConstructor
public class EserviceRow {
    private Long id;
    private UUID eserviceId;
    private UUID versionId;
    private String eserviceName;
    private String producerName;
    private String eserviceTechnology; // "REST" | "SOAP"
    private List<String> basePath;     // nello script: array con 1 stringa
    private List<String> audience;
    private String state;
    private int versionNumber;
    private int lockVersion;
    private boolean probingEnabled;
    private OffsetTime pollingStartTime;
    private OffsetTime pollingEndTime;
    private int pollingFrequency;

    // Nuovo: riflette ok/error/random dello script
    private Outcome outcome;

    public enum Outcome {OK, ERROR, RANDOM}

    // ------------------------
    // Costanti default (come nello script)
    // ------------------------
    private static final OffsetTime POLLING_START_DEFAULT = italyToday(LocalTime.of(8, 0, 0));
    private static final OffsetTime POLLING_END_DEFAULT = italyToday(LocalTime.of(17, 0, 0));
    private static final int POLLING_FREQUENCY_DEFAULT = 15;
    private static final boolean PROBING_ENABLED_DEFAULT = false;

    private static final String STATE_DEFAULT = "ACTIVE";
    private static final int VERSION_NUMBER_DEFAULT = 1;
    private static final int LOCK_VERSION_DEFAULT = 0;

    private static final String REST_PREFIX = "/rest/interop/probing/";
    private static final String SOAP_PREFIX = "/soap/interop/probing/";

    private static final String DEFAULT_BASE_HOST = "http://probing-be-eservice-mock.qa:8080";

    // ------------------------
    // Convenience: stato endpoint
    // ------------------------
    public boolean isOk() {
        return outcome == Outcome.OK;
    }

    public boolean isRandom() {
        return outcome == Outcome.RANDOM;
    }

    public boolean isKo() {
        return outcome == Outcome.ERROR;
    } // KO = ERROR

    // ------------------------
    // Factory methods
    // ------------------------

    /**
     * Come nello script: outcome deterministico in base a i (gs) e ai count.
     */
    public static EserviceRow atIndex(long i, int okCount, int errorCount, int randomCount, String baseHost) {
        if (i <= 0) throw new IllegalArgumentException("Index i must be >= 1");
        int total = okCount + errorCount + randomCount;
        if (total <= 0) throw new IllegalArgumentException("Total eservices count must be > 0");
        if (i > total) throw new IllegalArgumentException("Index i must be <= total (" + total + ")");

        String resolvedHost = (baseHost == null || baseHost.isBlank()) ? DEFAULT_BASE_HOST : baseHost;

        UUID eserviceId = uuidFromLongSeed(i);
        UUID versionId = uuidFromLongSeed(1_000_000_000L + i);

        String eserviceName = nameFromIndex(i);
        String producerName = "Producer " + (((i - 1) % 50) + 1);

        // Script: CASE WHEN (gs % 2) = 0 THEN 'SOAP' ELSE 'REST'
        boolean isEven = (i % 2) == 0;
        String technology = isEven ? "SOAP" : "REST";

        Outcome outcome = outcomeForIndex(i, okCount, errorCount);

        // Script: v_base_host + (/soap|/rest) + (ok|error|random) + '/status'
        String endpointPrefix = isEven ? SOAP_PREFIX : REST_PREFIX;
        String outcomePath = switch (outcome) {
            case OK -> "ok";
            case ERROR -> "error";
            case RANDOM -> "random";
        };

        List<String> basePath = List.of(
                resolvedHost + endpointPrefix + outcomePath + "/status"
        );

        List<String> audience = List.of(
                "AUD_" + (((i - 1) % 20) + 1)
        );

        return new EserviceRow(
                i,
                eserviceId,
                versionId,
                eserviceName,
                producerName,
                technology,
                basePath,
                audience,
                STATE_DEFAULT,
                VERSION_NUMBER_DEFAULT,
                LOCK_VERSION_DEFAULT,
                PROBING_ENABLED_DEFAULT,
                POLLING_START_DEFAULT,
                POLLING_END_DEFAULT,
                POLLING_FREQUENCY_DEFAULT,
                outcome
        );
    }

    /**
     * Overload "comodo": usa i default dello script per host e count.
     * (Se vuoi, puoi anche rimuoverlo e forzare sempre i parametri.)
     */
    public static EserviceRow atIndex(long i) {
        // default come tuo script di esempio: ok=1, error=0, random=1
        return atIndex(i, 1, 0, 1, DEFAULT_BASE_HOST);
    }

    public static EserviceRow fromName(String eserviceName, int okCount, int errorCount, int randomCount, String baseHost) {
        return atIndex(indexFromName(eserviceName), okCount, errorCount, randomCount, baseHost);
    }

    public static EserviceRow fromName(String eserviceName) {
        return atIndex(indexFromName(eserviceName));
    }

    // ------------------------
    // Utilities pubbliche
    // ------------------------

    public static String nameFromIndex(long i) {
        if (i <= 0) throw new IllegalArgumentException("Index i must be >= 1");
        return "ESVC-" + String.format("%08d", i);
    }

    public static int indexFromName(String eserviceName) {
        if (eserviceName == null) throw new IllegalArgumentException("Name is null");
        if (!eserviceName.matches("^ESVC-\\d{8}$")) {
            throw new IllegalArgumentException("Invalid name format. Expected ESVC-XXXXXXXX (8 digits).");
        }
        return Integer.parseInt(eserviceName.substring(5));
    }

    /**
     * Replica della CASE dello script:
     * WHEN gs <= okCount THEN ok
     * WHEN gs <= okCount + errorCount THEN error
     * ELSE random
     */
    public static Outcome outcomeForIndex(long i, int okCount, int errorCount) {
        if (i <= okCount) return Outcome.OK;
        if (i <= (long) okCount + errorCount) return Outcome.ERROR;
        return Outcome.RANDOM;
    }

    // ------------------------
    // Replica di uuid_from_int(seed)
    // ------------------------

    private static UUID uuidFromLongSeed(long seed) {
        String hex32 = md5Hex(Long.toString(seed)); // md5(seed::text) su Postgres
        String uuidStr =
                hex32.substring(0, 8) + "-" +
                        hex32.substring(8, 12) + "-" +
                        hex32.substring(12, 16) + "-" +
                        hex32.substring(16, 20) + "-" +
                        hex32.substring(20, 32);
        return UUID.fromString(uuidStr);
    }

    private static String md5Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
