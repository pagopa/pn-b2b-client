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

/**TODO IMPORTANTE:
 * Prima del lancio della suite, accertarsi del valore della property ${pn.technical_refusal_cost_mode}
 * Se non presente ->
 * 1.1) @IncludeTags({"ricercaIndirizzoVas", "technicalRefusalCostDefault"})
 * 2.1) @ExcludeTags({"ignore", "physicalAddressLookupDisabled", "technicalRefusalCostUniform", "technicalRefusalCostRecipient"})
 * Se presente e valorizzata con UNIFORM ->
 * 2.1) @IncludeTags({"ricercaIndirizzoVas", "technicalRefusalCostUniform"})
 * 2.2) @ExcludeTags({"ignore", "physicalAddressLookupDisabled", "technicalRefusalCostRecipient", "technicalRefusalCostDefault"})
 * Se presente e valorizzata con RECIPIENT_BASED ->
 * 3.1) @IncludeTags({"ricercaIndirizzoVas", "technicalRefusalCostUniform"})
 * 3.2) @ExcludeTags({"ignore", "physicalAddressLookupDisabled", "technicalRefusalCostUniform", "technicalRefusalCostDefault"})
 */
public class RicercaIndirizzoVas {
}