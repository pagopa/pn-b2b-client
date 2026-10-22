package it.pagopa.pn.cucumber.steps;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.*;
import java.util.List;

/**
 * Plugin per bloccare l'esecuzione della suite di rerun se il file failed.txt non esiste, è vuoto o contiene più di una certa soglia di test falliti.
 * La soglia massima di test falliti può essere configurata tramite la variabile d'ambiente RERUN_MAX (default 10).
 */
@Slf4j
public class RerunGuardPlugin implements ConcurrentEventListener {
    private static final String FAILED_FILE = "target/failed.txt";

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, this::onStart);
    }

    private void onStart(TestRunStarted event) {
        log.info("Validazione rerun suite...");
        try {
            Path path = Paths.get(FAILED_FILE);
            // file non esiste
            if (!Files.exists(path)) {
                stopExecution("failed.txt non trovato");
            }
            List<String> lines = Files.readAllLines(path);
            // file vuoto
            if (lines.isEmpty()) {
                stopExecution("failed.txt vuoto");
            }
            int failedTests = lines.size();
            // recupero soglia
            int maxAllowed = Integer.parseInt(
                    System.getenv().getOrDefault("RERUN_MAX", "30")
            );
            if (failedTests > maxAllowed) {
                stopExecution("troppi test falliti (" + failedTests + ")");
            }
            log.info("Rerun autorizzato ({} test)", failedTests);
        } catch (Exception e) {
            stopExecution("errore durante validazione rerun");
        }
    }

    private void stopExecution(String reason) {
        log.error("Rerun bloccato: {}", reason);
        // blocca la suite
        throw new RuntimeException("Rerun non consentito: " + reason);
    }
}