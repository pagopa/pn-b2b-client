package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Rilascio asincrono, preparazione: da lanciare a mano prima di un rilascio, non fa parte dell'esecuzione ordinaria.
 * Crea le notifiche e le ferma con le sequence bloccate, annotando nel log gli IUN (pn.ripresa.iun.&lt;chiave&gt;=&lt;IUN&gt;)
 * da passare dopo il rilascio a TracciaturaIndagineInLocoTest, che altrimenti salta gli scenari di ripresa.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/tracciaturaIndagineInLoco")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ExcludeTags({"ignore"})
@IncludeTags({"rilascioAsincronoPre"})
public class TracciaturaIndagineInLocoPreRilascioTest {

}
