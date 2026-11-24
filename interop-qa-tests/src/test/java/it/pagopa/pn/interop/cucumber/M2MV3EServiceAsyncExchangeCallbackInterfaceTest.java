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
                        "json:target/cucumber-report-m2mv3.json," +
                        "html:target/cucumber-report-m2mv3.html," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V3;" +
                        "api.mode=RIGHT_FIT;" +
                        "api.set=M2M;" +
                        "api.bff.version=V1"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),

        // abilita parallelismo JUnit
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@IncludeTags({"m2m-apiv3-eservice-async-exchange-callback-interface"})
public class M2MV3EServiceAsyncExchangeCallbackInterfaceTest {
}
