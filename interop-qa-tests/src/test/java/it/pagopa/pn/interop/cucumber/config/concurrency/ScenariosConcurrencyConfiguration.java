package it.pagopa.pn.interop.cucumber.config.concurrency;


import it.pagopa.pn.interop.cucumber.config.concurrency.writer.ScenariosConcurrencyReportWriter;
import it.pagopa.pn.interop.cucumber.config.concurrency.writer.ScenariosConcurrencyStdoutReportWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ScenariosConcurrencyConfiguration {

    private final ScenariosConcurrencyProperties properties;

    @Bean
    public ScenariosConcurrencyManager scenariosConcurrencyManager() {
        ScenariosConcurrencyManager manager = new ScenariosConcurrencyManager();

        if (properties.getGroups() != null) {
            properties.getGroups().forEach((name, config) -> {
                manager.register(name, config.getTag(), config.getSlots());
            });
        }

        return manager;
    }

    @Bean
    public ScenariosConcurrencyAuditor concurrencyAuditor() {
        return new ScenariosConcurrencyAuditor();
    }

    @Bean
    public ScenariosConcurrencyReporter scenariosConcurrencyReporter(
            ScenariosConcurrencyAuditor auditor,
            List<ScenariosConcurrencyReportWriter> writers) {
        return new ScenariosConcurrencyReporter(auditor, writers, properties.getReport().getBucketMs());
    }

    @Bean
    @Order(1)
    public ScenariosConcurrencyReportWriter stdoutReportWriter() {
        return new ScenariosConcurrencyStdoutReportWriter();
    }

}
