package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — da cancellare a fine analisi QA-16429 (prefisso {@code Tmp_}).
 * <p>
 * Verifica migrazione PDF verso {@code it.pagopa.common.util.PDFUtility}:
 * <ul>
 *   <li>{@code B2B-TIMELINE_HOTFIX-BUG-PEC_4} — {@code containsText(..., "F24", true)}</li>
 *   <li>{@code TEMPLATE-ENGINE_13} — {@code extractText} via {@code isValidPdf}</li>
 *   <li>{@code RADD_WAVE_8} — {@code matchesPattern} via {@code checkTypeAAR}</li>
 *   <li>{@code B2B-LEGALFACT_CONTENT_VERIFY_1} — {@code extractStructuredText} via {@code PnContentExtractor}</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/workflowNotifica/workflowAnalogico/workflow890/AvanzamentoNotifichePFAnalogico890.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/templateEngine/TemplateEngine.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/SperimentazioneRadd.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/baseFunction/contentVerify/LegalFactContentVerify.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "same_thread")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = ".*(B2B-TIMELINE_HOTFIX-BUG-PEC_4|TEMPLATE-ENGINE_13|RADD_WAVE_8\\]|B2B-LEGALFACT_CONTENT_VERIFY_1).*"
)
@ExcludeTags({"ignore"})
@IncludeTags({"workflowAnalogico", "templateEngine", "raddWave", "legalFact"})
public class Tmp_HotfixBugPec4UatReviewTest {
}
