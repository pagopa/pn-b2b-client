package it.pagopa.pn.interop.cucumber.utility;

import it.pagopa.pn.interop.cucumber.utility.functionalint.Task;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestore del ciclo di vita dei test di una Feature.
 */
@Slf4j
public class FeatureLifecycleManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger activeScenarios = new AtomicInteger(0);
    private volatile boolean setupPerformed = false;

    /**
     * Garantisce che il setup avvenga prima di qualsiasi test.
     * Se il setup è già in corso, i thread successivi attendono.
     */
    public void before(Task task) throws Exception {
        activeScenarios.incrementAndGet();

        // Double-Checked Locking per il setup
        if (!setupPerformed) {
            lock.lock();
            try {
                if (!setupPerformed) {
                    log.info("Running task before test...");
                    task.run();
                    setupPerformed = true;
                }
            } catch (Exception e) {
                setupPerformed = false;
                throw e;
            } finally {
                lock.unlock();
            }
            log.info("Task before test done.");
        } else {
            log.info("Task before test already done. Skipping...");
        }
    }

    /**
     * Garantisce che il teardown avvenga solo se non ci sono altri scenari attivi.
     * Resetta lo stato per permettere una nuova inizializzazione se arrivano altri test.
     */
    public void after(Task task) throws Exception {
        String skippingMsg = "Other tests ongoing, can't perform task after tests. Skipping...";

        // Double-Checked Locking per il teardown
        if (activeScenarios.decrementAndGet() == 0) {
            lock.lock();
            try {
                // Secondo controllo: verifica che nessun test sia entrato nel frattempo
                if (activeScenarios.get() == 0) {
                    log.info("Running task after test...");
                    task.run();
                    setupPerformed = false;
                } else {
                    log.info(skippingMsg);
                }
            } finally {
                lock.unlock();
            }
        } else {
            log.info(skippingMsg);
        }
    }
}