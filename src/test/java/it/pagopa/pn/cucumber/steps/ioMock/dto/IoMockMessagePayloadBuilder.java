package it.pagopa.pn.cucumber.steps.ioMock.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class IoMockMessagePayloadBuilder {

    private String fiscalCode = "RSSMRA80A01H5010";
    private String featureLevelType = "ADVANCED";
    private String subject = "Comunicazione istituzionale @io:OK_READ_THEN_PAID";
    private String markdown = "# Messaggio PagoPA\nTesto della notifica...";
    private Map<String, Object> paymentData = null;
    private final Map<String, Object> extraRootFields = new HashMap<>();
    private final Map<String, Object> extraContentFields = new HashMap<>();

    private boolean includeFiscalCode = true;
    private boolean includeFeatureLevelType = true;
    private boolean includeContent = true;
    private boolean includeSubject = true;
    private boolean includeMarkdown = true;

    public static IoMockMessagePayloadBuilder builder() {
        return new IoMockMessagePayloadBuilder();
    }

    public IoMockMessagePayloadBuilder withFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
        this.includeFiscalCode = true;
        return this;
    }

    public IoMockMessagePayloadBuilder withSequence(String sequenceName) {
        this.subject = "Comunicazione istituzionale @io:" + sequenceName;
        return this;
    }

    public IoMockMessagePayloadBuilder withSubject(String subject) {
        this.subject = subject;
        this.includeSubject = true;
        return this;
    }

    public IoMockMessagePayloadBuilder withMarkdown(String markdown) {
        this.markdown = markdown;
        this.includeMarkdown = true;
        return this;
    }

    public IoMockMessagePayloadBuilder withFeatureLevelType(String featureLevelType) {
        this.featureLevelType = featureLevelType;
        this.includeFeatureLevelType = true;
        return this;
    }

    public IoMockMessagePayloadBuilder withPaymentData(Map<String, Object> paymentData) {
        this.paymentData = paymentData;
        return this;
    }

    public IoMockMessagePayloadBuilder withExtraField(String key, Object value) {
        this.extraRootFields.put(key, value);
        return this;
    }

    public IoMockMessagePayloadBuilder withExtraContentField(String key, Object value) {
        this.extraContentFields.put(key, value);
        return this;
    }

    public IoMockMessagePayloadBuilder withoutField(String fieldName) {
        if ("fiscal_code".equalsIgnoreCase(fieldName)) {
            this.includeFiscalCode = false;
        } else if ("feature_level_type".equalsIgnoreCase(fieldName)) {
            this.includeFeatureLevelType = false;
        } else if ("content".equalsIgnoreCase(fieldName)) {
            this.includeContent = false;
        }
        return this;
    }

    public IoMockMessagePayloadBuilder withoutContentField(String subFieldName) {
        if ("subject".equalsIgnoreCase(subFieldName)) {
            this.includeSubject = false;
        } else if ("markdown".equalsIgnoreCase(subFieldName)) {
            this.includeMarkdown = false;
        }
        return this;
    }

    public Map<String, Object> buildMap() {
        Map<String, Object> payload = new HashMap<>();

        if (includeFiscalCode) {
            payload.put("fiscal_code", fiscalCode);
        }
        if (includeFeatureLevelType) {
            payload.put("feature_level_type", featureLevelType);
        }

        if (includeContent) {
            Map<String, Object> contentMap = new HashMap<>();
            if (includeSubject) {
                contentMap.put("subject", subject);
            }
            if (includeMarkdown) {
                contentMap.put("markdown", markdown);
            }
            contentMap.put("payment_data", paymentData);
            contentMap.putAll(extraContentFields);

            payload.put("content", contentMap);
        }

        payload.putAll(extraRootFields);
        return payload;
    }

    public String buildJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(buildMap());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Errore durante la serializzazione JSON del payload NewMessage", e);
        }
    }
}
