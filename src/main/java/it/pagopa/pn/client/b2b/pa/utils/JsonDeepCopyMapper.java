package it.pagopa.pn.client.b2b.pa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Converte un oggetto in un'istanza di una classe "gemella" (stessa forma JSON, tipo Java diverso),
 * serializzando e deserializzando tramite Jackson. Usato per mappare i DTO generati da un'openapi
 * verso i DTO equivalenti generati da un'altra openapi (es. b2b-pg-external vs internal).
 */
public final class JsonDeepCopyMapper {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonDeepCopyMapper() {
    }

    public static <T> T deepCopy(Object obj, Class<T> toClass) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            return OBJECT_MAPPER.readValue(json, toClass);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException(
                    "Unable to deep copy object of type " + (obj == null ? "null" : obj.getClass().getName()) + " to " + toClass.getName(),
                    exc
            );
        }
    }
}
