package it.pagopa.interop.purpose.domain;

import lombok.Data;

import java.util.UUID;

public class PartialPurposeEServiceSeed extends TEServiceMode {
    private final UUID riskAnalysisId;

    public PartialPurposeEServiceSeed(UUID eserviceId, UUID consumerId, UUID riskAnalysisId) {
        super(eserviceId, consumerId);
        this.riskAnalysisId = riskAnalysisId;
    }

    public UUID getRiskAnalysisId() {
        return riskAnalysisId;
    }
}
