package it.pagopa.pn.cucumber;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.FilePosition;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static org.junit.platform.launcher.EngineFilter.includeEngines;

public class RerunFailedTestSuite {

    @Test
    void runFailedScenarios() throws Exception {
        Path failedFile = Paths.get("target/failed.txt");
        List<String> lines = Files.readAllLines(failedFile);

        List<DiscoverySelector> selectors = new ArrayList<>();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Formato atteso dallo script jq: classpath:it/pagopa/pn/cucumber/File.feature:15
            String cleanPath = trimmed.replaceFirst("^classpath:", "");
            int colonIdx = cleanPath.lastIndexOf(':');

            if (colonIdx > 0) {
                String resourcePath = cleanPath.substring(0, colonIdx);
                int lineNumber = Integer.parseInt(cleanPath.substring(colonIdx + 1));
                selectors.add(DiscoverySelectors.selectClasspathResource(
                        resourcePath,
                        FilePosition.from(lineNumber)
                ));
            } else {
                selectors.add(DiscoverySelectors.selectClasspathResource(cleanPath));
            }
        }

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectors)
                .filters(includeEngines("cucumber"))
                .configurationParameter(GLUE_PROPERTY_NAME, "it.pagopa.pn.cucumber.steps")
                .configurationParameter(PLUGIN_PROPERTY_NAME,
                        "pretty,json:target/cucumber-report-rerun.json,html:target/cucumber-report-rerun.html,it.pagopa.pn.cucumber.steps.RerunGuardPlugin")
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        if (summary.getTotalFailureCount() > 0) {
            summary.getFailures().forEach(failure -> {
                System.err.println("Test fallito: " + failure.getTestIdentifier().getDisplayName());
                System.err.println("Motivo: " + failure.getException().getMessage());
            });
        }

    }
}
