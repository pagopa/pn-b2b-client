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

    // abilita parallelismo JUnit
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),

    // abilita parallelismo Cucumber
    @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
})
@ExcludeTags({"wait_for_fix"})

/* Esclusa la feature archiviazione documentale, notifiche, probing, tracing */
@IncludeTags({"client_admin", "client", "catalog", "producer", "DPoPSuite", "e-service-template-instances-suffix",
        "m2m-purpose-client", "m2m-incaricato", "e-service-template-m2m-version-get", "m2m-events",
        "purposeTemplateGet-filtered", "tenant", "voucher"})
public class NrtComplementareTest {
}
