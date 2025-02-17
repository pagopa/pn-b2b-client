package it.pagopa.pn.cucumber;


import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/* FIXME 17/02/2025: suite temporanea per facilitare le NRT per il task QA-5470. Si intende
*   rimuoverla prima del branch su develop. */

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ExcludeTags({"ignore","uat","appIo", "integration","realNR","mockNormalizzatore","giacenza890Complex","raddAlternativeCsv"})
@IncludeTags({"workflowDigitale", "workflowAnalogico", "pagamentiMultipli","giacenza890Simplified",
            "Async", "f24", "version","AOO_UO", "Annullamento", "raddTechnicalAnnex", "raddAlt",
            "validation", "RetentionAllegati", "apiKeyManager", "downtimeLogs", "recuperoDisservizi",
            "legalFact", "letturaDestinatario", "raddAnagrafica", "raddAttoIntero", "restApiValidation"}) //TMP EXCLUDED: ,"partitaIva","raddAlt"
public class QA5470 {
}
