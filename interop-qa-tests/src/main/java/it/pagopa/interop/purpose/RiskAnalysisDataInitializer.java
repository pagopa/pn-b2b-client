package it.pagopa.interop.purpose;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.purpose.domain.RiskAnalysisDataFromJson;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Getter
public class RiskAnalysisDataInitializer {
    private final Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> riskAnalysisData;

    public RiskAnalysisDataInitializer(Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> riskAnalysisData) {
        this.riskAnalysisData = initializeMap();
    }

    private Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> initializeMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> riskAnalysisData = null;
        try {
            riskAnalysisData = objectMapper.readValue(new File("src/main/resources/risk_analysis_data.json"),
                    new TypeReference<Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return riskAnalysisData;
    }
}
