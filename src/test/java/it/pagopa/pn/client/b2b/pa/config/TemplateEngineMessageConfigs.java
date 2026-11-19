package it.pagopa.pn.client.b2b.pa.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TemplateEngineMessageConfigs {

    private Map<String, Map<String, LocalizedText>> messages = new HashMap<>();

    /**
     * Ogni lingua può essere definita nello yaml sia come singolo testo (retro-compatibile con i
     * template già esistenti) sia come lista di testi: in quest'ultimo caso, tutti i testi della
     * lista devono essere presenti nel documento generato (contains "AND" su tutti i valori).
     */
    @Data
    public static class LocalizedText {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> it;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> de;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> fr;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> sl;
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> en;
    }
}

