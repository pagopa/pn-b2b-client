package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Scenari INIPEC con invio in DLQ: eseguiti in sequenza perché la lettura della DLQ condivisa
 * nasconde temporaneamente i messaggi (visibility timeout) agli altri scenari in esecuzione.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," + "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ExcludeTags({"ignore"})
@IncludeTags({"inipecDlq"})
public class INIPECGestionePuntualeDlqTest {
}
