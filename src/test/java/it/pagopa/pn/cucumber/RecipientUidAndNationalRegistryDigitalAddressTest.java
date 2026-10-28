package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * QA-18495: riesecuzione mirata degli 11 KO relativi allo UID Galileo e dei 4 KO Digital Address.
 * https://pagopa.atlassian.net/browse/QA-18495
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty,json:target/recipient-uid-national-registry-digital-address/cucumber-report.json,"
                + "html:target/recipient-uid-national-registry-digital-address/cucumber-report.html")
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "false")
@IncludeTags("recipientUidAndNationalRegistryDigitalAddress")
@ExcludeTags({"ignore", "realNR"})
public class RecipientUidAndNationalRegistryDigitalAddressTest {
}
