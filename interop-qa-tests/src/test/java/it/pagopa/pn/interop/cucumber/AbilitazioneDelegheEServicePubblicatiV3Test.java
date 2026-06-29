package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," + "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
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
@ExcludeTags({"wait_for_fix", "eservice"})
@IncludeTags({"eservice_published_delegation_m2m_v3"})
public class AbilitazioneDelegheEServicePubblicatiV3Test {
}
