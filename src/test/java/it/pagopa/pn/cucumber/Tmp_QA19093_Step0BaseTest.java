package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Fase 0 / Base: SharedSteps + step consumer generici (scenari #1-5 del resoconto).
 * Da cancellare a chiusura analisi.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_Step0BaseTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/baseFunction/validation/SyncValidation.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/ApiB2bDestinatariStrutturati.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/e2e/AnalogSend.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-step0.json," +
        "html:target/cucumber-report-qa19093-step0.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = "^\\[(B2B-PA-SYNC_VALIDATION_WRONG_FILEKEY"
                + "|B2B-PA-SYNC_VALIDATION_70"
                + "|B2B-PA-ANNULLAMENTO_38"
                + "|B2B-DEST-USER-ATTR_10"
                + "|B2B_ANALOG_SEND_4"
                + ")\\].*"
)
@ExcludeTags({"ignore"})
public class Tmp_QA19093_Step0BaseTest {
}
