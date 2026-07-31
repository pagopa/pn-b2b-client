package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Isola B2B-PA-ANNULLAMENTO_28_1 (rilancio: _35 già PASS con EXTRA_RAPID).
 * Esecuzione {@code same_thread}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/annullamentoNotifica/AnnullamentoNotificheB2b.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*B2B-PA-ANNULLAMENTO_28_1.*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"Annullamento"})
public class Tmp_AnnullamentoUatReviewTest {
}
