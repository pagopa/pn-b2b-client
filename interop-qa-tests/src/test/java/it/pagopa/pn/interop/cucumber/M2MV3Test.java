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
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),

        /* FIXME configurazione non funzionante, al momento si ripiega su quella di ApiProfileConfiguration */
        @ConfigurationParameter(key = "api.m2m.version", value = "V3"),
        @ConfigurationParameter(key = "api.mode", value = "RIGHT_FIT"),
        @ConfigurationParameter(key = "api.set", value = "M2M"),
})
@ExcludeTags({"wait_for_fix"})
@IncludeTags({// M2M
    "m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices", "m2m-agreements-parte2-luglio",
    "m2m-parte2-agosto-rilascio1", "m2m-parte2-agosto-rilascio2", "m2m-parte2-settembre",
    "m2m-parte2-ottobre", "m2mEservices"})
public class M2MV3Test {
}
