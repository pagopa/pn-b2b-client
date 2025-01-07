package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Value;

@Value
public class RiskAnalysis {
    String name;
    RiskAnalysisFormSeed riskAnalysisForm;
}
