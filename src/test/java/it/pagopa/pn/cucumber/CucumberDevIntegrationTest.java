package it.pagopa.pn.cucumber;


import org.junit.platform.suite.api.*;


import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-20231003-UAT-light.json"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "html:target/cucumber-report-20231003-UAT-light.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
})
//@ExcludeTags({"svil","ignore"})
@IncludeTags({"testLite"})
public class CucumberDevIntegrationTest {
}
