package it.pagopa.pn.cucumber.steps.ioMock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IoMockMessageIdHelper {

    public static final String IO_MESSAGE_ID_REGEX = "^MOCK-([A-Za-z0-9_]+)-(\\d+)-([A-Za-z0-9_]+)$";
    public static final Pattern IO_MESSAGE_ID_PATTERN = Pattern.compile(IO_MESSAGE_ID_REGEX);

    private IoMockMessageIdHelper() {
        // Private constructor for utility class
    }

    public static String buildMockId(String sequenceName, long submitMillis, String randomSuffix) {
        return String.format("MOCK-%s-%d-%s", sequenceName, submitMillis, randomSuffix);
    }

    public static String buildMockIdWithOffset(String sequenceName, long offsetMillisAgo) {
        long submitMillis = System.currentTimeMillis() - offsetMillisAgo;
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return buildMockId(sequenceName, submitMillis, randomSuffix);
    }

    public static String buildMockIdForT0(String sequenceName) {
        // T0: elapsed < 30s (offset 2 secondi nel passato)
        return buildMockIdWithOffset(sequenceName, 2_000L);
    }

    public static String buildMockIdForT1(String sequenceName) {
        // T1: 30s <= elapsed < 60s (offset 40 secondi nel passato)
        return buildMockIdWithOffset(sequenceName, 40_000L);
    }

    public static String buildMockIdForT2(String sequenceName) {
        // T2: 60s <= elapsed < 90s (offset 70 secondi nel passato)
        return buildMockIdWithOffset(sequenceName, 70_000L);
    }

    public static String buildMockIdForT3(String sequenceName) {
        // T3: elapsed >= 90s (offset 100 secondi nel passato)
        return buildMockIdWithOffset(sequenceName, 100_000L);
    }

    public static boolean isValidMockId(String messageId) {
        if (messageId == null) {
            return false;
        }
        return IO_MESSAGE_ID_PATTERN.matcher(messageId).matches();
    }

    public static MockIdComponents parseMockId(String messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId non può essere null");
        }
        Matcher matcher = IO_MESSAGE_ID_PATTERN.matcher(messageId);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Identificativo mock non valido rispetto al pattern: " + messageId);
        }
        String sequence = matcher.group(1);
        long submitMillis = Long.parseLong(matcher.group(2));
        String randomSuffix = matcher.group(3);
        return new MockIdComponents(sequence, submitMillis, randomSuffix);
    }

    public static SnapshotState calculateCumulativeState(long submitMillis, long currentMillis) {
        long elapsed = currentMillis - submitMillis;
        String status = elapsed < 30_000L ? "ACCEPTED" : "PROCESSED";
        String readStatus = null;
        String paymentStatus = null;

        if (elapsed >= 30_000L && elapsed < 60_000L) {
            readStatus = "UNREAD";
            paymentStatus = "NOT_PAID";
        } else if (elapsed >= 60_000L && elapsed < 90_000L) {
            readStatus = "READ";
            paymentStatus = "NOT_PAID";
        } else if (elapsed >= 90_000L) {
            readStatus = "READ";
            paymentStatus = "PAID";
        }

        return new SnapshotState(status, readStatus, paymentStatus, elapsed);
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class MockIdComponents {
        private final String sequenceName;
        private final long submitMillis;
        private final String randomSuffix;

        public String getRandToken() {
            return randomSuffix;
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class SnapshotState {
        private final String status;
        private final String readStatus;
        private final String paymentStatus;
        private final long elapsedMillis;
    }
}
