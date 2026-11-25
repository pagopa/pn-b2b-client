package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Suite isolata dei test ancora in {@code Delayer.feature} (con Step Function / E2E).
 * Ogni elemento in {@code @IncludeTags} = uno scenario.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "json:target/cucumber-report.json,html:target/cucumber-report.html"
)
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "it.pagopa.pn.cucumber.steps"
)
@ConfigurationParameter(
        key = EXECUTION_MODE_FEATURE_PROPERTY_NAME,
        value = "same_thread"
)
@ExcludeTags({"ignore"})
@IncludeTags({
        "delayer6",
        "delayer7",
        "delayer9",
        "delayer12",
        "delayer13",
        "delayer15",
        "delayer16"
})
public class DelayerTest {
}
