package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bPG.feature")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-lite-Annullamento-PG-TEST-19092023_1-report.html," +
                "json:target/cucumber-lite-Annullamento-PG-TEST-19092023_1-report.json"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"Annullamento"})
public class CucumberLiteIntegrationAnnullamentoPGTest {
}
