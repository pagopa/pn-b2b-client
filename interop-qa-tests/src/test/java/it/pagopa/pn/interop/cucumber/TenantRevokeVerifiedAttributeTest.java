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

    // disabilita parallelismo JUnit
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false"),
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "same_thread"),

    // disabilita parallelismo Cucumber
    @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
})
@ExcludeTags({"wait_for_fix"})

@IncludeTags({"tenant_revoke_verified_attribute3", "tenant_revoke_verified_attribute4"})
public class TenantRevokeVerifiedAttributeTest {
}
