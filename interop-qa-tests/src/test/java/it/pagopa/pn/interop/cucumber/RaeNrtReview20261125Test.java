package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Reruns the 37 cases selected for review from the NRT report of 25 November 2026.
 */
@Suite
@SelectClasses({RaeNrtReview20261125Test.V2.class, RaeNrtReview20261125Test.V3.class})
public class RaeNrtReview20261125Test {

    /** Runs the cases reported by the BFF/M2M V2 suite. */
    @Suite
    @IncludeEngines("cucumber")
    @SelectClasspathResource("it/pagopa/pn/cucumber")
    @ConfigurationParameters({
            @ConfigurationParameter(
                    key = PLUGIN_PROPERTY_NAME,
                    value = "pretty," +
                            "json:target/cucumber-report-rae-nrt-20261125-v2.json," +
                            "html:target/cucumber-report-rae-nrt-20261125-v2.html," +
                            "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                            "api.m2m.version=V2;" +
                            "api.mode=RIGHT_FIT;" +
                            "api.set=M2M;" +
                            "api.bff.version=V1"
            ),
            @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
            @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
            @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),
            @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
    })
    @ExcludeTags({"wait_for_fix", "ignore", "adeguamento-analisi-rischio", "certifiedDiscreteAttributeFlagOn",
            "nuovi-operatori-off", "data-pagination", "m2m-ra-review", "m2m-ra-events"})
    @IncludeTags("rae-nrt-20261125-v2")
    public static class V2 {
    }

    /** Runs the cases reported by the M2M V3 suite. */
    @Suite
    @IncludeEngines("cucumber")
    @SelectClasspathResource("it/pagopa/pn/cucumber")
    @ConfigurationParameters({
            @ConfigurationParameter(
                    key = PLUGIN_PROPERTY_NAME,
                    value = "pretty," +
                            "json:target/cucumber-report-rae-nrt-20261125-v3.json," +
                            "html:target/cucumber-report-rae-nrt-20261125-v3.html," +
                            "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                            "api.m2m.version=V3;" +
                            "api.mode=RIGHT_FIT;" +
                            "api.set=M2M;" +
                            "api.bff.version=V1"
            ),
            @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
            @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
            @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),
            @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
    })
    @ExcludeTags({"wait_for_fix", "ignore", "adeguamento-analisi-rischio"})
    @IncludeTags("rae-nrt-20261125-v3")
    public static class V3 {
    }
}
