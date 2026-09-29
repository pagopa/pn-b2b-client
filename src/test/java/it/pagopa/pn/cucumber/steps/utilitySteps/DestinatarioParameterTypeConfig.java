package it.pagopa.pn.cucumber.steps.utilitySteps;

import io.cucumber.java.ParameterType;
import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import it.pagopa.pn.client.b2b.pa.provider.DestinatarioRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import static it.pagopa.pn.client.b2b.pa.domain.Costanti.*;

/**
 * Glue Cucumber che registra la conversione automatica "nome destinatario -> {@link Destinatario}"
 * usata nei parametri degli step Gherkin (es. {@code Given "Mario Cucumber" riceve una notifica}).
 * La logica di risoluzione vera e propria vive in {@link DestinatarioRegistry} (src/main), qui
 * c'e' solo l'annotazione {@code @ParameterType}, che richiede Cucumber e quindi non puo' stare
 * in src/main.
 */
public class DestinatarioParameterTypeConfig {

    private final DestinatarioRegistry destinatarioRegistry;

    @Autowired
    public DestinatarioParameterTypeConfig(DestinatarioRegistry destinatarioRegistry) {
        this.destinatarioRegistry = destinatarioRegistry;
    }

    @ParameterType(MARIO_GHERKIN + "|" +
            MARIO_CUCUMBER + "|" +
            SIGNOR_CASUALE + "|" +
            ETTORE_FIERAMOSCA + "|" +
            CRISTOFORO_COLOMBO + "|" +
            LEONARDO_DA_VINCI + "|" +
            GHERKIN_SPA + "|" +
            CUCUMBER_SPA + "|" +
            CUCUMBER_SPA_B2B + "|" +
            GHERKIN_SRL + "|" +
            GHERKIN_SRL_B2B + "|" +
            CUCUMBER_SRL + "|" +
            GHERKIN_ANALOGIC + "|" +
            CUCUMBER_ANALOGIC + "|" +
            GHERKIN_IRREPERIBILE + "|" +
            CUCUMBER_SOCIETY + "|" +
            SIGNOR_GENERATO + "|" +
            GALILEO_GALILEI + "|" +
            NESSUNO + "|" +
            UTENZA_CON_INDIRIZZO_NON_VALIDO + "|" +
            UTENZA_CON_INDIRIZZO_VALIDO_ANPR + "|" +
            ALDA_MERINI + "|" +
            DINO_SAURO
    )
    public Destinatario destinatario(String name) {
        return destinatarioRegistry.destinatario(name);
    }
}
