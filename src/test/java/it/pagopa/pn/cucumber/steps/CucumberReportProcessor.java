package it.pagopa.pn.cucumber.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.util.*;

public class CucumberReportProcessor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void process() throws Exception {

        File reportA = new File("target/cucumber-report.json");
        File reportB = new File("target/cucumber-report-rerun.json");
        File mergedFile = new File("target/cucumber-report-merged.json");

        List<Map<String, Object>> featuresA = load(reportA);
        List<Map<String, Object>> featuresB = load(reportB);

        Map<String, Map<String, Object>> scenarioMap = new LinkedHashMap<>();

        mergeFeatures(featuresA, scenarioMap);
        mergeFeatures(featuresB, scenarioMap);

        // Ricostruisci le feature
        List<Map<String, Object>> mergedFeatures = rebuildFeatures(scenarioMap);

        MAPPER.writerWithDefaultPrettyPrinter().writeValue(mergedFile, mergedFeatures);

        System.out.println("Merged JSON creato");

        generateHtml(mergedFile);
    }

    private static void mergeFeatures(List<Map<String, Object>> features,
                                      Map<String, Map<String, Object>> scenarioMap) {

        for (Map<String, Object> feature : features) {

            String uri = (String) feature.get("uri");
            List<Map<String, Object>> elements = (List<Map<String, Object>>) feature.get("elements");

            if (elements == null) continue;

            for (Map<String, Object> scenario : elements) {

                String scenarioName = (String) scenario.get("name");
                String key = uri + "||" + scenarioName;

                boolean isPassed = isScenarioPassed(scenario);

                if (!scenarioMap.containsKey(key)) {
                    scenarioMap.put(key, scenario);
                } else {
                    Map<String, Object> existing = scenarioMap.get(key);

                    boolean alreadyPassed = isScenarioPassed(existing);

                    // 🔥 REGOLA IMPORTANTE
                    if (!alreadyPassed && isPassed) {
                        scenarioMap.put(key, scenario); // override con versione PASSATA
                    }
                }
            }
        }
    }

    private static boolean isScenarioPassed(Map<String, Object> scenario) {

        List<Map<String, Object>> steps = (List<Map<String, Object>>) scenario.get("steps");
        if (steps == null) return false;

        for (Map<String, Object> step : steps) {
            Map<String, Object> result = (Map<String, Object>) step.get("result");
            if (result == null) return false;

            String status = (String) result.get("status");

            if (!"passed".equalsIgnoreCase(status)) {
                return false;
            }
        }

        return true;
    }

    private static List<Map<String, Object>> rebuildFeatures(Map<String, Map<String, Object>> scenarioMap) {

        Map<String, Map<String, Object>> featureMap = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, Object>> entry : scenarioMap.entrySet()) {

            String key = entry.getKey();
            Map<String, Object> scenario = entry.getValue();

            String uri = key.split("\\|\\|")[0];

            featureMap.putIfAbsent(uri, new LinkedHashMap<>());
            Map<String, Object> feature = featureMap.get(uri);

            feature.put("uri", uri);
            feature.putIfAbsent("elements", new ArrayList<>());

            ((List<Map<String, Object>>) feature.get("elements")).add(scenario);
        }

        return new ArrayList<>(featureMap.values());
    }

    private static List<Map<String, Object>> load(File f) throws Exception {
        if (!f.exists() || f.length() == 0) return new ArrayList<>();
        return MAPPER.readValue(f, new TypeReference<>() {});
    }

    private static void generateHtml(File mergedJson) {
        File outputDir = new File("target/cucumber-html-reports");

        Configuration config = new Configuration(outputDir, "PN B2B Tests");

        ReportBuilder reportBuilder = new ReportBuilder(
                Collections.singletonList(mergedJson.getAbsolutePath()),
                config
        );

        reportBuilder.generateReports();
    }
}