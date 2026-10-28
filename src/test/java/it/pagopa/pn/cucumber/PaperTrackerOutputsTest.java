package it.pagopa.pn.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * QA-18544 / PST QA-18546: persistenza degli output RUN e regressione DRY.
 *
 * <p>Verifica della selezione completa senza eseguire i flussi remoti:</p>
 * <pre>
 * ./mvnw -Dtest=PaperTrackerOutputsTest -Dcucumber.execution.dry-run=true test
 * </pre>
 *
 * <p>Per l'esecuzione reale, usare due passaggi dopo aver predisposto l'ambiente:</p>
 * <pre>
 * ./mvnw -Dtest=PaperTrackerOutputsTest -Dcucumber.filter.tags=@paperTrackerOutputsRun test
 * ./mvnw -Dtest=PaperTrackerOutputsTest -Dcucumber.filter.tags=@paperTrackerOutputsDry test
 * </pre>
 *
 * <p>Paper Tracker sceglie RUN o DRY per prodotto e data di attivazione. La selezione
 * dei tag non modifica questa configurazione: i due gruppi dello stesso prodotto
 * richiedono configurazioni distinte. Per la 890 rispettare anche il filtro EC,
 * disattivo per RUN e attivo per DRY, e la configurazione OCR prevista dalle sequenze.
 * Senza filtro il runner seleziona entrambi i gruppi, utile per il dry-run completo.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("it/pagopa/pn/cucumber/paperTracker")
@IncludeTags("paperTrackerOutputs")
@ExcludeTags("ignore")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.pn.cucumber.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,"
        + "json:target/cucumber-paper-tracker-outputs.json,"
        + "html:target/cucumber-paper-tracker-outputs.html")
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "false")
public class PaperTrackerOutputsTest {
}
