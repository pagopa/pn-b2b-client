package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Builder
public class TEServiceMode {
    private final UUID eserviceId;
    private final UUID consumerId;
    private final String title;
    private final String description;
    private final boolean isFreeOfCharge;
    private final String freeOfChargeReason;
    private final int dailyCalls;

    private final UUID riskAnalysisId;
    private final RiskAnalysisFormSeed riskAnalysisFormSeed;

}
