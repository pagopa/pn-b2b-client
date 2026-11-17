package it.pagopa.pn.cucumber.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Processa i report Cucumber JSON, mergia i risultati e genera report HTML.
 *
 * Logica di merge:
 * - Se uno scenario è passato nel rerun, sostituisce la versione fallita dal run iniziale
 * - Mantiene la traccia completa di tutti gli scenari da entrambi i run
 */
public class CucumberReportProcessor {

    private static final Logger LOGGER = Logger.getLogger(CucumberReportProcessor.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Configurazione file
    private static final Path REPORT_A = Paths.get("target/cucumber-report.json");
    private static final Path REPORT_B = Paths.get("target/cucumber-report-rerun.json");
    private static final Path MERGED_REPORT = Paths.get("target/cucumber-report-merged.json");
    private static final Path HTML_OUTPUT_DIR = Paths.get("target/cucumber-html-reports");

    // Chiavi JSON
    private static final String URI_KEY = "uri";
    private static final String ELEMENTS_KEY = "elements";
    private static final String NAME_KEY = "name";
    private static final String STEPS_KEY = "steps";
    private static final String RESULT_KEY = "result";
    private static final String STATUS_KEY = "status";
    private static final String STATUS_PASSED = "passed";

    private static final String SCENARIO_SEPARATOR = "||";

    /**
     * Processa i report Cucumber: merge, validazione e generazione HTML.
     *
     * @throws IOException se c'è un errore di lettura/scrittura file
     */
    public static void process() throws IOException {
        try {
            LOGGER.info("Inizio elaborazione report Cucumber");

            List<Map<String, Object>> featuresA = loadReport(REPORT_A);
            List<Map<String, Object>> featuresB = loadReport(REPORT_B);

            Map<String, ScenarioData> scenarioMap = mergeReports(featuresA, featuresB);
            List<Map<String, Object>> mergedFeatures = rebuildFeatureStructure(scenarioMap);

            writeReport(mergedFeatures);
            generateHtmlReport();

            LOGGER.info("Report elaborati con successo");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'elaborazione dei report", e);
            throw new IOException("Errore durante l'elaborazione dei report Cucumber", e);
        }
    }

    /**
     * Carica un report JSON da file.
     */
    private static List<Map<String, Object>> loadReport(Path reportPath) throws IOException {
        if (!Files.exists(reportPath)) {
            LOGGER.warning("File report non trovato: " + reportPath);
            return Collections.emptyList();
        }

        if (Files.size(reportPath) == 0) {
            LOGGER.warning("File report vuoto: " + reportPath);
            return Collections.emptyList();
        }

        try {
            return MAPPER.readValue(reportPath.toFile(),
                    new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore nel parsing del report: " + reportPath, e);
            return Collections.emptyList();
        }
    }

    /**
     * Merge due report: sovrascrive gli scenari falliti con versioni passate dal rerun.
     */
    private static Map<String, ScenarioData> mergeReports(
            List<Map<String, Object>> featuresA,
            List<Map<String, Object>> featuresB) {

        Map<String, ScenarioData> scenarioMap = new LinkedHashMap<>();

        mergeFeatures(featuresA, scenarioMap, "report principale");
        mergeFeatures(featuresB, scenarioMap, "rerun");

        LOGGER.info("Merge completato: " + scenarioMap.size() + " scenari totali");
        return scenarioMap;
    }

    /**
     * Merge una lista di features nella mappa degli scenari.
     */
    @SuppressWarnings("unchecked")
    private static void mergeFeatures(List<Map<String, Object>> features,
                                      Map<String, ScenarioData> scenarioMap,
                                      String source) {

        for (Map<String, Object> feature : features) {
            String uri = getString(feature, URI_KEY);
            List<Map<String, Object>> elements = (List<Map<String, Object>>)
                    feature.get(ELEMENTS_KEY);

            if (elements == null || elements.isEmpty()) {
                continue;
            }

            for (Map<String, Object> scenario : elements) {
                String scenarioName = getString(scenario, NAME_KEY);
                String key = buildScenarioKey(uri, scenarioName);

                boolean isPassed = isScenarioPassed(scenario);

                if (scenarioMap.containsKey(key)) {
                    // Scenario già presente: aggiorna solo se il nuovo è passato
                    ScenarioData existing = scenarioMap.get(key);
                    if (!existing.isPassed() && isPassed) {
                        LOGGER.fine("Aggiornamento scenario passato: " + key);
                        scenarioMap.put(key, new ScenarioData(uri, scenario, isPassed, source));
                    }
                } else {
                    // Primo scenario trovato
                    scenarioMap.put(key, new ScenarioData(uri, scenario, isPassed, source));
                }
            }
        }
    }

    /**
     * Verifica se uno scenario è completamente passato.
     */
    @SuppressWarnings("unchecked")
    private static boolean isScenarioPassed(Map<String, Object> scenario) {
        List<Map<String, Object>> steps = (List<Map<String, Object>>)
                scenario.get(STEPS_KEY);

        if (steps == null || steps.isEmpty()) {
            return false;
        }

        return steps.stream()
                .map(step -> (Map<String, Object>) step.get(RESULT_KEY))
                .filter(Objects::nonNull)
                .map(result -> getString(result, STATUS_KEY))
                .allMatch(status -> STATUS_PASSED.equalsIgnoreCase(status));
    }

    /**
     * Ricostruisce la struttura delle feature dal mappa degli scenari.
     */
    private static List<Map<String, Object>> rebuildFeatureStructure(
            Map<String, ScenarioData> scenarioMap) {

        Map<String, List<Map<String, Object>>> featuresByUri = new LinkedHashMap<>();

        for (ScenarioData scenarioData : scenarioMap.values()) {
            featuresByUri.computeIfAbsent(scenarioData.uri, k -> new ArrayList<>())
                    .add(scenarioData.scenario);
        }

        return featuresByUri.entrySet().stream()
                .map(entry -> buildFeatureMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Costruisce una feature map dalla lista di scenari.
     */
    private static Map<String, Object> buildFeatureMap(String uri,
                                                       List<Map<String, Object>> elements) {
        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put(URI_KEY, uri);
        feature.put(ELEMENTS_KEY, elements);
        feature.put(NAME_KEY, uri.replace("classpath:", ""));
        return feature;
    }

    /**
     * Scrive il report merged su file.
     */
    private static void writeReport(List<Map<String, Object>> mergedFeatures) throws IOException {
        MAPPER.writerWithDefaultPrettyPrinter()
                .writeValue(MERGED_REPORT.toFile(), mergedFeatures);
        LOGGER.info("Report merged salvato in: " + MERGED_REPORT);
    }

    /**
     * Genera il report HTML dal JSON merged.
     */
    private static void generateHtmlReport() {
        try {
            File outputDir = HTML_OUTPUT_DIR.toFile();
            if (!outputDir.exists()) {
                Files.createDirectories(HTML_OUTPUT_DIR);
            }

            Configuration config = new Configuration(outputDir, "PN B2B Tests");

            ReportBuilder reportBuilder = new ReportBuilder(
                    Collections.singletonList(MERGED_REPORT.toAbsolutePath().toString()),
                    config
            );

            reportBuilder.generateReports();
            LOGGER.info("Report HTML generato in: " + HTML_OUTPUT_DIR);

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore durante la generazione del report HTML", e);
        }
    }

    /**
     * Costruisce la chiave univoca dello scenario.
     */
    private static String buildScenarioKey(String uri, String scenarioName) {
        return uri + SCENARIO_SEPARATOR + scenarioName;
    }

    /**
     * Ottiene un valore string da una mappa.
     */
    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : "";
    }

    /**
     * Classe interna per rappresentare i dati di uno scenario.
     */
    private static class ScenarioData {
        private final String uri;
        private final Map<String, Object> scenario;
        private final boolean passed;
        private final String source;

        ScenarioData(String uri, Map<String, Object> scenario, boolean passed, String source) {
            this.uri = uri;
            this.scenario = scenario;
            this.passed = passed;
            this.source = source;
        }

        boolean isPassed() {
            return passed;
        }

        @Override
        public String toString() {
            return String.format("ScenarioData{uri='%s', passed=%s, source='%s'}",
                    uri, passed, source);
        }
    }
}
