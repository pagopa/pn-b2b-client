package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Genera un report CSV dai risultati dei test Cucumber.
 * Estrae gli scenari falliti dal report JSON e li esporta in formato CSV.
 */
public class CucumberCsvReportGenerator {

    private static final Logger LOGGER = Logger.getLogger(CucumberCsvReportGenerator.class.getName());

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern TEST_ID_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");

    private static final String INPUT_FILE = "target/cucumber-report-merged.json";
    private static final String OUTPUT_FILE = "target/cucumber-report.csv";

    private static final String[] CSV_HEADERS = {
            "#", "ID TEST", "ERROR LOG", "KEYS", "COMPONENTS", "RESULT", "PROCEDURE", "BUG SUITE", "BUG PRODOTTO"
    };

    private static final String STATUS_FAILED = "failed";
    private static final String ELEMENTS_KEY = "elements";
    private static final String STEPS_KEY = "steps";
    private static final String RESULT_KEY = "result";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_MESSAGE_KEY = "error_message";
    private static final String NAME_KEY = "name";

    /**
     * Genera il file CSV dai report di Cucumber.
     *
     * @throws IOException se c'è un errore di lettura/scrittura file
     * @throws IllegalStateException se il file di input non è valido
     */
    public static void generateCsv() throws IOException {
        try {
            LOGGER.info("Inizio generazione report CSV da: " + INPUT_FILE);

            Path inputPath = Paths.get(INPUT_FILE);
            Path outputPath = Paths.get(OUTPUT_FILE);

            if (!Files.exists(inputPath)) {
                LOGGER.warning("File input non trovato: " + INPUT_FILE);
                return;
            }

            List<Map<String, Object>> features = loadFeatures(inputPath);
            List<CsvRow> rows = extractFailedTests(features);

            writeCsv(outputPath, rows);

            LOGGER.info(String.format("CSV generato con successo in: %s (%d righe)",
                    outputPath.toAbsolutePath(), rows.size()));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante la generazione del CSV", e);
            throw new IOException("Errore durante la generazione del CSV", e);
        }
    }

    /**
     * Carica le features dal file JSON.
     */
    private static List<Map<String, Object>> loadFeatures(Path inputPath) throws IOException {
        try {
            return MAPPER.readValue(inputPath.toFile(), new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore durante il parsing del JSON, ritorno lista vuota", e);
            return Collections.emptyList();
        }
    }

    /**
     * Estrae i test falliti dalle features.
     */
    private static List<CsvRow> extractFailedTests(List<Map<String, Object>> features) {
        return features.stream()
                .flatMap(feature -> getScenarios(feature).stream())
                .filter(CucumberCsvReportGenerator::isFailed)
                .map(CucumberCsvReportGenerator::createCsvRow)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene gli scenari da una feature.
     */
    private static List<Map<String, Object>> getScenarios(Map<String, Object> feature) {
        Object elements = feature.get(ELEMENTS_KEY);
        return (elements instanceof List) ? (List<Map<String, Object>>) elements : Collections.emptyList();
    }

    /**
     * Verifica se uno scenario è fallito.
     */
    private static boolean isFailed(Map<String, Object> scenario) {
        return getSteps(scenario).stream()
                .map(step -> getResult(step))
                .filter(Objects::nonNull)
                .anyMatch(result -> STATUS_FAILED.equalsIgnoreCase(getString(result, STATUS_KEY)));
    }

    /**
     * Ottiene i steps da uno scenario.
     */
    private static List<Map<String, Object>> getSteps(Map<String, Object> scenario) {
        Object steps = scenario.get(STEPS_KEY);
        return (steps instanceof List) ? (List<Map<String, Object>>) steps : Collections.emptyList();
    }

    /**
     * Ottiene il result da uno step.
     */
    private static Map<String, Object> getResult(Map<String, Object> step) {
        Object result = step.get(RESULT_KEY);
        return (result instanceof Map) ? (Map<String, Object>) result : null;
    }

    /**
     * Crea una riga CSV dallo scenario fallito.
     */
    private static CsvRow createCsvRow(Map<String, Object> scenario) {
        String scenarioName = getString(scenario, NAME_KEY);
        String testId = extractTestId(scenarioName);
        String errorMessage = extractErrorMessage(scenario);

        return new CsvRow(testId, errorMessage);
    }

    /**
     * Estrae l'ID del test dal nome dello scenario.
     */
    private static String extractTestId(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        Matcher matcher = TEST_ID_PATTERN.matcher(name);
        return matcher.find() ? matcher.group(1) : name;
    }

    /**
     * Estrae il messaggio di errore dal primo step fallito.
     */
    private static String extractErrorMessage(Map<String, Object> scenario) {
        return getSteps(scenario).stream()
                .map(step -> getResult(step))
                .filter(Objects::nonNull)
                .filter(result -> STATUS_FAILED.equalsIgnoreCase(getString(result, STATUS_KEY)))
                .map(result -> result.get(ERROR_MESSAGE_KEY))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(error -> error.replaceAll("[\\n\\r]+", " "))
                .findFirst()
                .orElse("");
    }

    /**
     * Ottiene un valore string da una mappa.
     */
    private static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : "";
    }

    /**
     * Scrive il CSV su file.
     */
    private static void writeCsv(Path outputPath, List<CsvRow> rows) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(String.join(",", CSV_HEADERS));

        for (int i = 0; i < rows.size(); i++) {
            lines.add(rows.get(i).toCsvLine(i + 1));
        }

        Files.write(outputPath, lines);
    }

    /**
     * Classe interna per rappresentare una riga CSV.
     */
    private static class CsvRow {
        private final String testId;
        private final String errorMessage;

        CsvRow(String testId, String errorMessage) {
            this.testId = testId;
            this.errorMessage = errorMessage;
        }

        String toCsvLine(int index) {
            String[] values = {
                    String.valueOf(index),
                    testId,
                    errorMessage,
                    "", "", "", "", "", ""
            };
            return String.join(",", Arrays.stream(values)
                    .map(CsvRow::escapeCsv)
                    .toArray(String[]::new));
        }

        private static String escapeCsv(String value) {
            if (value == null) value = "";
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
    }
}
