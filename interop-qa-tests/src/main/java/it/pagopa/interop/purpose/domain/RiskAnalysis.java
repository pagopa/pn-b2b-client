package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Data;

@Data
public class RiskAnalysis {
    private final String name;
    private final RiskAnalysisFormSeed riskAnalysisForm;
}
