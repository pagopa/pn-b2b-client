package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * QA-18544 / PST QA-18546: verifica degli output Paper Tracker in modalità DRY.
 *
 * <p>Predisporre Paper Tracker in DRY per i prodotti interessati; per la 890
 * rispettare il filtro EC attivo e la configurazione OCR prevista dalle sequenze.
 * Il runner seleziona i casi ma non modifica la configurazione dell'ambiente.</p>
 * <pre>
 * ./mvnw -Dtest=PaperTrackerOutputsDryTest test
 * ./mvnw -Dtest=PaperTrackerOutputsDryTest -Dcucumber.execution.dry-run=true test
 * </pre>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/paperTracker")
@IncludeTags("paperTrackerOutputsDry")
@ExcludeTags({"ignore", "paperTrackerOutputsRun"})
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,"
        + "json:target/cucumber-paper-tracker-outputs-dry.json,"
        + "html:target/cucumber-paper-tracker-outputs-dry.html")
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "false")
public class PaperTrackerOutputsDryTest {
}
