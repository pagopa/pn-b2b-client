package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report.json," +
        "html:target/cucumber-report.html")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent")
@IncludeTags({"ricercaIndirizzoVas"})
@ExcludeTags({"ignore", "physicalAddressLookupDisabled"})

/**TODO VAS IMPORTANTE:
 * a seconda della modalità di calcolo per il costo della notifica impostata su ParameterStore,
 * andranno esclusi dalla run i test con le annotation
 * @technicalRefusalCostUniform
 * @technicalRefusalCostRecipient
 * @TODO modalitàDefault (nessuno dei due sopra, tag ancora da creare)
 */

public class RicercaIndirizzoVas {
}