package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.pn.interop.cucumber.steps.probing.model.EserviceRow;

import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class EserviceRowAllocator {

    private static final EnumMap<EserviceRow.Outcome, AtomicInteger> COUNTERS =
            new EnumMap<>(EserviceRow.Outcome.class);

    static {
        COUNTERS.put(EserviceRow.Outcome.OK, new AtomicInteger(0));
        COUNTERS.put(EserviceRow.Outcome.ERROR, new AtomicInteger(0));
        COUNTERS.put(EserviceRow.Outcome.RANDOM, new AtomicInteger(0));
    }

    private EserviceRowAllocator() {
    }

    public static long nextIndex(
            EserviceRow.Outcome outcome,
            int okCount,
            int errorCount,
            int randomCount
    ) {
        int total = okCount + errorCount + randomCount;
        if (total <= 0) throw new IllegalArgumentException("Total eservices count must be > 0");

        int base;
        int bucketSize;

        switch (outcome) {
            case OK -> {
                base = 1;
                bucketSize = okCount;
            }
            case ERROR -> {
                base = 1 + okCount;
                bucketSize = errorCount;
            }
            case RANDOM -> {
                base = 1 + okCount + errorCount;
                bucketSize = randomCount;
            }
            default -> throw new IllegalStateException("Unsupported outcome: " + outcome);
        }

        if (bucketSize <= 0) {
            throw new IllegalArgumentException("No " + outcome + " eservices available");
        }

        int offset = COUNTERS.get(outcome).getAndIncrement(); // thread-safe
        if (offset >= bucketSize)
            throw new IllegalStateException(
                    "Not enough " + outcome + " eservices: requested " + (offset + 1) + " but only " + bucketSize + " available"
            );

        return (long) base + offset;
    }

    public static void resetAll() {
        COUNTERS.values().forEach(c -> c.set(0));
    }
}

