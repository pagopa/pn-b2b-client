package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO riusabile — un’unica classe per rilanci isolati durante review NRT/UAT
 * (prefisso {@code Tmp_}). Da cancellare solo a chiusura analisi, non a ogni scenario.
 * <p>
 * Per ogni sessione: aggiorna {@code @SelectClasspathResource}, filtro nome e tag.
 * Stato attuale: {@code B2B_TIMELINE_MULTI_RIS_2}.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_NrtReviewTest}
 * <p>
 * Execution mode: lasciare {@code concurrent} (come NRT) salvo motivo concreto per {@code same_thread}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/workflowDigitale/AvanzamentoNotificheB2bPFPGMultiDigitalRS.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*B2B_TIMELINE_MULTI_RIS_2.*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"workflowDigitale"})
public class Tmp_NrtReviewTest {
}
