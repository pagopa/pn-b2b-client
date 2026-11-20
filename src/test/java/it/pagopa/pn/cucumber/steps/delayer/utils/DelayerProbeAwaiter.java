package it.pagopa.pn.cucumber.steps.delayer.utils;

import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.common.polling.PollingTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
public final class DelayerProbeAwaiter {

    private DelayerProbeAwaiter() {
    }

    /**
     * Ripete i probe non ancora riusciti finche' tutti terminano senza eccezioni, oppure scade {@code config}.
     *
     * @throws IllegalStateException allo scadere del polling, con l'elenco delle chiavi i cui probe falliscono ancora
     */
    public static <K> void awaitAllSucceed(String label, Map<K, Runnable> probes, PollingConfig config) {
        Set<K> pending = new LinkedHashSet<>(probes.keySet());
        Predicate<Set<K>> allSucceeded = Set::isEmpty;
        try {
            // Le eccezioni dei probe vanno gestite qui: il Poller non ritenta in caso di eccezione della chiamata.
            Poller.pollUntil(() -> {
                        pending.removeIf(key -> tryRun(label, key, probes.get(key)));
                        return pending;
                    },
                    allSucceeded,
                    config);
        } catch (PollingTimeoutException e) {
            throw new IllegalStateException("[" + label + "] Non disponibili allo scadere del polling: " + pending, e);
        }
    }

    private static <K> boolean tryRun(String label, K key, Runnable probe) {
        try {
            probe.run();
            return true;
        } catch (Exception e) {
            log.info("[{}] {} non ancora disponibile: {}", label, key, e.getMessage());
            return false;
        }
    }
}
