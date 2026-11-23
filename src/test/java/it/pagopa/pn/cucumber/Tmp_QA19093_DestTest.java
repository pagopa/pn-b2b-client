package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo C – Destinatario / ServiceDesk / trasversali (scenari #18-36 del resoconto, tag {@code @qa19093_dest}).
 * Da cancellare a chiusura analisi insieme ai tag {@code @qa19093_*} nelle feature.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_DestTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-dest.json," +
        "html:target/cucumber-report-qa19093-dest.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ExcludeTags({"ignore"})
@IncludeTags({"qa19093_dest"})
public class Tmp_QA19093_DestTest {
}
