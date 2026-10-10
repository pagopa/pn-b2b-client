package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

/**
 * Verifica locale, senza chiamate remote, della versione dello stream impostata dalla suite (V29).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("streamversion-isolation")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-streamversion-isolation-v29.json"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.streamversion.hooks.v29," +
                "it.pagopa.pn.cucumber.streamversion.isolation"),
        @ConfigurationParameter(key = Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@IncludeTags({"streamVersionIsolationV29"})
public class StreamVersionIsolationV29Test {
}
