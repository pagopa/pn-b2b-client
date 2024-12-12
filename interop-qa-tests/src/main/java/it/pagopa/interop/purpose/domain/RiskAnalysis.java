package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import lombok.Data;

@Data
public class RiskAnalysis {
    private final String name;
    private final RiskAnalysisForm riskAnalysisForm;
}
