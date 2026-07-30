package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_NAME_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Runner mirato: conteggio documenti RADD su ACT-62 + gemelli di confronto.
 * <ul>
 *   <li>ACT-62 — KO baseline (PG, solo PagoPA, IUN) attesi 5</li>
 *   <li>ACT-57 — PF, solo PagoPA, IUN (attesi 6)</li>
 *   <li>ACT-60 — PG, PagoPA+F24, QR (attesi 6)</li>
 *   <li>ACT-61 — PG, solo F24, QR (attesi 5)</li>
 *   <li>ACT-63 — PG, multi PagoPA+F24, IUN (attesi 8)</li>
 * </ul>
 * Nei log cercare {@code startTransactionResponse:} (stampato da {@code RaddAltSteps}).
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/RaddAlternative.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*RADD-ALT_ACT-(57|60|61|62|63).*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"raddAlt"})
public class Tmp_RaddAltAct62ReviewTest {
}
