package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo A – PA senza webhook (scenari #41-42, #44-61 del resoconto, tag {@code @qa19093_pa}; i webhook sono in
 * {@link Tmp_QA19093_WebhookTest}).
 * Da cancellare a chiusura analisi insieme ai tag {@code @qa19093_*} nelle feature.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_PaTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-pa.json," +
        "html:target/cucumber-report-qa19093-pa.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ExcludeTags({"ignore"})
@IncludeTags({"qa19093_pa"})
public class Tmp_QA19093_PaTest {
}
