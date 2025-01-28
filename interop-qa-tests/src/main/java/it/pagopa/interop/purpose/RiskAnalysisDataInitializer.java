package it.pagopa.interop.purpose;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.purpose.domain.RiskAnalysisDataFromJson;
import it.pagopa.interop.purpose.exception.RiskAnalysisDataInitializationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.Value;

@Value
public class RiskAnalysisDataInitializer {
    Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> riskAnalysisData;

    public RiskAnalysisDataInitializer() {
        this.riskAnalysisData = initializeMap();
    }

    private Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> initializeMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, RiskAnalysisDataFromJson.RiskAnalysisTemplate> ongoingRiskAnalysisData = null;
        try {
            ongoingRiskAnalysisData = objectMapper.readValue(
                new File("src/main/resources/risk_analysis_data.json"),
                new TypeReference<>() {}
            );
        } catch (IOException e) {
            throw new RiskAnalysisDataInitializationException("Error while reading risk analysis data from file", e);
        }
        return ongoingRiskAnalysisData;
    }
}
