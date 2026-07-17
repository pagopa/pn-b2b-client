package it.pagopa.pn.cucumber;

import it.pagopa.pn.cucumber.steps.delayer.model.DelayerSuiteContext;
import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

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
@ExcludeTags({"ignore"})
@IncludeTags({"delayerParallel"})
public class DelayerParallelTest {

    static {
        DelayerSuiteContext.configureParallelSuite(
                "DELAYER-TC1", "DELAYER-TC2", "DELAYER-TC3", "DELAYER-TC4", "DELAYER-TC5");
    }
}
