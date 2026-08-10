package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameters({
        @ConfigurationParameter(
                key = PLUGIN_PROPERTY_NAME,
                value = "pretty," +
                        "json:target/cucumber-report-nrt-bff-m2mv2.json," +
                        "html:target/cucumber-report-nrt-bff-m2mv2.html," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V2;" +
                        "api.mode=RIGHT_FIT;" +
                        "api.set=M2M;" +
                        "api.bff.version=V1"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),

        // abilita parallelismo JUnit
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),
})
@ExcludeTags({"wait_for_fix", "ignore", "adeguamento-analisi-rischio", "certifiedDiscreteAttributeFlagOn", "nuovi-operatori-off"})
@IncludeTags({
        // BFF
        "agreement", "attribute", "descriptor", "document", "eservice", "purpose", "daily_calls_update_request",
        "purpose_latest_risk_analysis", "purpose_risk_analysis", "incaricato", "capofila", "selfcare",
        "app-edit-ff-on", "llgg", "e-service-template", "e-service-template-receive-bff", "purposeTemplate",
        "client_admin", "client", "catalog", "producer", "DPoPSuite", "tenant", "voucher", "dailyCallsThreshold",
        "e-service-async", "e-service-template-async", "voucher_async", "devToolsClientAssertion",
        "manual-archiving-eservice", "notification-manual-archiving-eservice", "certifiedDiscreteAttributeFlagOff",
        "nuovi-operatori","crudNotification", "document-url-description", "document-type-check",

        // M2M
        "m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices", "m2m-agreements-parte2-luglio",
        "m2m-parte2-agosto-rilascio1", "m2m-parte2-agosto-rilascio2", "m2m-parte2-settembre",
        "m2m-parte2-ottobre", "m2mEservices", "e-service-template-receive-m2m", "m2m-client", "m2m-purpose-client",
        "m2m-incaricato", "m2m-events", "e-service-template-m2m-version-get", "eservice_published_delegation"

})
public class NrtTest {
}
