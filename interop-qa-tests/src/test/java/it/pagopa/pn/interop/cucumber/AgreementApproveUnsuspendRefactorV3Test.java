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
                        "json:target/cucumber-report-agreement-approve-unsuspend-refactor-v3.json," +
                        "html:target/cucumber-report-agreement-approve-unsuspend-refactor-v3.html," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V3;" +
                        "api.mode=RIGHT_FIT;" +
                        "api.set=M2M;" +
                        "api.bff.version=V1"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
})
@ExcludeTags({"wait_for_fix", "ignore"})
@IncludeTags({"m2m-agreement-approve-unsuspend-refactor"})
public class AgreementApproveUnsuspendRefactorV3Test {
}