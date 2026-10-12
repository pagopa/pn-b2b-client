package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Verifica locale, senza chiamate remote, della versione dello stream impostata dalla suite (V28).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("streamversion-isolation")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-streamversion-isolation-v28.json"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.streamversion.hooks.v28," +
                "it.pagopa.pn.cucumber.streamversion.isolation"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@IncludeTags({"streamVersionIsolationV28"})
public class StreamVersionIsolationV28Test {
}
