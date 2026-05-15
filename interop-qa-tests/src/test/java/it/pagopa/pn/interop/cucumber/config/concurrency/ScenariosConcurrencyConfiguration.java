package it.pagopa.pn.interop.cucumber.config.concurrency;


import it.pagopa.pn.interop.cucumber.config.concurrency.writer.ScenariosConcurrencyReportWriter;
import it.pagopa.pn.interop.cucumber.config.concurrency.writer.ScenariosConcurrencyStdoutReportWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ScenariosConcurrencyConfiguration {

    @Bean
    public ScenariosConcurrencyManager scenariosConcurrencyManager() {
        ScenariosConcurrencyManager scenariosConcurrencyManager = new ScenariosConcurrencyManager();
        scenariosConcurrencyManager.register("exp", "@exp_vincolato", 1);
        scenariosConcurrencyManager.register("exp2", "@exp_vincolato2", 1);
        return scenariosConcurrencyManager;
    }

    @Bean
    public ScenariosConcurrencyAuditor concurrencyAuditor() {
        return new ScenariosConcurrencyAuditor();
    }

    @Bean
    public ScenariosConcurrencyReporter reportGenerator(ScenariosConcurrencyAuditor auditor, List<ScenariosConcurrencyReportWriter> writers) {
        return new ScenariosConcurrencyReporter(auditor, writers);
    }

    @Bean
    public ScenariosConcurrencyStdoutReportWriter stdoutReportWriter() {
        return new ScenariosConcurrencyStdoutReportWriter();
    }

}
