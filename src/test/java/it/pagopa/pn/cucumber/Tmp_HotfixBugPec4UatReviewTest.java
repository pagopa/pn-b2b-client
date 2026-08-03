package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Riproduce {@code B2B-TIMELINE_HOTFIX-BUG-PEC_4} (F24 expected 20 was 0 su allegati cartacei).
 * Modalità URL-only: log {@code PaperEngage PRESIGN-ONLY} / {@code F24 PRESIGN-ONLY}
 * (~5 min di vita). File (se accessibile sul runner):
 * {@code target/tmp-hotfix-pec4-pdfs/<IUN>/presigned-urls-paper.txt}.
 * Passa subito l'XML così si possono scaricare i PDF prima della scadenza.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/workflow890/AvanzamentoNotifichePFAnalogico890.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*B2B-TIMELINE_HOTFIX-BUG-PEC_4.*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"workflowAnalogico"})
public class Tmp_HotfixBugPec4UatReviewTest {
}
