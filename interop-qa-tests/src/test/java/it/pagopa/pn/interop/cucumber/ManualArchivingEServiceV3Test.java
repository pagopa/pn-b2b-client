package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," + "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty," +
                "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html," +
                "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                "api.m2m.version=V3;" +
                "api.mode=RIGHT_FIT;" +
                "api.set=M2M;" +
                "api.bff.version=V1"
)
@ExcludeTags({"wait_for_fix"})
@IncludeTags({"m2m-v3-manual-archiving-eservice"})
public class ManualArchivingEServiceV3Test {
}
