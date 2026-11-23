package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * TEMPORANEO — validazione QA-19093 (migrazione {@code catch (HttpStatusCodeException)} → {@code ApiCallHelper}).
 * Gruppo B – RADD (scenari #6-17 del resoconto).
 * Da cancellare a chiusura analisi.
 * <p>
 * {@code RADD_ANAGRAFICA_CRUD_V2_23} e {@code _24} compaiono due volte nella feature V2: si selezionano solo
 * le varianti PATCH e PUT Selective.
 * <p>
 * Runner: {@code -Dtest=it.pagopa.pn.cucumber.Tmp_QA19093_RaddTest}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/AnagraficaRaddAlternative/RaddAlternativeAngraficaSportelliV2.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/AnagraficaRaddAlternative/RaddAlternativeAngraficaSportelli.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/AnagraficaRaddAlternative/RaddCapCoverage.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddAlternative/RaddAlternative.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddTechnicalAnnex/RaddTechnicalAnnex.feature")
@SelectClasspathResource("it/pagopa/pn/cucumber/Radd/RaddFsu/RaddFsu.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-qa19093-radd.json," +
        "html:target/cucumber-report-qa19093-radd.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@ConfigurationParameter(
        key = FILTER_NAME_PROPERTY_NAME,
        value = "^\\[(RADD_ANAGRAFICA_CRUD_V2_27"
                + "|RADD_ANAGRAFICA_CRUD_V2_15"
                + "|RADD_ANAGRAFICA_CSV_2"
                + "|RADD_ANAGRAFICA_CSV_STATO_3"
                + "|RADD_API_COPERTURA_CAP_CREAZIONE_3"
                + "|RADD_API_COPERTURA_CAP_MODIFICA_17"
                + "|RADD-ALT_ACT-47"
                + "|RADD-ALT_ACT-94"
                + "|ADEG-RADD-TRANS_ACT-5"
                + "|B2B_RADD_DOC-UP-1"
                + ")\\].*"
                + "|^\\[RADD_ANAGRAFICA_CRUD_V2_23\\] - PATCH.*"
                + "|^\\[RADD_ANAGRAFICA_CRUD_V2_24\\] - PUT Selective.*"
)
@ExcludeTags({"ignore"})
public class Tmp_QA19093_RaddTest {
}
