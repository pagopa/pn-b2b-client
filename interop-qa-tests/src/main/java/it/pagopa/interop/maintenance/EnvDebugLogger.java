package it.pagopa.interop.maintenance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/* Logger delle variabili d'ambiente del sistema. Utile per operazioni di debug remoto (e.g. quando il test viene
 * eseguito attraverso workflow Github).
 * Si noti che è necessario attivare il debug level per i log di questa classe. */
@Slf4j
@Component
public class EnvDebugLogger {

    @PostConstruct
    public void logEnvironmentVariableNames() {
        log.debug("========================================================================");
        log.debug("         DEBUG: ELENCO CHIAVI DELLE VARIABILI D'AMBIENTE DISPONIBILI     ");
        log.debug("========================================================================");

        Map<String, String> env = System.getenv();

        // Estraiamo solo i nomi delle chiavi e le ordiniamo alfabeticamente
        env.keySet().stream()
                .sorted()
                .forEach(key -> log.debug("Disponibile variabile d'ambiente: {}", key));

        log.debug("========================================================================");
        log.debug("Totale variabili rilevate: {}", env.size());
        log.debug("========================================================================");
    }
}