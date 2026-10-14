package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * QA-17189: validates PN-20973 for incomplete foreign addresses returned by VAS.
 * Selects scenarios B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_11 through _14.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/nationalRegistries/AvanzamentoNotificheB2bPFAnalogicoNR.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-report.json,html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(key = FILTER_NAME_PROPERTY_NAME,
        value = ".*\\[B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_(11|12|13|14)\\].*")
@IncludeTags({"validazioneDeduplica"})
@ExcludeTags({"ignore"})
public class ValidazioneDeduplicaTest {
}
