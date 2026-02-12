package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
        @ConfigurationParameter(key = "api.mode", value = "AUTO"),
        @ConfigurationParameter(key = "api.m2m.version", value = "v3"),
        @ConfigurationParameter(key = "api.bff.version", value = "v1")
})
@ExcludeTags({"wait_for_fix"})
@IncludeTags({"m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices"})
public class M2MV3Test {
}
