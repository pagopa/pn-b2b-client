package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Verifica rollback GPD di {@code B2B_ASYNC_8_PF} sotto concorrenza, dopo migrazione a
 * {@code PnPollingStrategy.PAYMENT_INFO} (Awaitility; timing {@code PAYMENT_INFO} ≈ 45×2s = 90s, early-exit):
 * <ul>
 *   <li>B2B_ASYNC_8_PF — target; nei log {@code GPD amount changed}</li>
 *   <li>B2B_ASYNC_15_PF — carico GPD fino a SEND_SIMPLE_REGISTERED_LETTER</li>
 *   <li>B2B_ASYNC_11_PF — carico GPD multipagamento</li>
 * </ul>
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
        value = ".*B2B_ASYNC_8_PF.*|.*B2B_ASYNC_15_PF.*|.*B2B_ASYNC_11_PF.*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"Async"})
public class Tmp_Async8PfUatReviewTest {
}
