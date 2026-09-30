package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Gli scenari comuni non indicano la versione: la imposta l'hook incluso nella glue.
 * La glue comprende i sottopackage, quindi va indicato il solo package hooks.v25.
 * ResaAlMittente.feature è incluso perché lo selezionava WebhookV23V25Test, che ora delega a questa suite.
 * Il nome non termina in Test: la suite è già eseguita da WebhookV23V25Test e Surefire non deve rilevarla una seconda volta.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook")
@SelectClasspathResource("it/pagopa/pn/cucumber/ResaAlMittente.feature")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html," +
                "json:target/cucumber-report-webhook-v25.json," +
                "html:target/cucumber-report-webhook-v25.html"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps," +
                "it.pagopa.pn.cucumber.streamversion.hooks.v25"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"webhookV25", "webhookStream", "streamV25"})
public class WebhookV25Suite {
}
