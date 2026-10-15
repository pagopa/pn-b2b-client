package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner Cucumber per il Visual Regression Testing dei documenti PDF SEND.
 *
 * <p>Esegue tutti gli scenari con tag {@code @visualTest}.
 * Per eseguire solo i test smoke (senza golden master): usare il tag {@code @smoke}.</p>
 *
 * <p>I diff visivi vengono salvati in {@code target/pdf-visual-diff/} al fallimento.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/visualTest")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-report-visual.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,   value = "it.pagopa.pn.cucumber.steps")
@IncludeTags({"visualTest"})
public class PdfVisualRegressionTest {
}
