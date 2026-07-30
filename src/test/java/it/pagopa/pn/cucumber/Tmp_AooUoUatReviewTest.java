package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Fonte di verità: NRT UAT ({@code NrtTest_uat}). Isola i KO AOO/UO della baseline:
 * <ul>
 *   <li>B2B-AOO-UO_3 / _5 — SEND_DIGITAL_DOMICILE presente ma details non combaciano</li>
 *   <li>B2B-AOO-UO_8 / _10 — SEND_COURTESY_MESSAGE presente ma details non combaciano</li>
 * Nei log verificare se digitalAddress resta OK-pec / provaemail (atteso) oppure sequence / provaemail2 (pollution).
 * Esecuzione {@code same_thread} per ridurre race su addressbook Galileo.
**/
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/AooUo/AooUoB2b.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*B2B-AOO-UO_(3|5|8|10)\\].*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"AOO_UO"})
public class Tmp_AooUoUatReviewTest {
}
