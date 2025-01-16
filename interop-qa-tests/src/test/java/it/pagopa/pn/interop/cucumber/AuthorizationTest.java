package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
                "html:target/cucumber-report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"wait_for_fix"})
@IncludeTags({"client_create", "client_delete", "client_key_content_read", "client_key_delete","client_key_read", "client_key_upload",
        "client_user_remove", "client_keys_listing", "client_listing", "client_purpose_add", "client_purpose_remove", "client_read",
        "client_user_add", "client_user_remove", "client_users_listing"})
public class AuthorizationTest {
}
