package it.pagopa.pn.cucumber.streamversion.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.pn.cucumber.steps.pa.webhookVersions.StreamVersion;
import lombok.Getter;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Versione dello stream definita dalla test suite per lo scenario in esecuzione.
 * La valorizza, una sola volta, l'hook del package it.pagopa.pn.cucumber.streamversion.hooks.vXX
 * incluso nella glue del runner; la leggono gli step che non indicano una versione esplicita.
 * La glue comprende anche i sottopackage: il runner deve indicare il solo package hooks.vXX,
 * non streamversion o streamversion.hooks, altrimenti verrebbero caricati tutti gli hook.
 */
@Component
@ScenarioScope(proxyMode = ScopedProxyMode.NO)
public class StreamVersionContext {

    /**
     * Identificativo dell'istanza, assegnato alla costruzione: permette di verificare
     * che ogni scenario disponga di un proprio contesto.
     */
    @Getter
    private final String instanceId = UUID.randomUUID().toString();

    private StreamVersion streamVersion;

    public synchronized void initialize(StreamVersion version) {
        if (version == null) {
            throw new IllegalArgumentException("La versione dello stream della suite non può essere nulla");
        }
        if (streamVersion != null) {
            throw new IllegalStateException(String.format(
                    "Versione dello stream già inizializzata a %s: tentativo di inizializzarla a %s. " +
                            "Verificare che il runner includa un solo package it.pagopa.pn.cucumber.streamversion.hooks.vXX",
                    streamVersion, version));
        }
        streamVersion = version;
    }

    public synchronized boolean isInitialized() {
        return streamVersion != null;
    }

    public synchronized StreamVersion getRequiredStreamVersion() {
        if (streamVersion == null) {
            throw new IllegalStateException(
                    "Versione dello stream non definita dalla suite: lo step non specifica una versione " +
                            "e il runner non include un package it.pagopa.pn.cucumber.streamversion.hooks.vXX");
        }
        return streamVersion;
    }
}
