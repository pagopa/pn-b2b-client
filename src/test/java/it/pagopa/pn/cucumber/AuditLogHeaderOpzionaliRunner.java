package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Lancia i 7 feature file di QA-19752 (uno per ciascuna API privata/audit log, con i suoi 7 Scenario indipendenti,
 * uno per ciascuna combinazione non vuota degli header opzionali cx_id/cx_type/uid).
 * <p>
 * {@code junit-platform.properties} imposta globalmente {@code cucumber.execution.execution-mode.feature=same_thread},
 * cosi' gli scenari di una stessa Feature girano in sequenza sullo stesso thread: e' una scelta che riguarda il resto
 * della suite (altre feature potrebbero condividere stato tra i loro scenari) e non va cambiata globalmente solo per
 * questi 7 file. La sovrascriviamo pero' qui, con un {@code @ConfigurationParameter} che ha priorita' su quel file
 * solo per l'esecuzione di questo Suite: ogni scenario crea la propria notifica ed e' indipendente dagli altri, quindi
 * puo' tranquillamente girare in parallelo anche con gli scenari della propria stessa Feature.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/auditLog")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-audit-log-header-opzionali.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@IncludeTags({"qa19752AuditHeaderOpzionali"})
public class AuditLogHeaderOpzionaliRunner {
}
