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

        // Necessaria esecuzione sequenziale per evitare conflitti tra test che manipolano gli stessi files
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread"),
})
@ExcludeTags({"wait_for_fix", "ignore", "adeguamento-analisi-rischio", "certifiedDiscreteAttributeFlagOn", "nuovi-operatori-off"})
@IncludeTags({"disallineamento-nome-pacchetto"})
public class DisallineamentoNomePacchettoTest {
}
