package it.pagopa.pn.client.b2b.pa.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TemplateEngineMessageConfigs {

    private Map<String, Map<String, LocalizedText>> messages = new HashMap<>();

    @Data
    public static class LocalizedText {
        private String it;
        private String de;
        private String fr;
        private String sl;
        private String en;
    }
}

