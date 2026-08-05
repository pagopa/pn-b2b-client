package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO riusabile — un’unica classe per rilanci isolati durante review NRT/UAT
 * (prefisso {@code Tmp_}). Da cancellare solo a chiusura analisi, non a ogni scenario.
 * <p>
 * Per ogni sessione: aggiorna {@code @SelectClasspathResource}, filtro nome e tag.
 * Stato attuale: {@code B2B_ASYNC_8_PF} — diagnostica GPD (noticeCode / atMost / amount per poll).
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_NrtReviewTest}
 * <p>
 * Cercare nei log: {@code PAYMENT_INFO poll START}, {@code PAYMENT_INFO poll attempt},
 * {@code PAYMENT_INFO poll TIMEOUT}, {@code ASYNC GPD poll}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheAsyncB2bPF.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*B2B_ASYNC_8_PF.*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"Async"})
public class Tmp_NrtReviewTest {
}
