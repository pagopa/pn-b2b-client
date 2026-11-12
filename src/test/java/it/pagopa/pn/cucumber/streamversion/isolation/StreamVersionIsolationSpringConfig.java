package it.pagopa.pn.cucumber.streamversion.isolation;

import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pn.cucumber.streamversion.context.StreamVersionContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Configurazione Spring minima, senza dipendenze remote, per la sola verifica di isolamento.
 * Non va inclusa nella glue insieme a it.pagopa.pn.cucumber.steps: ammessa una sola @CucumberContextConfiguration.
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = StreamVersionContext.class)
public class StreamVersionIsolationSpringConfig {
}
