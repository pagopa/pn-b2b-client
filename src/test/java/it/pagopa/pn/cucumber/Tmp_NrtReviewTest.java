package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO riusabile — un’unica classe per rilanci isolati durante review NRT/UAT
 * (prefisso {@code Tmp_}). Da cancellare solo a chiusura analisi, non a ogni scenario.
 * <p>
 * Per ogni sessione: aggiorna {@code @SelectClasspathResource}, filtro nome e tag.
 * Stato attuale: RIR fail — wait {@code SEND_ANALOG_FEEDBACK} {@code ATTEMPT_0} (allineato a PG).
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_NrtReviewTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/workflowAr/AvanzamentoNotifichePFAnalogicoAR.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/workflowAr/AvanzamentoNotificheB2bPFPGMultiAnalogicoAR.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*(B2B_TIMELINE_ANALOG_RIR_2|B2B_TIMELINE_ANALOG_RIR_5|B2B_TIMELINE_MULTI_ANALOG_RIR_2).*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"workflowAnalogico"})
public class Tmp_NrtReviewTest {
}
