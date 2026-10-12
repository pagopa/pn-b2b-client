package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Gli scenari comuni non indicano la versione: la imposta l'hook incluso nella glue.
 * La glue comprende i sottopackage, quindi va indicato il solo package hooks.v27.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html," +
                "json:target/cucumber-report-webhook-v27.json," +
                "html:target/cucumber-report-webhook-v27.html"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps," +
                "it.pagopa.pn.cucumber.streamversion.hooks.v27"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"webhookV27", "webhookStream", "streamV27"})
public class WebhookV27Test {
}
