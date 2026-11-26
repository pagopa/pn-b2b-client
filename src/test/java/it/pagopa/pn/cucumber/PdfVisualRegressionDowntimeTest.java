package it.pagopa.pn.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Visual regression scenarios that write to the environment (they open and close a downtime in pn-downtime-logs).
 * Kept out of {@link PdfVisualRegressionTest}: run this suite on purpose, one environment at a time.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualTest")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps"),
})
@ExcludeTags({"ignore"})
@IncludeTags({"visualTestDowntime"})
public class PdfVisualRegressionDowntimeTest {
}
