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
                        "it.pagopa.pn.interop.cucumber.plugins.SetTenantKindPropsPlugin," +
                        "it.pagopa.pn.interop.cucumber.plugins.SetApiProfilePropsPlugin:" +
                        "api.m2m.version=V2;" +
                        "api.mode=RIGHT_FIT;" +
                        "api.set=M2M;" +
                        "api.bff.version=V1;"
        ),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.interop.cucumber.steps"),

        // abilita parallelismo JUnit
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false"),
        @ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "false"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "same_thread"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.fixed.parallelism", value = "1"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
})
@ExcludeTags({"wait_for_fix", "ignore"})
@IncludeTags({"debug"})
// FIXME utile solo per operazioni di debug per la feature Adeguamento analisi del rischio, rimuovere
public class DEBUG {
}
