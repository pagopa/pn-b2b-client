package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;
import static it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext.SCENARIO_IDS_PROPERTY;

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
        value = "concurrent"
)
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "true")
@ConfigurationParameter(key = PARALLEL_CONFIG_STRATEGY_PROPERTY_NAME, value = "fixed")
// Ceiling: >= partecipanti al gate; non serve allinearlo al numero esatto di id.
@ConfigurationParameter(key = PARALLEL_CONFIG_FIXED_PARALLELISM_PROPERTY_NAME, value = "50")
@ConfigurationParameter(
        key = SCENARIO_IDS_PROPERTY,
        value = "DELAYER-TC1,DELAYER-TC2,DELAYER-TC3,DELAYER-TC4,DELAYER-TC5"
)
@ExcludeTags({"ignore"})
@IncludeTags({
        "delayer1",
        "delayer2",
        "delayer3",
        "delayer4",
        "delayer5"
})
public class DelayerParallelTest {
}
