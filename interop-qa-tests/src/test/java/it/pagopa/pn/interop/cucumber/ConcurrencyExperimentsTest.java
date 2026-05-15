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

        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.strategy", value = "fixed"),
        @ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.parallelism", value = "30"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),

        @ConfigurationParameter(key = "cucumber.execution.execution-mode.scenario", value = "concurrent"),
        @ConfigurationParameter(key = "cucumber.execution.exclusive-resources.vincolato.read-write", value = "@concurrency-exp_vincolato"),
})
@ExcludeTags({"wait_for_fix", "ignore"})
@IncludeTags({"concurrency-exp"})
//Test utili solo per verificare i meccanismi di gestione della concorrenza, non hanno alcun legame coi test di dominio
public class ConcurrencyExperimentsTest {

}
