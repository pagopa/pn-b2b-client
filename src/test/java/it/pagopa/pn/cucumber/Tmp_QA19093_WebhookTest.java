package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo A – webhook a lunga durata (scenari #37-40, #43 del resoconto).
 * Da cancellare a chiusura analisi.
 * <p>
 * Si selezionano solo le feature v23: gli stessi ID {@code B2B-STREAM_ES1.*} esistono anche in v24…v30_informal.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_WebhookTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook/v1/AvanzamentoNotificheWebhookB2bPF.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook/v23/WebhookV23Delete.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook/v23/WebhookV23Disable.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/webhook/v23/WebhookV23ConsumeFailure.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-webhook.json," +
        "html:target/cucumber-report-qa19093-webhook.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = "^\\[(B2B-STREAM_TIMELINE_14"
                + "|B2B-STREAM_ES1\\.1_119"
                + "|B2B-STREAM_ES1\\.1_118"
                + "|B2B-STREAM_ES1\\.5_139"
                + "|B2B-STREAM_TIMELINE_24"
                + ")\\].*"
)
@ExcludeTags({"ignore"})
public class Tmp_QA19093_WebhookTest {
}
