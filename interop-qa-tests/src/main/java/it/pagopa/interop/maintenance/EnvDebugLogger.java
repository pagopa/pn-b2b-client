package it.pagopa.interop.maintenance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/* TODO 25/05/2026: ad uso temporaneo per verificare eventuali problemi con la feature Adeguamento analisi del
*   rischio, il quale dovrà fare pesante affidamento sul workflow Github. Questa classe sarà eventualmente usata
*   per facilitare operazioni di debug remoto.
*   A sviluppo concluso, si valuterà se mantenere questa classe per uso futuro - rendendola eventualmente disattivabile
*   attraverso configurazione - o rimuoverla. */
@Slf4j
@Component
public class EnvDebugLogger {

    @PostConstruct
    public void logEnvironmentVariableNames() {
        log.info("========================================================================");
        log.info("         DEBUG: ELENCO CHIAVI DELLE VARIABILI D'AMBIENTE DISPONIBILI     ");
        log.info("========================================================================");

        Map<String, String> env = System.getenv();

        // Estraiamo solo i nomi delle chiavi e le ordiniamo alfabeticamente
        env.keySet().stream()
                .sorted()
                .forEach(key -> log.info("Disponibile variabile d'ambiente: {}", key));

        log.info("========================================================================");
        log.info("Totale variabili rilevate: {}", env.size());
        log.info("========================================================================");
    }
}