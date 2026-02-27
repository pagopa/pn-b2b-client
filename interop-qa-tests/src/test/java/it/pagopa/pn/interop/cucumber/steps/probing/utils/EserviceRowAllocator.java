package it.pagopa.pn.interop.cucumber.steps.probing.utils;

import it.pagopa.pn.interop.cucumber.steps.probing.model.EserviceRow;

import java.util.BitSet;

public final class EserviceRowAllocator {

    private static final Object LOCK = new Object();
    private static BitSet used = new BitSet();
    private static int total = -1;

    private EserviceRowAllocator() {
    }

    public static void init(int okCount, int errorCount, int randomCount) {
        int t = okCount + errorCount + randomCount;
        if (t <= 0) throw new IllegalArgumentException("Total eservices count must be > 0");
        synchronized (LOCK) {
            // inizializza una sola volta o re-init se cambia il total (scegli tu la policy)
            if (total != t) {
                total = t;
                used = new BitSet(total + 1);
            }
        }
    }

    public static long nextAny(int okCount, int errorCount, int randomCount) {
        init(okCount, errorCount, randomCount);
        synchronized (LOCK) {
            int i = used.nextClearBit(1);
            if (i > total) {
                throw new IllegalStateException("No more eservices available (total=" + total + ")");
            }
            used.set(i);
            return i;
        }
    }

    public static long reservePreferred(long preferredIndex, int okCount, int errorCount, int randomCount) {
        init(okCount, errorCount, randomCount);
        if (preferredIndex < 1 || preferredIndex > total) {
            // fuori range: assegna un qualsiasi libero
            return nextAny(okCount, errorCount, randomCount);
        }
        synchronized (LOCK) {
            int p = (int) preferredIndex;
            if (!used.get(p)) {
                used.set(p);
                return p;
            }
            // già occupato: prendi il prossimo libero
            int i = used.nextClearBit(1);
            if (i > total) {
                throw new IllegalStateException("No more eservices available (total=" + total + ")");
            }
            used.set(i);
            return i;
        }
    }

    public static long nextByOutcome(
            EserviceRow.Outcome outcome,
            int okCount,
            int errorCount,
            int randomCount
    ) {
        init(okCount, errorCount, randomCount);

        int start;
        int size;
        switch (outcome) {
            case OK -> {
                start = 1;
                size = okCount;
            }
            case ERROR -> {
                start = 1 + okCount;
                size = errorCount;
            }
            case RANDOM -> {
                start = 1 + okCount + errorCount;
                size = randomCount;
            }
            default -> throw new IllegalStateException("Unsupported outcome: " + outcome);
        }
        if (size <= 0) throw new IllegalArgumentException("No " + outcome + " eservices available");

        int endInclusive = start + size - 1;

        synchronized (LOCK) {
            int i = used.nextClearBit(start);
            if (i > endInclusive) {
                throw new IllegalStateException("No more " + outcome + " eservices available (size=" + size + ")");
            }
            used.set(i);
            return i;
        }
    }

    /**
     * Se vuoi ripartire pulito tra run/feature.
     */
    public static void reset() {
        synchronized (LOCK) {
            if (total > 0) used.clear(1, total + 1);
        }
    }
}


