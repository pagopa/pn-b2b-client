package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2bDeleghe.feature")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-lite-Annullamento-Deleghe-TEST-19092023-report.html," +
                "json:target/cucumber-lite-Annullamento-Deleghe-TEST-19092023-report.json"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps"),

})
@ExcludeTags({"ignore"})
@IncludeTags({"Annullamento"})
public class CucumberLiteIntegrationAnnullamentoDelegheTest {
}
