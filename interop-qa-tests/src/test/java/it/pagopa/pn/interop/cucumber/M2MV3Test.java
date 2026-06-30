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
                        "json:target/cucumber-report-m2mv3.json," +
                        "html:target/cucumber-report-m2mv3.html," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V3;" +
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
@ExcludeTags({"wait_for_fix", "ignore", "adeguamento-analisi-rischio"})
@IncludeTags({// M2M
        "m2m-agreements", "m2m-purposes", "m2m-attributes", "m2m-eservices", "m2m-agreements-parte2-luglio",
        "m2m-parte2-agosto-rilascio1", "m2m-parte2-agosto-rilascio2", "m2m-parte2-settembre",
        "m2m-parte2-ottobre", "m2mEservices", "m2m-apiv3-users", "m2m-apiv3-producer-keychains",
        "m2m-apiv3-client-keychains", "m2m-apiv3-client-consumer", "m2m-apiv3-purposes-threshold", "m2m-client",
        "eservice_published_delegation_m2m_v3", "m2m-purpose-template-events", "eServiceTemplateLink_m2mv3"
})
public class M2MV3Test {

}
