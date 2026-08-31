package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(
                key = PLUGIN_PROPERTY_NAME,
                value = "pretty," +
                        "json:target/cucumber-report-manual-archiving-cloning.json," +
                        "html:target/cucumber-report-manual-archiving-cloning.html," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V3;" +
                        "api.mode=RIGHT_FIT;" +
                        "api.set=M2M;" +
                        "api.bff.version=V1"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"wait_for_fix", "ignore"})
@IncludeTags("manual-archiving-cloning")
public class ManualArchivingCloningTest {
}