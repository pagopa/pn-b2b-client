package it.pagopa.pn.interop.cucumber;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
        @ConfigurationParameter(key = "api.m2m.version", value = "V3"),
        @ConfigurationParameter(key = "api.mode", value = "RIGHT_FIT"),
})
@ExcludeTags({"wait_for_fix"})
@IncludeTags({// M2M
    "m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices", "m2m-agreements-parte2-luglio",
    "m2m-parte2-agosto-rilascio1", "m2m-parte2-agosto-rilascio2", "m2m-parte2-settembre",
    "m2m-parte2-ottobre", "m2mEservices"})
public class M2MV3Test {
}
