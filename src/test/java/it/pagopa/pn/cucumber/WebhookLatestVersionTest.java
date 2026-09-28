package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Scenari della V29: versionati (@webhookV29) e comuni (@webhookStream, @streamV29), eseguiti con la versione
 * impostata dall'hook in glue (streamversion.hooks.v29).
 * Esegue anche, una sola volta, gli scenari che usano sempre la V10 (@webhookV10Fisso).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html," +
                "json:target/cucumber-report-webhook-v29.json," +
                "html:target/cucumber-report-webhook-v29.html"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps," +
                "it.pagopa.pn.cucumber.streamversion.hooks.v29"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"webhookV29", "webhookStream", "streamV29", "webhookLatestVersion", "webhookV10Fisso"})
public class WebhookLatestVersionTest {
}
