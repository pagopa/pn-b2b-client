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
    private UUID eserviceId;
    private UUID versionId;
    private String eserviceName;
    private String producerName;
    private String eserviceTechnology;
    private List<String> basePath;
    private List<String> audience;
    private String state;
    private int versionNumber;
    private int lockVersion;
    private boolean probingEnabled;
    private OffsetTime pollingStartTime;
    private OffsetTime pollingEndTime;
    private int pollingFrequency;

    // ------------------------
    // Costanti (come nello script)
    // ------------------------
    private static final List<String> TECHNOLOGIES = List.of("REST", "SOAP");

    private static final OffsetTime POLLING_START = italyToday(LocalTime.of(9, 0, 0));
    private static final OffsetTime POLLING_END = italyToday(LocalTime.of(18, 0, 0));
    private static final int POLLING_FREQUENCY = 15;
    private static final boolean PROBING_ENABLED = false;

    private static final String STATE_DEFAULT = "ACTIVE";
    private static final int VERSION_NUMBER_DEFAULT = 1;
    private static final int LOCK_VERSION_DEFAULT = 0;

    // ------------------------
    // Factory methods
    // ------------------------

    /**
     * Crea la riga deterministica corrispondente a gs=i (1-based).
     */
    public static EserviceRow atIndex(int i) {
        if (i <= 0) throw new IllegalArgumentException("Index i must be >= 1");

        UUID eserviceId = uuidFromLongSeed(i);
        UUID versionId = uuidFromLongSeed(1_000_000_000L + i);

        String eserviceName = nameFromIndex(i);
        String producerName = "Producer " + (((i - 1) % 50) + 1);
        String technology = TECHNOLOGIES.get((i - 1) % TECHNOLOGIES.size());

        List<String> basePath = List.of(
                "/api/v1/" + eserviceName.toLowerCase(),
                "/health"
        );

        List<String> audience = List.of(
                "AUD_" + (((i - 1) % 20) + 1)
        );

        return new EserviceRow(
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
                PROBING_ENABLED,
                POLLING_START,
                POLLING_END,
                POLLING_FREQUENCY
        );
    }

    /**
     * Crea la riga a partire dal nome deterministico "ESVC-XXXXXXXX".
     */
    public static EserviceRow fromName(String eserviceName) {
        return atIndex(indexFromName(eserviceName));
    }

    // ------------------------
    // Utilities pubbliche
    // ------------------------

    public static String nameFromIndex(int i) {
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

