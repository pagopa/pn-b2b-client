package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Isola {@code B2B_ASYNC_8_PF}: verifica se con polling GPD prolungato (120×2s)
 * l'amount torna a 100 dopo {@code NOTIFICATION_CANCELLED}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/b2b/pf/AvanzamentoNotificheAsyncB2bPF.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(key = FILTER_NAME_PROPERTY_NAME, value = ".*B2B_ASYNC_8_PF.*")
@ExcludeTags({"ignore"})
@IncludeTags({"Async"})
public class Tmp_Async8PfUatReviewTest {
}
