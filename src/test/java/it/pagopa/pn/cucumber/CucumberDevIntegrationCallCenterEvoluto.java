package it.pagopa.pn.cucumber;


import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/serviceDesk/ApiServiceDesk.feature")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-20231003-UAT-ServiceDesk.json"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "html:target/cucumber-report-20231003-UAT-ServiceDesk.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})

@ExcludeTags({"ignore","dp"})
public class CucumberDevIntegrationCallCenterEvoluto {
}
