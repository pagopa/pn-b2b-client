package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Da lanciare a mano poco prima del deploy della funzionalità in un ambiente: prepara le notifiche da riprendere con TracciaturaIndagineInLocoTest.
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
