package it.pagopa.pn.client.b2b.pa.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "messages", ignoreUnknownFields = false)
@Data
public class PnB2bClientTemplateEngineConfigs {

    @Data
    public static class LocalizedText {
        private String it;
        private String de;
        private String fr;
        private String sl;
    }

    @Data
    public static class RecypientType {
        private String recypientType;
    }

    private Map<String, Map<RecypientType, Map<LocalizedText, String>>> messages = new HashMap<>();
}

