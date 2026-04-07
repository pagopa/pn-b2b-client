package it.pagopa.pn.cucumber;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectFile;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectFile("target/failed.txt")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-report.json,html:target/cucumber-report.html," +
        "it.pagopa.pn.cucumber.steps.RerunGuardPlugin")
public class RerunFailedTestSuite {
}
