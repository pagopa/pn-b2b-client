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
        @ConfigurationParameter(key = "api.m2m.version", value = "V3"),
        @ConfigurationParameter(key = "api.mode", value = "RIGHT_FIT"),
})
@ExcludeTags({"wait_for_fix", "ignore"})
@IncludeTags({// M2M
    "m2m-apiv3-users", "m2m-apiv3-producer-keychains" })
public class M2MV3DeltaTest {
}
