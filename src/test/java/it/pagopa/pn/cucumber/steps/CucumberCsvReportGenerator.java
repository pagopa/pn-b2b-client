package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CucumberCsvReportGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern TEST_ID_PATTERN = Pattern.compile("(\\[.*?\\])");

    public static void generateCsv() throws Exception {

        File input = new File("target/cucumber-report-merged.json");
        File output = new File("target/cucumber-report.csv");

        List<Map<String, Object>> features = load(input);

        List<String[]> rows = new ArrayList<>();

        int counter = 1;

        for (Map<String, Object> feature : features) {
            List<Map<String, Object>> scenarios = (List<Map<String, Object>>) feature.get("elements");
            if (scenarios == null) continue;

            for (Map<String, Object> scenario : scenarios) {

                if (!isFailed(scenario)) continue;

                String scenarioName = (String) scenario.get("name");

                String testId = extractTestId(scenarioName);
                String errorMessage = extractErrorMessage(scenario);

                rows.add(new String[]{
                        String.valueOf(counter++), // #
                        testId,                    // ID TEST
                        errorMessage,              // ERROR LOG
                        "", "", "", "", "", ""     // altre colonne vuote
                });
            }
        }

        writeCsv(output, rows);

        System.out.println("CSV generato in: " + output.getAbsolutePath());
    }

    private static List<Map<String, Object>> load(File f) throws Exception {
        if (!f.exists() || f.length() == 0) return new ArrayList<>();
        return MAPPER.readValue(f, new TypeReference<>() {});
    }

    private static boolean isFailed(Map<String, Object> scenario) {
        List<Map<String, Object>> steps = (List<Map<String, Object>>) scenario.get("steps");
        if (steps == null) return false;

        for (Map<String, Object> step : steps) {
            Map<String, Object> result = (Map<String, Object>) step.get("result");
            if (result == null) continue;

            String status = (String) result.get("status");
            if ("failed".equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private static String extractTestId(String name) {
        if (name == null) return "";

        Matcher matcher = TEST_ID_PATTERN.matcher(name);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return name;
    }

    private static String extractErrorMessage(Map<String, Object> scenario) {

        List<Map<String, Object>> steps = (List<Map<String, Object>>) scenario.get("steps");
        if (steps == null) return "";

        for (Map<String, Object> step : steps) {
            Map<String, Object> result = (Map<String, Object>) step.get("result");
            if (result == null) continue;

            if ("failed".equalsIgnoreCase((String) result.get("status"))) {
                Object error = result.get("error_message");
                if (error != null) {
                    return error.toString().replace("\n", " ").replace("\r", " ");
                }
            }
        }
        return "";
    }

    private static void writeCsv(File file, List<String[]> rows) throws Exception {

        try (FileWriter writer = new FileWriter(file)) {

            // Header
            writer.write("#,ID TEST,ERROR LOG,KEYS,COMPONENTS,RESULT,PROCEDURE,BUG SUITE,BUG PRODOTTO\n");

            for (String[] row : rows) {
                writer.write(String.join(",", escape(row)) + "\n");
            }
        }
    }

    private static String[] escape(String[] row) {
        return Arrays.stream(row)
                .map(value -> "\"" + value.replace("\"", "\"\"") + "\"")
                .toArray(String[]::new);
    }
}