package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
    @ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,"
            + "json:target/cucumber-report-document-type-check.json,"
            + "html:target/cucumber-report-document-type-check.html,"
            + "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:"
            + "api.m2m.version=V3;"
            + "api.mode=RIGHT_FIT;"
            + "api.set=M2M;"
            + "api.bff.version=V1"
    ),
    @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),

    // abilita parallelismo JUnit
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
    @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),

    // abilita parallelismo Cucumber
    @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"wait_for_fix"})
@IncludeTags({"document-type-check"})
public class DocumentTypeCheckTest {
}

