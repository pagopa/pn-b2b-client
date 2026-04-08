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
})
@ExcludeTags({"wait_for_fix", "nrtC-waitForFix"})
@IncludeTags({
        // BFF
        "agreement", "attribute", "descriptor", "document", "eservice", "purpose", "daily_calls_update_request",
        "purpose_latest_risk_analysis", "purpose_risk_analysis", "incaricato", "capofila", "selfcare",
        "app-edit-ff-on", "llgg", "e-service-template", "e-service-template-receive-bff", "purposeTemplate",
        "client_admin", "client", "catalog", "producer", "DPoPSuite", "tenant", "voucher",

        // M2M
        "m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices", "m2m-agreements-parte2-luglio",
        "m2m-parte2-agosto-rilascio1", "m2m-parte2-agosto-rilascio2", "m2m-parte2-settembre",
        "m2m-parte2-ottobre", "m2mEservices", "e-service-template-receive-m2m", "m2m-purpose-client", "m2m-incaricato",
        "e-service-template-m2m-version-get", "m2m-events", "purposeTemplateGet-filtered", "m2m-client"

})
public class AbstractNrtTest {
}
