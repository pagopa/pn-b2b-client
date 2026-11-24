package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Fase 0 / Base: SharedSteps + step consumer generici (scenari #1-5 del resoconto, tag {@code @qa19093_step0}).
 * Da cancellare a chiusura analisi insieme ai tag {@code @qa19093_*} nelle feature.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_Step0BaseTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ExcludeTags({"ignore"})
@IncludeTags({"qa19093_step0"})
public class Tmp_QA19093_Step0BaseTest {
}
