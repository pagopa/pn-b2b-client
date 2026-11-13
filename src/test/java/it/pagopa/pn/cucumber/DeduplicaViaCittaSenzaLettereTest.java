package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * QA-18853: validates PN-21776 for street and/or city without letters returned by the national registries.
 * Selects scenario B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_15.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/nationalRegistries/AvanzamentoNotificheB2bPFAnalogicoNR.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,json:target/cucumber-report.json,html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(key = FILTER_NAME_PROPERTY_NAME,
        value = ".*\\[B2B_TIMELINE_ANALOG_VALIDAZIONE_DEDUPLICA_15\\].*")
@IncludeTags({"validazioneDeduplica"})
@ExcludeTags({"ignore"})
public class DeduplicaViaCittaSenzaLettereTest {
}
