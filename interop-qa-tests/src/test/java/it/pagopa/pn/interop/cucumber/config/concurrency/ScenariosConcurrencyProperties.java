package it.pagopa.pn.interop.cucumber.config.concurrency;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "scenarios.concurrency")
public class ScenariosConcurrencyProperties {

    /**
     * Corrisponde a scenarios.concurrency.report.bucket-ms
     */
    private ReportConfig report = new ReportConfig();

    /**
     * Corrisponde a scenarios.concurrency.groups.*
     */
    private Map<String, GroupConfig> groups;

    @Data
    public static class ReportConfig {
        // Valore di default se non specificato nel file properties
        private long bucketMs = 1000;
    }

    @Data
    public static class GroupConfig {
        private String tag;
        private int slots;
    }
}