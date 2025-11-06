package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RiskAnalysis {
    String name;
    RiskAnalysisFormSeed riskAnalysisForm;
}
