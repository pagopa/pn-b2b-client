package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Gli scenari comuni non indicano la versione: la imposta l'hook incluso nella glue.
 * La glue comprende i sottopackage, quindi va indicato il solo package hooks.v23.
 * ResaAlMittente.feature è incluso perché lo selezionava WebhookV23V25Test, che ora delega a questa suite.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook")
@SelectClasspathResource("it/pagopa/pn/cucumber/ResaAlMittente.feature")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html," +
                "json:target/cucumber-report-webhook-v23.json," +
                "html:target/cucumber-report-webhook-v23.html"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps," +
                "it.pagopa.pn.cucumber.streamversion.hooks.v23"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"webhookV23", "webhookStream", "streamV23"})
public class WebhookV23Test {
}
